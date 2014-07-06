package servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.SecurityException;

public class LimitedServlet extends HttpServlet {

	protected void securityCheck(HttpServletRequest request, HttpServletResponse response) throws SecurityException{
		
	}
}
