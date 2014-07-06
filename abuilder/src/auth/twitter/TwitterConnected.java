package auth.twitter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import entities.LoggedUser;
import exposed.LoggedUserExposed;

/**
 * Servlet implementation class TwitterConnected
 */
public class TwitterConnected extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Twitter twitter = (Twitter) request.getSession()
				.getAttribute("twitter");
		if (hasTwitter(response, twitter)) {
			response.getWriter().print(1);
			return;
		}
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if (person.gettAccess() != null && person.gettSAccess() != null) {
			if (initTwitter(request, person, twitter)) {
				response.getWriter().print(1);
				return;
			}
		}
		response.getWriter().print(0);
	}

	private boolean hasTwitter(HttpServletResponse response, Twitter twitter)
			throws IOException {
		try {
			if (twitter != null && twitter.getOAuthAccessToken() != null) {
				twitter.verifyCredentials();
				return true;
			}
		} catch (TwitterException e) {
		}
		return false;
	}

	static boolean initTwitter(HttpServletRequest request, LoggedUser person,
			Twitter twitter) {
		if (twitter == null) {
			twitter = new TwitterFactory().getInstance();
		}
		TwitterLogin.addTwitterKyes(twitter);
		AccessToken at = new AccessToken(person.gettAccess(),
				person.gettSAccess());
		twitter.setOAuthAccessToken(at);
		try {
			twitter.verifyCredentials();
			request.getSession().setAttribute("twitter", twitter);
		} catch (TwitterException te) {
			return false;
		}
		return true;
	}
}
