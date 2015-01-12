package admin;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import auth.openidconnect.Utils;
import persistency.entities.admin.EnabledFunctionality;
import persistency.exposed.EnabledFuncExposed;
import persistency.exposed.LoggedUserExposed;
import utils.SecurityException;

/**
 * Servlet implementation class DisableRating
 */
public class AppControl extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(AppControl.class);

	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedUserExposed lue = new LoggedUserExposed();
		if(request.getParameter("enable") != null){
			logger.warn("Aplication is working in write mode now.");
			setEnablement(lue.getCurrentUser(request).getName(), true);
			return;
		} else if(request.getParameter("disable") != null){
			logger.warn("Aplication is working in read only mode now.");
			setEnablement(lue.getCurrentUser(request).getName(), false);
			return;
		}
	}

	private void setEnablement(String userName, boolean isEnabled) {
		EnabledFunctionality e = new EnabledFunctionality();
		e.setUserName(userName);
		e.setEnabled(isEnabled);
		EnabledFuncExposed ee = new EnabledFuncExposed();
		ee.createEntity(e);
	}

	public static boolean writeMode(HttpServletRequest request){
		EnabledFuncExposed ee = new EnabledFuncExposed();
		EnabledFunctionality entity = ee.getEntity();
		if(entity != null && entity.getUserName() != null){
			return entity.isEnabled();
		}
		return false;
	}
}
