package service.rest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import persistency.entities.LoggedUser;
import persistency.entities.Points;
import persistency.exposed.LoggedUserExposed;

import com.google.gson.Gson;

import utils.PointsHelper;

public class RankingResource {
	
	private Gson g = new Gson();
	
	@GET
	@Path("/ranking/{id}")
	public Response extractPlayerData(@PathParam("id") String id) throws Exception {
		LoggedUserExposed pe = new LoggedUserExposed();
		int pid = Integer.parseInt(id);
		pid = pid -42;
		LoggedUser personById = pe.findPersonById(pid+"");
		Map<String, List<PointsHelper>> playerStats = personById.getPlayerStats();
		return Response.status(Response.Status.OK)
				.entity(g.toJson(new StatsHelper(playerStats.get("generic"), playerStats.get("codes"), personById.getPlayer().getPoints()))).build();
	}
	
	@GET
	@Path("/rankingRaw/{id}")
	public Response extractPlayerDataRaw(@PathParam("id") String id) throws Exception {
		LoggedUserExposed pe = new LoggedUserExposed();
		int pid = Integer.parseInt(id);
		pid = pid -42;
		LoggedUser personById = pe.findPersonById(pid+"");
		String result = "";
		List<Points> points = personById.getPoints();
		for (Points points2 : points) {
			result+="Points code: "+points2.getType().getName() +" points: "+points2.getType().getPoints() + "\n\r";
		}
		return Response.status(Response.Status.OK)
				.entity(result).build();
	}
	
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
