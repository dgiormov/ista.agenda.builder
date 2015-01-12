package auth.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import auth.openidconnect.Utils;
import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;
import utils.LoginUtils;

/**
 * Servlet implementation class Logout
 */
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			response.sendRedirect("/");
		}
		currentUser.setSessionExpires(0);
		lue.updateEntity(currentUser);
		performLocalInfoRemove(request, response);
		response.sendRedirect("/");
	}

	public void performLocalInfoRemove(HttpServletRequest request,
			HttpServletResponse response) {
		request.getSession().removeAttribute(Utils.ACCESS_TOKEN_SESSION_KEY);
		request.getSession().setAttribute(Utils.TWITTER, null);
		LoginUtils.invalidateCookie(request, response);
	}

}
