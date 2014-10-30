package auth.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;

/**
 * Servlet Filter implementation class AdminFilter
 */
public class AdminFilter extends AbstractFilter {

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		super.doFilter(request, response, chain);
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser((HttpServletRequest) request);
		boolean isAuthorized = false;
		if(currentUser != null && !currentUser.isSessionExpired()){
			List<String> rolesForUser = getRolesForUser(currentUser.getOpenId()+"");
			isAuthorized = rolesForUser.contains("remote_access_admin");
		}
		if(currentUser == null){
			((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login with admin account");
			return;
		}

		if(isAuthorized){
			chain.doFilter(request, response);	
		} else {
			((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN, "This operation requires admin priviliges");
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
