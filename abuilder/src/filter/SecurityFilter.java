package filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

import entities.LoggedUser;
import exposed.LoggedUserExposed;

/**
 * Servlet Filter implementation class SecurityFilter
 */
public class SecurityFilter implements Filter {

	private ServletContext servletContext;


	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//		try {
//			if(!isAuthorized(((HttpServletRequest) request))){
//				((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
//				return;
//			}
//		} catch (PersistenceException e) {
//			((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
//			return;
//		}
		

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	private boolean isAuthorized(HttpServletRequest httpServletRequest) throws PersistenceException {
		return true;
	}
	

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		servletContext = fConfig.getServletContext();
	}

}
