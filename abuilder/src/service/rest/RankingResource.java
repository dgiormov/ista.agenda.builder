package service.rest;

import gamification.ExecuteAction;
import gamification.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.exposed.LoggedUserExposed;
import service.rest.wrappers.PlayerDetailsHelper;
import service.rest.wrappers.PointsInstanceJson;
import service.rest.wrappers.UserPointsCategoryJson;
import utils.PointsHelper;
import utils.RankingHelper;

import com.google.gson.Gson;

@Produces({ MediaType.APPLICATION_JSON })
@Path("/ranking")
public class RankingResource {
	
	private Gson g = new Gson();
	
	@GET
	public Response getRanking(@Context HttpServletRequest hsr) throws Exception {
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(hsr);
		List<LoggedUser> allPersons = lue.getAllPersons();
		List<Player> allPlayers = new ArrayList<Player>(allPersons.size());
		int position = 0;
		Player cplayer = null;
		
		for (LoggedUser loggedUser : allPersons) {
			Player player = loggedUser.getPlayer();
			
			allPlayers.add(player);
		}
		Collections.sort(allPlayers);
		long previousPoints = 0;
		for (Player player : allPlayers) {
			if(previousPoints != player.getPoints()){
				position++;
			}
			player.setPosition(position);
			previousPoints = player.getPoints();
			LoggedUser loggedUser = lue.findPersonById(player.getId()-42);
			if(position <= 100){
				ExecuteAction.getInstance().execute("top100", loggedUser, null);
			}
			if(currentUser != null){
				if(loggedUser.getId() == currentUser.getId()){
					cplayer = player;
				}
			}
		}
		RankingHelper rh = new RankingHelper(cplayer, allPlayers);
		return Response.status(Response.Status.OK)
				.entity(g.toJson(rh)).build();
	}
	
	@GET
	@Path("{id}")
	public Response extractPlayerData(@PathParam("id") String id) throws Exception {
		LoggedUserExposed pe = new LoggedUserExposed();
		int pid = Integer.parseInt(id);
		pid = pid -42;
		LoggedUser personById = pe.findPersonById(pid);
		List<PointsInstance> pointsInstances = personById.getPointsInstances();
		Map<PointsCategory, Integer> map  = new HashMap<PointsCategory, Integer>();
		for (PointsInstance pointsInstance : pointsInstances) {
			Integer integer = map.get(pointsInstance.getCategory());
			if(integer != null){
				map.put(pointsInstance.getCategory(), integer+1);
			} else {
				map.put(pointsInstance.getCategory(), 1);
			}
		}
		List<UserPointsCategoryJson> result = new ArrayList<UserPointsCategoryJson>();
		for (PointsCategory instance : map.keySet()) {
			if(instance.getPoints() > 0){
				result.add(new UserPointsCategoryJson(instance, map.get(instance)));
			}
		}
		
		Collections.sort(result);
		
		return Response.status(Response.Status.OK)
				.entity(g.toJson(new PlayerDetailsHelper(personById.getName(), result, personById.getPoints()))).build();
	}
	
	@GET
	@Path("{id}/detailed")
	public Response extractPlayerDataDetailed(@PathParam("id") String id) throws Exception {
		LoggedUserExposed pe = new LoggedUserExposed();
		int pid = Integer.parseInt(id);
		pid = pid -42;
		LoggedUser personById = pe.findPersonById(pid);
		List<PointsInstanceJson> playerStats = toJsonObjects(personById.getPointsInstances());
		return Response.status(Response.Status.OK)
				.entity(g.toJson(playerStats)).build();
	}
	
	private List<PointsInstanceJson> toJsonObjects(
			List<PointsInstance> pointsInstances) {
		List<PointsInstanceJson> result = new ArrayList<PointsInstanceJson>();
		for (PointsInstance pointsInstance : pointsInstances) {
			if(pointsInstance.getCategory().getPoints() > 0){
				result.add(new PointsInstanceJson(pointsInstance));	
			}
		}
		return result;
	}

//	@GET
//	@Path("/rankingRaw/{id}")
//	public Response extractPlayerDataRaw(@PathParam("id") String id) throws Exception {
//		LoggedUserExposed pe = new LoggedUserExposed();
//		int pid = Integer.parseInt(id);
//		pid = pid -42;
//		LoggedUser personById = pe.findPersonById(pid+"");
//		String result = "";
//		List<PointsInstance> points = personById.getPointsInstances();
//		for (PointsInstance points2 : points) {
//			result+="Points code: "+points2.getCategory().getName() +" points: "+points2.getCategory().getPoints() + "\n\r";
//		}
//		return Response.status(Response.Status.OK)
//				.entity(result).build();
//	}
	
	private class StatsHelper{
		private List<PointsHelper> genericData;
		private List<PointsHelper> codesData;
		private float points;

		public StatsHelper(List<PointsHelper> genericData, List<PointsHelper> codesData, float points) {
			this.genericData = genericData;
			this.codesData = codesData;
			this.points = points;
			
		}
	}
}
