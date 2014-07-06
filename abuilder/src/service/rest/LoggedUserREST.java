package service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import entities.Speaker;
import exposed.SpeakerExposed;

//@Path("configuration/account/{applicationAccount}/application/{applicationName}/tenant/{tenantName}")
@Path("user")
@Produces({ MediaType.APPLICATION_JSON })
@SuppressWarnings({})
public class LoggedUserREST {

	private Gson g;

	public LoggedUserREST() {
		g = new Gson();
	}

	@GET
	@Path("{path:.*}")
	public Response readConfiguration(
			@PathParam("applicationAccount") String applicationAccount,
			@PathParam("applicationName") String applicationName,
			@PathParam("path") String rawPath,
			@PathParam("tenantName") String tenantName) throws Exception {
		SpeakerExposed users = new SpeakerExposed();
		return Response.status(Response.Status.OK)
				.entity(g.toJson(users.allEntities())).build();
	}

//	@POST
//	// @Path("{path:.+}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response createOrUpdate(String content) {
//		try {
//			LoggedUserExposed userDao = new LoggedUserExposed();
//			Participant e = g.fromJson(content, Participant.class);
//			userDao.createEntity(e);
//			return Response.status(Response.Status.OK).build();
//		} catch (Exception e) {
//			return Response
//					.status(Response.Status.BAD_REQUEST)
//					.entity("System DB Configuration API cannot create configurations with this name.").type("text/plain").build(); //$NON-NLS-1$ //$NON-NLS-2$
//		}
//	}

	@DELETE
	@Path("{path:.+}")
	public Response deleteConfiguration(
			@PathParam("applicationAccount") String applicationAccount,
			@PathParam("applicationName") String applicationName,
			@PathParam("tenantName") String tenantName,
			@PathParam("path") String rawPath) {
		return Response
				.status(Response.Status.BAD_REQUEST)
				.entity("System DB Configuration API cannot delete configurations with this name.").type("text/plain").build(); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
