package service.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import utils.Status.STATE;
import utils.TimeStopper;

import com.google.gson.Gson;

@Path("rating")
@Produces(MediaType.APPLICATION_JSON)
public class RatingResource {

	
	private static final float RATE_LIMIT = 10;
	
	@POST
	@Path("/session")
	public Response rateSession(@Context HttpServletRequest requst, @QueryParam("rating") String rating, @QueryParam("sessionId") String sessionId){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(requst);
		
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return rateGeneric(requst, person, rating, sessionId, person.getSessionRatings());
	}
	
	@POST
	@Path("/speaker")
	public Response rateSpeaker(@Context HttpServletRequest requst, @QueryParam("rating") String rating, @QueryParam("sessionId") String sessionId){
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(requst);
		
		if (person == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return rateGeneric(requst, person, rating, sessionId, person.getSpeakerRatings());
	}
	
	private Response rateGeneric(HttpServletRequest request, LoggedUser person, String rating, String sessionId, Map<Integer, Integer> ratings){

		if (sessionId == null) {
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
		Session session = ee.findSessionById(sessionId);
		if (session == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		long timeToSessionAndHalf = TimeStopper.timeToSessionAndHalf(session);
		if(timeToSessionAndHalf < 0){
			timeToSessionAndHalf = timeToSessionAndHalf*(-1);
			return Response.status(Status.PRECONDITION_FAILED).entity((new Gson()).toJson(new utils.Status(STATE.ERROR, "You can only rate during or after the session. Time left: "+timeToSessionAndHalf+" min"))).build();
		}
		if (checkForConflictingTalks(ratings, session, ee)) {
			return Response.status(Status.NOT_ACCEPTABLE).entity("It is quite impossible for you to have attended all these sessions.").build();
		}
		boolean isContained = ratings.containsKey(session.getId());
		if(bin0 == 0){
			if(isContained){
				ratings.remove(session.getId());
				person.updateRatingPoints(ratings, session, false);
			}
		} else {
			ratings.put(session.getId(), bin0);
			person.updateRatingPoints(ratings, session, true);
		}
		LoggedUserExposed pe = new LoggedUserExposed();
		pe.updateEntity(person);
		return Response.status(Status.OK).build();
	}
	
	private boolean checkForConflictingTalks(Map<Integer, Integer> ratings,
			Session session, SessionExposedBasic ee) {
		return ratings.get(session.getId()) == null && ratings.size() >= RATE_LIMIT;
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
		return getRating(request, person.getSessionRatings());
	}
	
	private Response getRating(@Context HttpServletRequest request, Map<Integer, Integer> ratings) {
		
		String sessionId = request.getParameter("id");
		if (sessionId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Integer rating = ratings.get(Integer.parseInt(sessionId));
		if (rating != null) {
			return Response.status(Status.OK).entity(rating).build();
		}
		return Response.status(Status.OK).entity("0").build();
	}
}
