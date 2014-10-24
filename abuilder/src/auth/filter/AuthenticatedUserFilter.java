package auth.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import persistency.entities.Speaker;
import persistency.exposed.LoggedUserExposed;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import auth.UserPrincipalRequestWrapper;
import auth.openidconnect.ProviderData;
import auth.openidconnect.Utils;

public class AuthenticatedUserFilter implements Filter {

	private static final String USER_ROLES_JSON = "user_roles.json";
	private static final List<UserInRole> roles = new ArrayList<UserInRole>();

	public void init(FilterConfig cfg) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse response,
			FilterChain next) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		if(roles.isEmpty()){
			Gson g = new Gson();
	   		final BufferedReader userData = new BufferedReader(new InputStreamReader(request.getServletContext().getResourceAsStream(Utils.PATH_TO_PROVIDERS+USER_ROLES_JSON)));
	   		List<UserInRole> tempRead = g.fromJson(userData, new TypeToken<List<UserInRole>>(){}.getType());
	   		roles.addAll(tempRead);
		}

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

	private List<String> getRolesForUser(String user) {
		List<String> result = new ArrayList<String>();
		result.add("user");
		for (UserInRole userInRole : roles) {
			if(userInRole.userName.equals(user)){
				result.add(userInRole.role);
			}
		}
		return result;
	}

	public void destroy() {
	}
	
	private class UserInRole {
		private String userName;
		private String role;
	}
}
