package auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;
import utils.Constants;
import utils.LoginUtils;

/**
 * Servlet implementation class IsUserLogged
 */
public class IsUserLogged extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			LoggedUserExposed lue = new LoggedUserExposed();
			LoggedUser personById = lue.getCurrentUser(request);
			if(personById != null){
				if(!personById.isSessionExpired()){
					return;
				}
			}
			String provider = LoginUtils.findCookieValue(request, Constants.COOKIE_PROVIDER_KEY);
			if(provider != null){
				response.sendRedirect("/redirect?provider="+provider);
				return;
			}
			response.sendError(401);
	}
}
