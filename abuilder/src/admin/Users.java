package admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;

/**
 * Servlet implementation class Users
 */
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedUserExposed lue = new LoggedUserExposed();
		Gson g = new Gson();
		List<LoggedUser> allPersons = lue.getAllPersons();
		for (LoggedUser loggedUser : allPersons) {
			response.getWriter().print(g.toJson(new String[]{loggedUser.getName(), loggedUser.getComments().size()+"", loggedUser.getLikedComments().size()+""}));
		}
	}

}
