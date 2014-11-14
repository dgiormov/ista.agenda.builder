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
import utils.Status.STATE;
import admin.AppControl;

import com.google.gson.Gson;

@Path("comments")
@Produces({ MediaType.APPLICATION_JSON })
public class CommentsResource {

	private Gson g;

	public CommentsResource() {
		g = new Gson();
	}

	@GET
	@Path("{id}")
	public Response getCommentsForSession(@Context HttpServletRequest request, @PathParam("id") String sessionId){
		CommentsExposedBasic commetnsExposed = new CommentsExposedBasic();
		LoggedUserExposed lue = new LoggedUserExposed();
		List<CommentBasic> allEntities = commetnsExposed.allEntities(sessionId, lue.getCurrentUser(request));
		return Response.status(Response.Status.OK)
				.entity(g.toJson(allEntities)).build();
	}

	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response placeComment(@Context HttpServletRequest request, @PathParam("id") String sessionId) throws IOException{
		Gson g = new Gson();
		if (!AppControl.writeMode(request)) {
			return Response.status(405).entity(g.toJson(new utils.Status(STATE.ERROR, "Comments will be ebabled a few days be. "))).build();
		}
		String inReplyOf = request.getParameter("inReplyOf");;
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream(), writer, "UTF-8");
		String theString = writer.toString();
		
		Data fromJson = g.fromJson(theString, Data.class);
		SessionExposedBasic ee = new SessionExposedBasic();
		Session session = ee.findSessionById(sessionId);
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		//		if(request.getUserPrincipal() == null){
		//			return Response.status(Status.BAD_REQUEST).build();
		//		}
		if(session == null || person == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		CommentsExposedBasic ce = new CommentsExposedBasic();
		Comment c = new Comment();
		c.setSession(session);
		c.setCowner(person);
		//		c.setInReplyTo(Integer.parseInt(inReplyOf));
		c.setText(fromJson.getData());
		ce.createEntity(c);
		person.addComment(c);
		pe.updateEntity(person);
		ExecuteAction.getInstance().execute("comment", person, null);
		return Response.ok().build();
	}

	@POST
	@Path("{id}/like")
	public Response likeUnlike(@Context HttpServletRequest request, @PathParam("id") Integer commentId){
		if(!AppControl.writeMode(request)){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(commentId == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser p = pe.getCurrentUser(request);
		if(p != null){
			CommentsExposedBasic ce = new CommentsExposedBasic();
			Comment comment = ce.getComment(commentId);
			if(comment != null){
				if(comment.getCowner().getId() == p.getId()){
					//FIXME do not allow it.
				} 
				if(p.getLikedComments().contains(comment)){
					return Response.status(400).build();
//					p.getLikedComments().remove(comment);
//					if(comment.getLikedBy().contains(p)){
//						comment.getLikedBy().remove(p);
//					}
				} else {
					p.getLikedComments().add(comment);
					if(!comment.getLikedBy().contains(p)){
						comment.getLikedBy().add(p);
					}
				}
				pe.updateEntity(p);
				ce.updateEntity(comment);
				LoggedUser cowner = pe.findPersonById(comment.getCowner().getId());
				ExecuteAction.getInstance().execute("liked", cowner, null);
				if(comment.getLikes() >= 5){
					cowner.updateLiked5Points(comment);	
				}

				pe.updateEntity(cowner);
			}
		}
		return Response.ok().build();
	}

	@XmlRootElement
	public class Data {
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

}
