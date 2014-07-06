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

@Path("rating")
public class RatingResource {

	
	private static final float RATE_LIMIT = 10;
	
	@POST
	@Path("/session")
	public Response rateSession(@Context HttpServletRequest requst, @FormParam("rating") String rating, @FormParam("eventId") String eventId){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(requst);
		
		if (person == null || requst.getUserPrincipal() == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return rateGeneric(requst, person, rating, eventId, person.getEventRatings());
	}
	
	@POST
	@Path("/speaker")
	public Response rateSpeaker(@Context HttpServletRequest requst, @FormParam("rating") String rating, @FormParam("eventId") String eventId){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(requst);
		
		if (person == null || requst.getUserPrincipal() == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return rateGeneric(requst, person, rating, eventId, person.getSpeakerRatings());
	}
	
	private Response rateGeneric(HttpServletRequest request, LoggedUser person, String rating, String eventId, Map<Integer, Integer> ratings){
		if (!AppControl.writeMode()) {
			return Response.status(Status.PAYMENT_REQUIRED).entity("You can only rate session during the event.").build();
		}

		if (eventId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (rating == null) {
			return Response.status(Status.BAD_REQUEST).build();
			
		}
		Integer bin0 = Integer.parseInt(rating);
		if (bin0 == null || bin0 > 5 || bin0 < 0) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		SessionExposedBasic ee = new SessionExposedBasic();
		Session event = ee.findEventById(eventId);
		if (event == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (checkForConflictingTalks(ratings, event, ee)) {
			return Response.status(Status.PAYMENT_REQUIRED).entity("It is quite impossible for you to have attended all these sessions.").build();
		}
		boolean isContained = ratings.containsKey(event.getId());
		if(bin0 == 0){
			if(isContained){
				ratings.remove(event.getId());
			}
		} else {
			ratings.put(event.getId(), bin0);
		}
		LoggedUserExposed pe = new LoggedUserExposed();
		pe.updateEntity(person);
		return Response.status(Status.OK).build();
//		logger.info("Session with id: " + eventId +" has been rated with: " + bin0 + " by: "+person.getName());
	}
	
	private boolean checkForConflictingTalks(Map<Integer, Integer> ratings,
			Session event, SessionExposedBasic ee) {
		return ratings.get(event.getId()) == null && ratings.size() >= RATE_LIMIT;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/speaker")
	public Response getSpeakerRating(@Context HttpServletRequest request){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return getRating(request, person.getSpeakerRatings());
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/session")
	public Response getSessionRating(@Context HttpServletRequest request){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return getRating(request, person.getEventRatings());
	}
	
	private Response getRating(@Context HttpServletRequest request, Map<Integer, Integer> ratings) {
		
		String eventId = request.getParameter("id");
		if (eventId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Integer rating = ratings.get(Integer.parseInt(eventId));
		if (rating != null) {
			return Response.status(Status.OK).entity(rating).build();
		}
		return Response.status(Status.OK).entity("0").build();
	}
}
