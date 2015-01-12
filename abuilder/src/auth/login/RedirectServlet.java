package auth.login;

import gamification.ExecuteAction;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.jwt.JWT;
import org.apache.oltu.oauth2.jwt.io.JWTClaimsSetWriter;
import org.apache.oltu.oauth2.jwt.io.JWTHeaderWriter;
import org.apache.oltu.openidconnect.client.response.OpenIdConnectResponse;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.UserInfoJson;
import persistency.exposed.LoggedUserExposed.FacebookUserInfoJson;
import persistency.exposed.LoggedUserExposed.LinkedInUserInfoJson;
import service.rest.wrappers.OidClaimSetJsonObject;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import utils.LoginUtils;
import auth.openidconnect.ApplicationException;
import auth.openidconnect.OAuthParams;
import auth.openidconnect.ProviderData;
import auth.openidconnect.Utils;

import com.google.gson.Gson;

/**
 * Servlet implementation class RedirectServlet
 */
public class RedirectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedUser user = processRequest(request, response);
		ExecuteAction.getInstance().execute("login", user, null);
	}

	private LoggedUser processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		LoggedUser user = null;
		if(isAlreadyAuthenticatedThroughClient(request)){
			return authThroughClient(request, response);
		} else {
			Twitter twitter = (Twitter) request.getSession().getAttribute(Utils.TWITTER);
			String provider = null;
			if(twitter != null){
				provider = Utils.TWITTER;
				RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
				String verifier = request.getParameter("oauth_verifier");
				if(verifier == null){
					(new Logout()).performLocalInfoRemove(request, response);
					response.sendRedirect("/");
				} else {
					try {
						AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, verifier);
						request.getSession().removeAttribute("requestToken");
						user = initOrCreateUserTwitter(request, response, twitter, authAccessToken);
					} catch (TwitterException e) {
						(new Logout()).performLocalInfoRemove(request, response);
						response.sendRedirect("/");
						//					throw new ServletException(e);
					}
				}
			} else{
				OAuthParams oauthParams = new OAuthParams();
				try {
					// Get OAuth Info
					String clientId = LoginUtils.findCookieValue(request, "clientId");
					String clientSecret = LoginUtils.findCookieValue(request, "clientSecret");
					String authzEndpoint = LoginUtils.findCookieValue(request, "authzEndpoint");
					String tokenEndpoint = LoginUtils.findCookieValue(request, "tokenEndpoint");
					String redirectUri = LoginUtils.findCookieValue(request, "redirectUri");
					String scope = LoginUtils.findCookieValue(request, "scope");
					String state = LoginUtils.findCookieValue(request, "state");


					oauthParams.setClientId(clientId);
					oauthParams.setClientSecret(clientSecret);
					oauthParams.setAuthzEndpoint(authzEndpoint);
					oauthParams.setTokenEndpoint(tokenEndpoint);
					oauthParams.setRedirectUri(redirectUri);
					oauthParams.setScope(Utils.isIssued(scope));
					oauthParams.setState(Utils.isIssued(state));

					// Create the response wrapper
					OAuthAuthzResponse oar = null;
					oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);

					// Get Authorization Code
					String code = oar.getCode();
					oauthParams.setAuthzCode(code);

					String app = LoginUtils.findCookieValue(request, "app");
					response.addCookie(new Cookie("app", app));
					provider = app;
					oauthParams.setApplication(app);
					user = initToken(oauthParams, request, response);
				} catch (OAuthProblemException e) {
					StringBuffer sb = new StringBuffer();
					sb.append("</br>");
					sb.append("Error code: ").append(e.getError()).append("</br>");
					sb.append("Error description: ").append(e.getDescription()).append("</br>");
					sb.append("Error uri: ").append(e.getUri()).append("</br>");
					sb.append("State: ").append(e.getState()).append("</br>");
					oauthParams.setErrorMessage(sb.toString());
//					throw new ServletException(sb.toString(), e);
					e.printStackTrace();
					response.sendRedirect("/");
				} catch (OAuthSystemException e) {
					e.printStackTrace();
					response.sendRedirect("/");
//					throw new ServletException(e);
				}
			}
			createLoginCookie(response, provider);
			response.sendRedirect(request.getContextPath() + "/");
		}
		return user;
	}

	private void createLoginCookie(HttpServletResponse response, String provider) {
		response.addCookie(new Cookie(Utils.COOKIE_PROVIDER_NAME, provider));

	}

	private LoggedUser initOrCreateUserTwitter(HttpServletRequest request,
			HttpServletResponse response, Twitter twitter, AccessToken authAccessToken) throws TwitterException, IOException {
		String screenName = twitter.getScreenName();
//		long id = twitter.getId();
		UserInfoJson result = new UserInfoJson();
		result.setEmail(screenName);
		result.setName(screenName+"");
		result.setAccessToken(authAccessToken.getToken());
		result.setSecretAccessToken(authAccessToken.getTokenSecret());
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser findPersonByOpenId = lue.createNewUser(Utils.TWITTER, result);
		//backup 60 min
		findPersonByOpenId.setSessionExpires(System.currentTimeMillis()+60*60*1000);
		lue.updateEntity(findPersonByOpenId);
		LoginUtils.createCookie(response, Utils.TWITTER);
		request.getSession().setAttribute(Utils.ACCESS_TOKEN_SESSION_KEY, findPersonByOpenId.getAccessToken());
		return findPersonByOpenId;
	}

	private LoggedUser initToken(OAuthParams oauthParams,
			HttpServletRequest req, HttpServletResponse response) throws OAuthSystemException, IOException, ServletException {

		try {

			Utils.validateTokenParams(oauthParams, req.getServerName());

			OAuthClientRequest request = OAuthClientRequest
					.tokenLocation(oauthParams.getTokenEndpoint())
					.setClientId(oauthParams.getClientId())
					.setClientSecret(oauthParams.getClientSecret())
					.setRedirectURI(oauthParams.getRedirectUri())
					.setCode(oauthParams.getAuthzCode())
					.setGrantType(GrantType.AUTHORIZATION_CODE).setParameter(OAuth.OAUTH_ACCESS_TOKEN, oauthParams.getAccessToken())
					.buildBodyMessage();

			URLConnectionClient httpClient = new URLConnectionClient();
			OAuthClient client = new OAuthClient(httpClient);
			String app = LoginUtils.findCookieValue(req, "app");

			OAuthAccessTokenResponse oauthResponse = null;
			Class<? extends OAuthAccessTokenResponse> cl = OAuthJSONAccessTokenResponse.class;

			if (Utils.FACEBOOK.equalsIgnoreCase(app)) {
				cl = GitHubTokenResponse.class;
			} else if (Utils.GOOGLE.equalsIgnoreCase(app)){
				cl = OpenIdConnectResponse.class;
			}
//			initCACerts(app, req);
			oauthResponse = client.accessToken(request, cl);

			oauthParams.setAccessToken(oauthResponse.getAccessToken());

			//dirty workaround
			if(Utils.FACEBOOK.equalsIgnoreCase(app)){
				String[] bodySplit = oauthResponse.getBody().split("&");
				if(bodySplit != null){
					for (String line : bodySplit) {
						if(line != null && line.startsWith("expires=")){
							oauthParams.setExpiresIn(Long.parseLong(line.substring("expires=".length()))*1000);
							break;
						}
					}
				}

			} else {
				oauthParams.setExpiresIn(oauthResponse.getExpiresIn());
			}

			oauthParams.setRefreshToken(Utils.isIssued(oauthResponse.getRefreshToken()));

			if (Utils.GOOGLE.equalsIgnoreCase(app)){
				fetchUserDataFromGoogle(oauthParams, oauthResponse);
			}
			return initOrCreateUser(req, response, oauthParams);
		} catch (ApplicationException e) {
			oauthParams.setErrorMessage(e.getMessage());
			throw new ServletException(e);
		} catch (OAuthProblemException e) {
			StringBuffer sb = new StringBuffer();
			sb.append("</br>");
			sb.append("Error code: ").append(e.getError()).append("</br>");
			sb.append("Error description: ").append(e.getDescription()).append("</br>");
			sb.append("Error uri: ").append(e.getUri()).append("</br>");
			sb.append("State: ").append(e.getState()).append("</br>");
			oauthParams.setErrorMessage(sb.toString());
			throw new ServletException(sb.toString());
		}
	}
	
	
	private void initCACerts(String app, HttpServletRequest req) {
		KeyStore trustStore;
		try {
			trustStore = KeyStore.getInstance("JKS");
			trustStore.load(req.getServletContext().getResourceAsStream(app+".jks"), "123456".toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			KeyManager[] kms = kmf.getKeyManagers();

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustStore);
			TrustManager[] tms = tmf.getTrustManagers();

			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kms, tms, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void fetchUserDataFromGoogle(OAuthParams oauthParams,
			OAuthAccessTokenResponse oauthResponse)
					throws MalformedURLException, ServletException {
		OpenIdConnectResponse openIdConnectResponse = ((OpenIdConnectResponse)oauthResponse);
		JWT idToken = openIdConnectResponse.getIdToken();
		oauthParams.setIdToken(idToken.getRawString());

		oauthParams.setHeader(new JWTHeaderWriter().write(idToken.getHeader()));
		oauthParams.setClaimsSet(new JWTClaimsSetWriter().write(idToken.getClaimsSet()));

		URL url = new URL(oauthParams.getTokenEndpoint());

		oauthParams.setIdTokenValid(openIdConnectResponse.checkId(url.getHost(), oauthParams.getClientId()));
		if(!oauthParams.isIdTokenValid()){
			throw new ServletException("Failed to authenticate");
		}
	}

	private LoggedUser initOrCreateUser(HttpServletRequest request,
			HttpServletResponse response, OAuthParams oauthParams) throws IOException {
		LoggedUserExposed lue = new LoggedUserExposed();
		Gson g = new Gson();
		LoggedUser findPersonByOpenId = null;
		OidClaimSetJsonObject fromJson = null;
		if (oauthParams.getClaimsSet() != null){
			fromJson = g.fromJson(oauthParams.getClaimsSet(), OidClaimSetJsonObject.class);
			findPersonByOpenId = lue.findPersonByOpenId(lue.assebleOpenId(fromJson.getEmail(), oauthParams.getApplication()));
		}
		if(findPersonByOpenId == null){
			String userInfoString = null;
			String userEmail = null;
			try {
				ProviderData data = Utils.getProvider(oauthParams.getApplication(), request);
				userInfoString = getUserInfoString(oauthParams, data.getUserInfoEndpoint());
				if(data.getUserEmailEndpoint() != null){
					userEmail = getUserInfoString(oauthParams, data.getUserEmailEndpoint());
				}
			} catch (OAuthSystemException e) {
				//NO user info can be retrieved
				e.printStackTrace();
			}
			UserInfoJson userInfoJson = null;
			if(userInfoString != null && userInfoString.length() > 0){
				if(oauthParams.getApplication().equalsIgnoreCase(Utils.GOOGLE)){
					userInfoJson = g.fromJson(userInfoString, UserInfoJson.class);
//					if(userInfoJson.getEmail() == null && userInfoJson.getId() != null){
//						userInfoJson.setEmail(userInfoJson.getId());
//					}
				} else if(oauthParams.getApplication().equalsIgnoreCase(Utils.LINKEDIN)){
					userInfoJson = assembleUserInfo(g, userInfoString, userEmail);
				} else if(oauthParams.getApplication().equalsIgnoreCase(Utils.FACEBOOK)){
					userInfoJson = assembleUserInfoFromFacebook(g, userInfoString);
				}
			}
			userInfoJson.setAccessToken(oauthParams.getAccessToken());
			if(userInfoJson.getEmail()== null){
				if(request.getHeader("email") != null && !request.getHeader("email").equals("dimitar.giormov@gmail.com")){
					userInfoJson.setEmail(request.getHeader("email"));	
				}
			}
			findPersonByOpenId = lue.createNewUser(oauthParams.getApplication(), userInfoJson);
		}
		if(fromJson != null){
			findPersonByOpenId.setSessionExpires(System.currentTimeMillis() + Long.parseLong(fromJson.getExp()));
		} else if(oauthParams.getExpiresIn() != null) {
			findPersonByOpenId.setSessionExpires(System.currentTimeMillis() + oauthParams.getExpiresIn());
		} else {
			//backup 60 min
			findPersonByOpenId.setSessionExpires(System.currentTimeMillis() + 60*60*1000);
		}
		findPersonByOpenId.setAccessToken(oauthParams.getAccessToken());
		LoginUtils.createCookie(response, oauthParams.getApplication());
		lue.updateEntity(findPersonByOpenId);
		request.getSession().setAttribute(Utils.ACCESS_TOKEN_SESSION_KEY, findPersonByOpenId.getAccessToken());
		return findPersonByOpenId;
	}

	private UserInfoJson assembleUserInfo(Gson g, String userInfoString,
			String userEmail) {
		LinkedInUserInfoJson userInfoJspon = g.fromJson(userInfoString, LinkedInUserInfoJson.class);
		UserInfoJson result = new UserInfoJson();
		result.setGiven_name(userInfoJspon.getFirstName());
		result.setFamili_name(userInfoJspon.getLastName());
		result.setEmail(userEmail);
		return result;
	}

	private UserInfoJson assembleUserInfoFromFacebook(Gson g, String userInfoString) {
		FacebookUserInfoJson userInfoJson = g.fromJson(userInfoString, FacebookUserInfoJson.class);
		UserInfoJson result = new UserInfoJson();
		result.setGiven_name(userInfoJson.getFirstName());
		result.setFamili_name(userInfoJson.getLastName());
		result.setName(userInfoJson.getName());
		result.setEmail(userInfoJson.getEmail());
		return result;
	}

	private String getUserInfoString(OAuthParams oauthParams, String userInfoEndpoint) throws OAuthSystemException {
		OAuthClientRequest request = OAuthClientRequest
				.tokenLocation(userInfoEndpoint)
				.setClientId(oauthParams.getClientId())
				.setClientSecret(oauthParams.getClientSecret())
				.setRedirectURI(oauthParams.getRedirectUri())
				.setCode(oauthParams.getAuthzCode())
				.setGrantType(GrantType.AUTHORIZATION_CODE).setParameter(OAuth.OAUTH_ACCESS_TOKEN, oauthParams.getAccessToken())
				.buildQueryMessage();
		URLConnection c = null;
		int responseCode = 0;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.URL_ENCODED);
		if(oauthParams.getApplication().equalsIgnoreCase(Utils.LINKEDIN)){
			headers.put(OAuth.HeaderType.AUTHORIZATION, "Bearer "+ oauthParams.getAccessToken());
			headers.put("x-li-format", "json");	
		}
		try {
			URL url = new URL(request.getLocationUri());

			c = url.openConnection();
			responseCode = -1;
			if (c instanceof HttpURLConnection) {
				HttpURLConnection httpURLConnection = (HttpURLConnection)c;

				if (headers != null && !headers.isEmpty()) {
					for (Map.Entry<String, String> header : headers.entrySet()) {
						httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
					}
				}

				if (request.getHeaders() != null) {
					for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
						httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
					}
				}

				httpURLConnection.setRequestMethod(OAuth.HttpMethod.GET);

				httpURLConnection.connect();

				InputStream inputStream;
				responseCode = httpURLConnection.getResponseCode();
				if (responseCode == 400 || responseCode == 401) {
					inputStream = httpURLConnection.getErrorStream();
				} else {
					inputStream = httpURLConnection.getInputStream();
				}

				return OAuthUtils.saveStreamAsString(inputStream);
			}
		} catch (IOException e) {
			throw new OAuthSystemException(e);
		}
		return null;
	}

	private LoggedUser authThroughClient(HttpServletRequest request, HttpServletResponse response) throws IOException{
		OAuthParams oauthParams = new OAuthParams();
		oauthParams.setAccessToken(request.getHeader(Utils.ACCESS_TOKEN_SESSION_KEY));
		oauthParams.setApplication(request.getHeader("provider"));
		return initOrCreateUser(request, response, oauthParams);	
	}

	private boolean isAlreadyAuthenticatedThroughClient(HttpServletRequest request){
		return request.getHeader(Utils.ACCESS_TOKEN_SESSION_KEY) != null && request.getHeader("provider") != null; 
	}

}
