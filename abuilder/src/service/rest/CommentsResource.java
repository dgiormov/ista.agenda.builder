package service.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import rest.wrappers.CommentBasic;
import servlets.AppControl;

import com.sun.jersey.api.client.ClientResponse.Status;

import entities.Comment;
import entities.LoggedUser;
import entities.Session;
import exposed.CommentsExposedBasic;
import exposed.LoggedUserExposed;
import exposed.SessionExposedBasic;
import game.Score;
import game.ScoreCategories;

@Path("comments")
public class CommentsResource {

	@GET
	@Path("{id}")
	public List<CommentBasic> getCommentsForSession(@PathParam("id") String sessionId){
		CommentsExposedBasic commetnsExposed = new CommentsExposedBasic();
		return commetnsExposed.allEntities(sessionId);
	}
	
	@POST
	public Response placeComment(@Context HttpServletRequest request, @FormParam("sessionId") String sessionId){
		String text = request.getParameter("text");;
		String inReplyOf = request.getParameter("inReplyOf");;
		SessionExposedBasic ee = new SessionExposedBasic();
		Session event = ee.findEventById(sessionId);
		LoggedUserExposed pe = new LoggedUserExposed();
		LoggedUser person = pe.getCurrentUser(request);
		if(request.getUserPrincipal() == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(event == null || person == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		CommentsExposedBasic ce = new CommentsExposedBasic();
		Comment c = new Comment();
		c.setEvent(event);
		c.setCowner(person);
		c.setInReplyTo(Integer.parseInt(inReplyOf));
		c.setText(text);
		ce.createEntity(c);
		person.addComment(c);
		pe.updateEntity(person);
		return Response.ok().build();
	}
	
	@POST
	@Path("{id}/likes")
	public Response rateSpeaker(@Context HttpServletRequest request, @PathParam("id") String commentId, @FormParam("eventId") String eventId){
		if(!AppControl.writeMode()){
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
			if(comment != null && !p.getLikedComments().contains(comment)){
				if(comment.getCowner().getId() == p.getId()){
					return Response.status(Status.NOT_ACCEPTABLE).entity("This is your comment remember?").build();
				}
				p.getLikedComments().add(comment);
				pe.updateEntity(p);
				comment.incLikes();
				ce.updateEntity(comment);
				LoggedUser cowner = pe.findPersonById(comment.getCowner().getId()+"");
//				FIXME
//				cowner.addPoints(ScoreCategories.LIKE_MY_COMMENT, Score.LIKED);
				if(comment.getLikes() > 3){
//					cowner.addPoints(ScoreCategories.LIKED_BY5, Score.LIKED5);
				}
				pe.updateEntity(cowner);
			}
		}
		return Response.ok().build();
	}
	
}
