package service.rest;

import gamification.ExecuteAction;
import gamification.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;

import persistency.entities.Comment;
import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.exposed.CommentsExposedBasic;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.SessionExposedBasic;
import service.rest.wrappers.CommentBasic;
import utils.Status.STATE;
import admin.AppControl;

import com.google.gson.Gson;

@Path("codes")
@Produces({ MediaType.APPLICATION_JSON })
public class CodesResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitCode(@Context HttpServletRequest request, @PathParam("id") String sessionId) throws IOException{
		Gson g = new Gson();
		if(!AppControl.writeMode(request)){
			return Response.status(Status.PRECONDITION_FAILED).entity(g.toJson(new utils.Status(STATE.ERROR, "Codes are disabled at the moment, try again later."))).build();
		}
		Data fromJson = g.fromJson(new InputStreamReader(request.getInputStream(), "UTF-8"), Data.class);
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if(person == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(!person.canEnterCodes()){
			ExecuteAction.getInstance().execute("hacker", person, null);
			return Response.status(Status.PRECONDITION_FAILED).entity(g.toJson(new utils.Status(STATE.ERROR, "User cannot enter more codes, 10 wrong attempts detected.<br/>One time award of 20 points will be given."))).build();
		}
		Player player = person.getPlayer();
		if(player.getRankInt() < 2){
			return Response.status(Status.PRECONDITION_FAILED).entity(g.toJson(new utils.Status(STATE.ERROR, "You have to reach level Gold in order to participate in the challenge."))).build();
		}
		utils.Status status = ExecuteAction.getInstance().execute(null, person, fromJson.code.toLowerCase());
		if(status.severity == STATE.OK){
			return Response.ok(g.toJson(status)).build();	
		} else {
			person.strike();
			pe.updateEntity(person);
			status.message = status.message + "<br> User will not be able to submit codes after " +person.getStrikesLeft() +" more wrong codes.";
			return Response.status(Status.NOT_ACCEPTABLE).entity(g.toJson(status)).build();
		}
		
	}

	@XmlRootElement
	public class Data {
		private String code;
	}

}
