package persistency.exposed.feedback;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import persistency.entities.feedback.Answer;
import persistency.entities.gamification.PointsCategory;
import persistency.exposed.AbstractExposed;

public class AnswerExposed extends AbstractExposed<Answer> {

	public List<Answer> findAnswersOfUser(long userid) {
		Query namedQuery = entityManager.createNamedQuery("getAnswersByUserId");
		namedQuery.setParameter("userid", userid);
		 List<Answer> result = null;
		try {
			result = namedQuery.getResultList();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	public List<Answer> allAnswers() {
		Query namedQuery = entityManager.createNamedQuery("allAnswers");
		 List<Answer> result = null;
		try {
			result = namedQuery.getResultList();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	
}
