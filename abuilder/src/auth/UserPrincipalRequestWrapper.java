package auth;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class UserPrincipalRequestWrapper extends HttpServletRequestWrapper {


  private String user;
  private List<String> roles = null;
  private HttpServletRequest realRequest;
  
  public UserPrincipalRequestWrapper(String user, List<String> roles, HttpServletRequest request) {
    super(request);
    this.user = user;
    this.roles = roles;
    this.realRequest = request;
  }

  @Override
  public boolean isUserInRole(String role) {
    if (roles == null) {
      return this.realRequest.isUserInRole(role);
    }
    return roles.contains(role);
  }

  @Override
  public Principal getUserPrincipal() {
    if (this.user == null) {
      return realRequest.getUserPrincipal();
    }
    
    return new Principal() {
      @Override
      public String getName() {     
        return user;
      }
    };
  }

@Override
public boolean authenticate(HttpServletResponse response) throws IOException,
		ServletException {
	// TODO Auto-generated method stub
	return super.authenticate(response);
}

@Override
public String getAuthType() {
	// TODO Auto-generated method stub
	return super.getAuthType();
}

@Override
public String getRemoteUser() {
	// TODO Auto-generated method stub
	return super.getRemoteUser();
}

@Override
public void login(String username, String password) throws ServletException {
	// TODO Auto-generated method stub
	super.login(username, password);
}
  
  
}

