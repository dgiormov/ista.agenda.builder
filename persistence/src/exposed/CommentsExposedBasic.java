package exposed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

import rest.wrappers.CommentBasic;
import rest.wrappers.EventBasic;
import util.DBUtils;
import entities.Comment;
import entities.Event;
import entities.LoggedUser;

public class CommentsExposedBasic {

	public EntityManager entityManager = null;
	
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public CommentsExposedBasic() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(Comment e) {
		entityManager.getTransaction().begin();
		try {
			entityManager.persist(e);
			entityManager.getTransaction().commit();
		} catch (Exception e1) {
			entityManager.getTransaction().rollback();
		}
	}
	
	public void updateEntity(Comment e) {
		entityManager.getTransaction().begin();
		try {
			entityManager.merge(e);
			entityManager.getTransaction().commit();
		} catch (Exception e1) {
			entityManager.getTransaction().rollback();
		}
	}

	public List<CommentBasic> allEntities(String id) {
		Query namedQuery = entityManager.createNamedQuery("allComments");
		namedQuery.setParameter("id", Integer.parseInt(id));
		List<Comment> commentList = namedQuery.getResultList();
		List<CommentBasic> result = new ArrayList<CommentBasic>();
		for (Comment comment : commentList) {
			result.add(new CommentBasic(comment));
		}
		return result;
	}
	
	public List<CommentBasic> allEntities() {
		Query namedQuery = entityManager.createNamedQuery("allCommentsRaw");
		List<Comment> commentList = namedQuery.getResultList();
		List<CommentBasic> result = new ArrayList<CommentBasic>();
		for (Comment comment : commentList) {
			result.add(new CommentBasic(comment));
		}
		return result;
	}
	
	public Comment getComment(String id) {
		Query namedQuery = entityManager.createNamedQuery("commentById");
		namedQuery.setParameter("id", Integer.parseInt(id));
		return (Comment) namedQuery.getSingleResult();
	}
	
}
