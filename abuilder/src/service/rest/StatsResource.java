package service.rest;

import gamification.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import persistency.entities.Comment;
import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.entities.gamification.PointsCategory;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.SessionExposedBasic;
import service.rest.wrappers.CommentBasic;

import com.google.gson.Gson;

@Path("stats")
@Produces({ MediaType.APPLICATION_JSON })
public class StatsResource {

	private static final int MIN_RATES = 9;

	@GET
	@Path("game")	
	public Response getGamificationStats(@Context HttpServletRequest request){
		LoggedUserExposed lue = new LoggedUserExposed();
		Data d = new Data();
		List<LoggedUser> allPersons = lue.getAllPersons();
		d.allPlayers = allPersons.size();
		List<Comment> allComments = new ArrayList<Comment>();
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory category = pce.findCategoryByShortName("share");
		d.allShares = category.getInstancesOfThisType().size();
		for (LoggedUser loggedUser : allPersons) {
			Player player = loggedUser.getPlayer();
			d.totalScore += player.getPoints();
			d.totalLikes += loggedUser.getLikedComments().size();
			d.totalComments += loggedUser.getComments().size();
			allComments.addAll(loggedUser.getComments());
			d.totalRates += loggedUser.getSessionRatings().size()+loggedUser.getSpeakerRatings().size();
		}
		Collections.sort(allComments);
		int howManyTopComments = 3;
		for (Comment comment : allComments) {
			if (howManyTopComments <= 0){
				break;
			}
			d.topComments.add(new CommentBasic(comment, false));
			howManyTopComments--;
		}
		return Response.ok().entity((new Gson()).toJson(d)).build();
	} 
	
	public class Data {
		private int allPlayers;
		private int allShares;
		private int totalScore;
		private int totalLikes;
		private int totalComments;
		private int totalRates;
		private List<CommentBasic> topComments = new ArrayList<CommentBasic>();
	}
	
	@GET
	@Path("session")
	public Response getSessionRanking( @QueryParam("min_votes") int min_votes, @QueryParam("max_votes") int max_votes){
		if(min_votes == 0){
			min_votes = MIN_RATES; 
		}
		if(max_votes == 0){
			max_votes = -1; 
		}
		LoggedUserExposed lue = new LoggedUserExposed();
		List<LoggedUser> allPersons = lue.getAllPersons();
		List<RatedObject> result = new ArrayList<StatsResource.RatedObject>();
		SessionExposedBasic seb = new SessionExposedBasic();
		for (LoggedUser loggedUser : allPersons) {
			Map<Integer, Integer> sessionRatings = loggedUser.getSessionRatings();
			processRatings(result, seb, sessionRatings);
		}
		result = clearLowRatingSessions(result, min_votes, max_votes);
		return Response.ok().entity((new Gson()).toJson(result)).build();
	}

	private void processRatings(List<RatedObject> result, SessionExposedBasic seb,
			Map<Integer, Integer> sessionRatings) {
		Set<Integer> keySet = sessionRatings.keySet();
		for (Integer integer : keySet) {
			RatedObject rated = new RatedObject();
			rated.id = integer;
			int indexOf = result.indexOf(rated);
			if(indexOf != -1){
				result.get(indexOf).totalRating+= sessionRatings.get(integer);
				result.get(indexOf).timesRated++;
			} else {
				Session findSessionById = seb.findSessionById(integer+"");
				rated.name = findSessionById.getName();
				findSessionById.getSpeakers().size();
				rated.speakers = findSessionById.getSpeakers().toString();
				rated.totalRating = sessionRatings.get(integer);
				result.add(rated);
			}
		}
	}

	private List<RatedObject> clearLowRatingSessions(List<RatedObject> preResult, int min_votes, int max_votes) {
		List<RatedObject> forRemove = new ArrayList<StatsResource.RatedObject>();
		for (RatedObject ratedObject : preResult) {
			if(ratedObject.timesRated < min_votes || (max_votes > -1 && ratedObject.timesRated > max_votes)){
				forRemove.add(ratedObject);
			} else {
				ratedObject.computeRating();
			}
		}
		for (RatedObject ratedObject : forRemove) {
			preResult.remove(ratedObject);
		}
		Collections.sort(preResult);
		List<RatedObject> result = new ArrayList<StatsResource.RatedObject>(5);
		int pos = 0;
		double lastRating = 6;
		for (RatedObject ratedObject : preResult) {
			if(lastRating > ratedObject.rating){
				lastRating = ratedObject.rating;
				pos++;
			}
			ratedObject.pos = pos;
			result.add(ratedObject);
			
		}
		return result;
	}
	
	
	private class RatedObject implements Comparable<RatedObject>{
		private int id;
		private String name;
		private String speakers;
		private double rating = 0;
		private double totalRating = 0;
		private int timesRated = 1;
		private int pos = 1;
		
		@Override
		public boolean equals(Object obj) {
			if(RatedObject.class.isInstance(obj)){
				return ((RatedObject) obj).id == id;
			}
			return super.equals(obj);
		}
		
		public void computeRating() {
			double d = (double)totalRating/timesRated;
			d = Math.round(d*100)/100.0d;
			rating = d;
		}

		@Override
		public int compareTo(RatedObject o) {
			double diff =  o.rating - rating;
			if(diff>0){
				return 1;	
			} else if(diff <0){
				return -1;
			}
			return 0;
			
		}
		
	}
	
	@GET
	@Path("speaker")
	public Response getSpeakerRanking(@QueryParam("min_votes") int min_votes, @QueryParam("max_votes") int max_votes){
		if(min_votes == 0){
			min_votes = MIN_RATES; 
		}
		if(max_votes == 0){
			max_votes = -1; 
		}
		LoggedUserExposed lue = new LoggedUserExposed();
		List<LoggedUser> allPersons = lue.getAllPersons();
		List<RatedObject> result = new ArrayList<StatsResource.RatedObject>();
		SessionExposedBasic seb = new SessionExposedBasic();
		for (LoggedUser loggedUser : allPersons) {
			Map<Integer, Integer> sessionRatings = loggedUser.getSpeakerRatings();
			processRatings(result, seb, sessionRatings);
		}
		result = clearLowRatingSessions(result, min_votes, max_votes);
		return Response.ok().entity((new Gson()).toJson(result)).build();
	}

}
