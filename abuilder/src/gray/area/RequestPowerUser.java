package gray.area;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.LoggedUser;
import persistency.entities.admin.RequestPower;
import persistency.entities.admin.exposed.PowerUserExposed;
import persistency.entities.admin.exposed.RequestPowerExposed;
import persistency.exposed.LoggedUserExposed;

/**
 * Servlet implementation class RequestPowerUser
 */
public class RequestPowerUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			response.sendError(401);
		}
		RequestPowerExposed rpu = new RequestPowerExposed();
		RequestPower requestPower = rpu.findPersonById(currentUser.getId());
		if(requestPower == null){
			PowerUserExposed pue = new PowerUserExposed();
			if(pue.isPowerUser(currentUser.getId())){
				response.getWriter().print("Already approved.");
			}
			requestPower = new RequestPower();
			requestPower.setId(currentUser.getId());
			rpu.createEntity(requestPower);
			response.getWriter().print("Your request has been accepted.");
			return;
		} else {
			response.getWriter().print("Not yet approved...");
			return;
		}
	}

}
