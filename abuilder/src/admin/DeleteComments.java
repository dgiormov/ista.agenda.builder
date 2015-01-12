package admin;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.Comment;
import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.exposed.CommentsExposedBasic;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.PointsExposed;
import persistency.exposed.json.PointsInstanceJson;
import service.rest.wrappers.CommentBasic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class DeleteComments
 */
public class DeleteComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson g= new Gson();
		List<CommentBasic> commentsToDelete = g.fromJson(request.getReader(),  new TypeToken<List<CommentBasic>>(){}.getType());
		if(commentsToDelete != null){
			String templateLike = "like on comment: ";
			String templateLiked = "liked on comment: ";
			String templateLiked5 = "liked5 on comment: ";
			CommentsExposedBasic ceb = new CommentsExposedBasic();
			PointsCategoryExposed pce = new PointsCategoryExposed();
			PointsCategory likeCategory = pce.findCategoryByShortName("like");
			PointsCategory likedCategory = pce.findCategoryByShortName("liked");
			PointsCategory liked5Category = pce.findCategoryByShortName("liked5");
			for (CommentBasic commentBasic : commentsToDelete) {
				Comment comment = ceb.getComment(commentBasic.getId());
				EntityManager entityManager = ceb.entityManager;
				EntityTransaction transaction = entityManager.getTransaction();
				transaction.begin();
				comment.getCowner().getComments().remove(comment);
				entityManager.merge(comment.getCowner());
				List<LoggedUser> likedBy = comment.getLikedBy();
				for (LoggedUser loggedUser : likedBy) {
					loggedUser.getLikedComments().remove(comment);
					List<PointsInstance> likesPoints = loggedUser.findAllPointsWithDescription(likeCategory, templateLike);
					removePoints(likeCategory, likesPoints, loggedUser);
					
					List<PointsInstance> likedPoints = loggedUser.findAllPointsWithDescription(likedCategory, templateLiked);
					removePoints(likedCategory, likedPoints, loggedUser);
					//liked point are removed from the correct user?
					
					
					List<PointsInstance> liked5Points = loggedUser.findAllPointsWithDescription(liked5Category, templateLiked5);
					removePoints(liked5Category, liked5Points, loggedUser);
					entityManager.merge(loggedUser);
					entityManager.merge(likeCategory);
					entityManager.merge(likedCategory);
					entityManager.merge(liked5Category);
				}
				entityManager.remove(comment);
				transaction.commit();
			}
		}
	}

	private void removePoints(PointsCategory likeCategory,
			List<PointsInstance> likesPoints, LoggedUser loggedUser) {
		if(likesPoints != null){
			for (PointsInstance pointsInstance : likesPoints) {
				likeCategory.getInstancesOfThisType().remove(pointsInstance);
			}
			loggedUser.getPointsInstances().removeAll(likesPoints);
		}
		
	}

}
