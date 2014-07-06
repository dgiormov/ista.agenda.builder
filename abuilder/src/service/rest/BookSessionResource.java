package service.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.ClientResponse.Status;

import servlets.AppControl;
import entities.Session;
import entities.LoggedUser;
import exposed.SessionExposedBasic;
import exposed.LoggedUserExposed;

@Path("book/session")
public class BookSessionResource {

	
	private static final float RATE_LIMIT = 10;
	
	@POST
	public Response bookSession(@Context HttpServletRequest request, @FormParam("sessionId") String sessionId, @FormParam("isSelected") String isSelectedString){
		if (sessionId == null || isSelectedString == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		boolean bin0 = Boolean.parseBoolean(isSelectedString);

		SessionExposedBasic ee = new SessionExposedBasic();
		Session session = ee.findEventById(sessionId);
		if (session == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (bin0) {
			if (!person.getSessions().contains(session)) {
				person.addToAgenda(session);
			}

		} else {
			person.getSessions().remove(session);
			person.getBookedWhen().remove(session.getId());
		}
		pe.updateEntity(person);
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
		Session event = ee.findEventById(sessionId);
		if (event == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean isSelected = person.getSessions().contains(event);
		return Response.ok().entity(isSelected + "").build();
	}
}
