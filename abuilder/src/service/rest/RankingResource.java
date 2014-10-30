package service.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import persistency.entities.LoggedUser;
import persistency.entities.PointsInstance;
import persistency.exposed.LoggedUserExposed;
import utils.PointsHelper;

import com.google.gson.Gson;

public class RankingResource {
	
	private Gson g = new Gson();
	
	@GET
	@Path("/ranking/{id}")
	public Response extractPlayerData(@PathParam("id") String id) throws Exception {
		LoggedUserExposed pe = new LoggedUserExposed();
		int pid = Integer.parseInt(id);
		pid = pid -42;
		LoggedUser personById = pe.findPersonById(pid+"");
		List<PointsInstance> playerStats = personById.getCodes();
		return Response.status(Response.Status.OK)
				.entity(g.toJson(playerStats)).build();
	}
	
	@GET
	@Path("/rankingRaw/{id}")
	public Response extractPlayerDataRaw(@PathParam("id") String id) throws Exception {
		LoggedUserExposed pe = new LoggedUserExposed();
		int pid = Integer.parseInt(id);
		pid = pid -42;
		LoggedUser personById = pe.findPersonById(pid+"");
		String result = "";
		List<PointsInstance> points = personById.getCodes();
		for (PointsInstance points2 : points) {
			result+="Points code: "+points2.getCategory().getName() +" points: "+points2.getCategory().getPoints() + "\n\r";
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
