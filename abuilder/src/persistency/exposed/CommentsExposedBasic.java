package persistency.exposed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import persistency.entities.Comment;
import persistency.entities.LoggedUser;
import service.rest.wrappers.CommentBasic;
import utils.DBUtils;

public class CommentsExposedBasic extends AbstractExposed<Comment>{

	public EntityManager entityManager = null;

	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public CommentsExposedBasic() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public List<CommentBasic> allEntities(String id, LoggedUser lu) {
		Query namedQuery = entityManager.createNamedQuery("allComments");
		namedQuery.setParameter("id", Integer.parseInt(id));
		List<Comment> commentList = namedQuery.getResultList();
		List<CommentBasic> result = new ArrayList<CommentBasic>();
		for (Comment comment : commentList) {
			entityManager.refresh(comment);
			result.add(new CommentBasic(comment, lu != null && lu.getLikedComments().contains(comment)));
		}
		return result;
	}

	public List<CommentBasic> allEntities(LoggedUser lu) {
		Query namedQuery = entityManager.createNamedQuery("allCommentsRaw");
		List<Comment> commentList = namedQuery.getResultList();
		List<CommentBasic> result = new ArrayList<CommentBasic>();
		for (Comment comment : commentList) {
			result.add(new CommentBasic(comment, lu != null && lu.getLikedComments().contains(comment)));
		}
		return result;
	}
	
	public List<Comment> allEntities() {
		Query namedQuery = entityManager.createNamedQuery("allCommentsRaw");
		return namedQuery.getResultList();
	}

	public Comment getComment(Integer id) {
		Query namedQuery = entityManager.createNamedQuery("commentById");
		namedQuery.setParameter("id", id);
		return (Comment) namedQuery.getSingleResult();
	}

}
