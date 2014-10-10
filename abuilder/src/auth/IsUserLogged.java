package auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;

/**
 * Servlet implementation class IsUserLogged
 */
public class IsUserLogged extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long attribute = (Long) request.getSession().getAttribute("uid");
		if(attribute != null){
			LoggedUserExposed lue = new LoggedUserExposed();
			LoggedUser personById = lue.findPersonById(attribute+"");
			if(personById != null){
				if(personById.isSessionExpired()){
					response.sendError(440);
				}
			} else {
				response.sendError(401);
			}
		} else {
			response.sendError(401);
		}
	}
}
