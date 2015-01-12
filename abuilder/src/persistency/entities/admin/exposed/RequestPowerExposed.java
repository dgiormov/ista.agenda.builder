package persistency.entities.admin.exposed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import persistency.entities.LoggedUser;
import persistency.entities.admin.RequestPower;
import persistency.exposed.AbstractExposed;

public class RequestPowerExposed extends AbstractExposed<RequestPower> {

	public RequestPower findPersonById(long id) {
		Query namedQuery = entityManager.createNamedQuery("requestById");
		namedQuery.setParameter("id", id);
		RequestPower result = null;
		try {
			result = (RequestPower) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}

	public List<RequestPower> getAll() {
		Query namedQuery = entityManager.createNamedQuery("getAll");
		List<RequestPower> result = null;
		try {
			result = namedQuery.getResultList();
		} catch (NoResultException e) {
			result = new ArrayList<RequestPower>();
		}
		return result;
		
	}
}
