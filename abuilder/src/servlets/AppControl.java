package servlets;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.SecurityException;

import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

import entities.EnabledFunctionality;
import exposed.EnabledFuncExposed;

/**
 * Servlet implementation class DisableRating
 */
public class AppControl extends LimitedServlet {
	
	private Logger logger = LoggerFactory.getLogger(AppControl.class);
	
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			securityCheck(request, response);

			//FIX ME security check, record who has changed the setting
			if(request.getParameter("enable") != null){
				logger.warn("Aplication is working in write mode now.");
				setEnablement(null, true);
				return;
			}
			if(request.getParameter("disable") != null){
				logger.warn("Aplication is working in read only mode now.");
				setEnablement(null, false);
				return;
			}
		} catch (SecurityException e) {
			logger.warn("Aplication control is accessed by: " +e.getMessage());
		}
	}

	private void setEnablement(String userName, boolean isEnabled) {
		EnabledFunctionality e = new EnabledFunctionality();
		e.setUserName(userName);
		e.setEnabled(isEnabled);
		EnabledFuncExposed ee = new EnabledFuncExposed();
		ee.createEntity(e);
	}

	public static boolean writeMode(){
		EnabledFuncExposed ee = new EnabledFuncExposed();
		EnabledFunctionality entity = ee.getEntity();
		if(entity != null && entity.getUserName() != null){
			return "i024096".equals(entity.getUserName().toLowerCase()) && entity.isEnabled();
		}
		return false;
	}
}
