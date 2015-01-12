package persistency.exposed.feedback;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import persistency.entities.feedback.Question;
import persistency.exposed.AbstractExposed;

public class QuestionExposed extends AbstractExposed<Question> {

	public Question findQuestionById(int id) {
		Query namedQuery = entityManager.createNamedQuery("getQuestionById");
		namedQuery.setParameter("id", id);
		Question result = null;
		try {
			result = (Question) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;

	}

}
