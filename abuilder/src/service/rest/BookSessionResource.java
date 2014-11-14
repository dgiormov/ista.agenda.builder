package service.rest;

import gamification.ExecuteAction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.SessionExposedBasic;

@Path("book/session")
public class BookSessionResource {

	
	@POST
	public Response bookSession(@Context HttpServletRequest request, @QueryParam("id") String sessionId, @QueryParam("isSelected") Boolean isSelectedString){
		if (sessionId == null || isSelectedString == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		boolean bin0 = isSelectedString;

		SessionExposedBasic ee = new SessionExposedBasic();
		Session session = ee.findSessionById(sessionId);
		if (session == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (bin0) {
			if (!person.getSessions().contains(session)) {
				person.addToAgenda(session);
			}

		} else {
			person.getSessions().remove(session);
		}
		pe.updateEntity(person);
		ExecuteAction.getInstance().execute("prepare", person, null);
		return Response.status(Status.OK).build();
	}
	
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getSession(@Context HttpServletRequest request, @PathParam("sessionId") String sessionId){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (sessionId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		SessionExposedBasic ee = new SessionExposedBasic();
		Session session = ee.findSessionById(sessionId);
		if (session == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean isSelected = person.getSessions().contains(session);
		return Response.ok().entity(isSelected + "").build();
	}
}
