package auth.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import auth.openidconnect.ApplicationException;
import auth.openidconnect.OAuthParams;
import auth.openidconnect.ProviderData;
import auth.openidconnect.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import utils.Constants;
import utils.LoginUtils;

public class BeginAuthorization extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String providerName = request.getParameter("provider");
			if(providerName == null){
				response.sendError(400);
				return;
			}
			if(providerName.equals("auto")){
				String provider = LoginUtils.findCookieValue(request, Constants.COOKIE_PROVIDER_KEY);
				if(provider != null && provider.length() > 0){
					providerName = provider; 
				} else{
					response.sendError(400);
					return;
				}
			}
			String clientId = Utils.getClientId(providerName, request);
			if(clientId == null || clientId.length() == 0){
				response.sendError(401, "Login Provider is not configured");
				return;
			}
			if(providerName.equals(Utils.TWITTER)){
				authorizeWithTwitter(request, response);
			} else {
				authorize(initOAuthParams(providerName, request), request, response);	
			}
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void authorizeWithTwitter(HttpServletRequest request, HttpServletResponse response) throws TwitterException, IOException  {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Utils.getClientId(Utils.TWITTER, request), Utils.getClientSecret(Utils.TWITTER, request));
		request.getSession().setAttribute(Utils.TWITTER, twitter);
		StringBuffer callbackURL = request.getRequestURL();
		int index = callbackURL.lastIndexOf("/");
		callbackURL.replace(index, callbackURL.length(), "").append("/redirect");

		RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
		request.getSession().setAttribute("requestToken", requestToken);
		response.sendRedirect(requestToken.getAuthenticationURL());
	}

	private OAuthParams initOAuthParams(String providerName, HttpServletRequest request) throws OAuthProblemException{
		OAuthParams oauthParams = new OAuthParams();
		oauthParams.setApplication(providerName);
		ProviderData providerData = Utils.getProvider(providerName, request);
		oauthParams.setTokenEndpoint(providerData.getTokenEndpoint());
		oauthParams.setClientId(Utils.getClientId(providerName, request));
		oauthParams.setClientSecret(Utils.getClientSecret(providerName, request));
		oauthParams.setRedirectUri(Utils.getRedirectUri(request.getServerName()));
		oauthParams.setScope(providerData.getScope());
		oauthParams.setState(System.currentTimeMillis()+"");
		oauthParams.setAuthzCode("");
		oauthParams.setAuthzEndpoint(providerData.getAuthzEndpoint());
		return oauthParams;
	}


	public void authorize(OAuthParams oauthParams, HttpServletRequest req,  HttpServletResponse res) throws OAuthSystemException, IOException{
		try {
			Utils.validateAuthorizationParams(oauthParams, req.getServerName());

			res.addCookie(new Cookie("clientId", oauthParams.getClientId()));
			res.addCookie(new Cookie("clientSecret", oauthParams.getClientSecret()));
			res.addCookie(new Cookie("authzEndpoint", oauthParams.getAuthzEndpoint()));
			res.addCookie(new Cookie("tokenEndpoint", oauthParams.getTokenEndpoint()));
			res.addCookie(new Cookie("redirectUri", oauthParams.getRedirectUri()));
			res.addCookie(new Cookie("scope", oauthParams.getScope()));
			res.addCookie(new Cookie("state", oauthParams.getState()));
			res.addCookie(new Cookie("app", oauthParams.getApplication()));

			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation(oauthParams.getAuthzEndpoint())
					.setClientId(oauthParams.getClientId())
					.setRedirectURI(oauthParams.getRedirectUri())
					.setResponseType(ResponseType.CODE.toString())
					.setScope(oauthParams.getScope())
					.setState(oauthParams.getState())
					.buildQueryMessage();
			res.sendRedirect(request.getLocationUri());
		} catch (ApplicationException e) {
			oauthParams.setErrorMessage(e.getMessage());
		}
	}
}
