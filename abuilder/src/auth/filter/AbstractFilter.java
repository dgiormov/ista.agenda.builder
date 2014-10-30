package auth.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import auth.openidconnect.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class AbstractFilter implements Filter {

	private static final String USER_ROLES_JSON = "user_roles.json";
	private static final List<UserInRole> roles = new ArrayList<UserInRole>();

	
	@Override
	public void doFilter(ServletRequest req, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		if(roles.isEmpty()){
			Gson g = new Gson();
			HttpServletRequest request = (HttpServletRequest) req;
	   		final BufferedReader userData = new BufferedReader(new InputStreamReader(request.getServletContext().getResourceAsStream(Utils.PATH_TO_PROVIDERS+USER_ROLES_JSON)));
	   		List<UserInRole> tempRead = g.fromJson(userData, new TypeToken<List<UserInRole>>(){}.getType());
	   		roles.addAll(tempRead);
		}
		
	}
	private class UserInRole {
		private String userName;
		private String role;
	}
	
	protected List<String> getRolesForUser(String user) {
		List<String> result = new ArrayList<String>();
		result.add("user");
		for (UserInRole userInRole : roles) {
			if(userInRole.userName.equals(user)){
				result.add(userInRole.role);
			}
		}
		return result;
	}

}
