package persistency.entities.admin.exposed;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import persistency.entities.admin.PowerUser;
import persistency.exposed.AbstractExposed;

public class PowerUserExposed extends AbstractExposed<PowerUser> {

	public boolean isPowerUser(long id) {
		Query namedQuery = entityManager.createNamedQuery("requestById");
		namedQuery.setParameter("id", id);
		PowerUser result = null;
		try {
			result = (PowerUser) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return result != null;
	}
}
