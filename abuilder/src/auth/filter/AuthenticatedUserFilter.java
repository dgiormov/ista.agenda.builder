package auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;
import auth.UserPrincipalRequestWrapper;

public class AuthenticatedUserFilter extends AbstractFilter {

	
	public void init(FilterConfig cfg) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse response,
			FilterChain next) throws IOException, ServletException {
		super.doFilter(req, response, next);
		HttpServletRequest request = (HttpServletRequest) req;
		String user = null;
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser != null && !currentUser.isSessionExpired()){
			user = currentUser.getId()+"";
		}

		// pass the request along the filter chain
		if(HttpServletRequest.class.isInstance(request)){
			if(!((HttpServletRequest) request).getMethod().equals("GET")) {
				if(user == null || currentUser.isSessionExpired()){
					((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "This operation requires login");
					return;	
				}
			}
		}
		next.doFilter(new UserPrincipalRequestWrapper(user, getRolesForUser(user), request), response);
	}
	
	public void destroy() {
	}
	
	
}
