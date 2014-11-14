package service.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;
import utils.Constants;
import utils.LoginUtils;

import com.google.gson.Gson;

@Path("user")
@Produces({ MediaType.APPLICATION_JSON })
public class LoggedUserREST {

	private Gson g;

	public LoggedUserREST() {
		g = new Gson();
	}

	@GET
	public Response getCurrentLoggedUser(@Context HttpServletRequest request) {
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		LoggedUserJson result = new LoggedUserJson();
		result.isLogged = false;
		String cookieValue = LoginUtils.findCookieValue(request, Constants.COOKIE_PROVIDER_KEY);
		result.hasCookie = cookieValue != null &&  cookieValue.trim().length() > 0;
		if(currentUser != null){
			result.isLogged = true;
			
			if(currentUser.getName().indexOf(" ") != -1){
				result.name = currentUser.getName().substring(0,  currentUser.getName().indexOf(" "));	
			} else {
				result.name = currentUser.getName();
			}
		}
		return Response.status(Response.Status.OK)
				.entity(g.toJson(result)).build();
	}
	
	private class LoggedUserJson {
		private boolean isLogged;
		private String name;
		private boolean hasCookie;
	}

}
