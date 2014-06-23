package exposed;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import util.DBUtils;
import entities.Speaker;

@SuppressWarnings("unchecked")
public class SpeakerExposed {

	private static SpeakerExposed instance;
	public EntityManager entityManager = null;
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public SpeakerExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(Speaker e) {
		if (findUserById(e.getId()) != null) {
			return;
		}
		entityManager.getTransaction().begin();
		entityManager.persist(e);
		entityManager.getTransaction().commit();

	}

	public void updateEntity(Speaker e) {
		Speaker findUserById = findUserById(e.getId());
		if (findUserById != null) {
			entityManager.getTransaction().begin();
			entityManager.persist(e);
			entityManager.getTransaction().commit();
		}

	}

	public List<Speaker> allEntities() {
		Query namedQuery = entityManager.createNamedQuery("allUsersSQL");
		List<Speaker> resultList = namedQuery.getResultList();
		return resultList;
	}

	public Speaker findUserById(String id) {
		Query namedQuery = entityManager.createNamedQuery("getUserById");
		namedQuery.setParameter("id", id);
		Speaker singleResult = null;
		try {
			singleResult = (Speaker) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			singleResult = null;
		}
		return singleResult;
	}

	public void updateClickedUpon(Speaker p) {
		p.setClickedUpon(p.getClickedUpon() + 1);
		updateEntity(p);
	}

	public static SpeakerExposed getInstance() {
		if (instance != null) {
			return instance;
		}
		return new SpeakerExposed();
	}
}
