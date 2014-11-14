package service.rest;

import gamification.ExecuteAction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import utils.Status;
import utils.Status.STATE;

import com.google.gson.Gson;

@Produces({ MediaType.APPLICATION_JSON })
@Path("share")
public class GamificationMiscResources {

	private Gson g;

	public GamificationMiscResources() {
		g = new Gson();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response shareButtonUsed(@Context HttpServletRequest request) {
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		Status execute = ExecuteAction.getInstance().execute("share", currentUser, null);
		if(execute.severity == STATE.OK){
			return Response.status(Response.Status.ACCEPTED).build();	
		}
		return Response.status(Response.Status.CONFLICT).entity(g.toJson(execute)).build();
	}

}
