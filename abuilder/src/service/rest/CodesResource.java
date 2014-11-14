package service.rest;

import gamification.ExecuteAction;

import java.io.IOException;
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
import admin.AppControl;

import com.google.gson.Gson;

@Path("codes")
@Produces({ MediaType.APPLICATION_JSON })
public class CodesResource {

	private Gson g;

	public CodesResource() {
		g = new Gson();
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response placeComment(@Context HttpServletRequest request, @PathParam("id") String sessionId) throws IOException{
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream(), writer, "UTF-8");
		String theString = writer.toString();
		Gson g = new Gson();
		Data fromJson = g.fromJson(theString, Data.class);
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if(person == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		utils.Status status = ExecuteAction.getInstance().execute(null, person, fromJson.code);
		return Response.ok(g.toJson(status)).build();
	}

	@XmlRootElement
	public class Data {
		private String code;
	}

}
