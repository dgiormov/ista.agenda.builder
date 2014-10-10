package service.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.SessionExposedBasic;
import utils.Constants;
import utils.ICalendarConverter;

import com.google.gson.Gson;

@Produces({ MediaType.APPLICATION_JSON })
@Path("/sessions")
public class SessionResource {

	@Context
	private HttpHeaders httpHeaders;
	private Gson g;

	public SessionResource() {
		g = new Gson();
	}
	

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response listAllSessions(@Context HttpServletRequest hsr, @QueryParam("date") int date) throws Exception {
		SessionExposedBasic eventExposed = new SessionExposedBasic();
		return Response.status(Response.Status.OK)
				.entity(g.toJson(eventExposed.allEntitiesOnDate(hsr, date))).build();
	}
	
	@GET
	@Path("{id}")
	public Response readConfiguration0(@PathParam("id") String id, @Context HttpServletRequest hsr)
			throws Exception {
		SessionExposedBasic eventExposed = new SessionExposedBasic();
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser currentUser = pe.getCurrentUser(hsr);
		if (id != null) {
			return Response.status(Response.Status.OK)
					.entity(g.toJson(eventExposed.findEventByIdWrapped(id, currentUser))).build();
		}
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(g.toJson("")).build();
	}

	@GET
	@Path("/name")
	public Response readName() throws Exception {
		return Response.status(Response.Status.OK)
				.entity(g.toJson(Constants.EVENT_NAME)).build();
	}

	@GET
	@Path("/ical/{id}")
	@Produces("text/calendar")
	public Response readConfiguration(@PathParam("id") String id)
			throws Exception {
		SessionExposedBasic eventExposed = new SessionExposedBasic();
		if (id == null) {
			return Response.status(Response.Status.OK)
					.entity(g.toJson(eventExposed.findEventById(id))).build();
		}

		ICalendarConverter calendar = new ICalendarConverter(eventExposed.findEventById(id));
		// Generate your calendar here
		ResponseBuilder builder = Response.ok();
		builder.header("content-disposition",
				"attachment;filename=calendar.ics");
		builder.entity(calendar.toString());
		return builder.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdate(String content) {
		System.out.println(content);
		SessionExposedBasic eventDao = new SessionExposedBasic();
		Session e = g.fromJson(content, Session.class);
		eventDao.createEntity(e);
		return Response
				.status(Response.Status.BAD_REQUEST)
				.entity("System DB Configuration API cannot create configurations with this name.").type("text/plain").build(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
}
