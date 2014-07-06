package auth.twitter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import utils.LoginUtils;

import com.sap.security.um.user.PersistenceException;

import entities.LoggedUser;
import exposed.LoggedUserExposed;

/**
 * Servlet implementation class TwitterCallback
 */
public class TwitterCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Twitter twitter = (Twitter) request.getSession()
				.getAttribute("twitter");
		if (request.getParameter("denied") != null) {
			twitter = null;
			request.getSession().setAttribute("twitter", null);
			response.sendRedirect(request.getContextPath() + "/");
		}
		RequestToken requestToken = (RequestToken) request.getSession()
				.getAttribute("requestToken");
		String verifier = request.getParameter("oauth_verifier");
		try {
			twitter.getOAuthAccessToken(requestToken, verifier);
			request.getSession().removeAttribute("requestToken");
			AccessToken oAuthAccessToken = twitter.getOAuthAccessToken();
			String token = oAuthAccessToken.getToken();
			String tokenSecret = oAuthAccessToken.getTokenSecret();
			LoggedUserExposed pe = new LoggedUserExposed();
			LoggedUser p = pe.findPersonByTwitterId(token, tokenSecret);
			LoggedUser plocal = pe.getCurrentUser(request);

			if (p != null && plocal != null) {
				pe.deleteEntity(plocal);
				LoginUtils.invalidateCookie(request);
				LoginUtils.createCookie(response, p.getId()+"");
			}

			if (p == null) {
				p = plocal;
			}

			if (p == null) {
//				FIXME add login here
//				p = (new UserService()).initLogin(request, response, token, tokenSecret);
			} else {
				p.settAccess(token);
				p.settSAccess(tokenSecret);
				pe.updateEntity(p);
			}

		} catch (TwitterException e) {
			throw new ServletException(e);
		}
		response.sendRedirect(request.getContextPath() + "/index1.html");
	}

}
