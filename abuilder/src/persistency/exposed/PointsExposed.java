package persistency.exposed;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import persistency.entities.PointsInstance;
import persistency.entities.LoggedUser;
import utils.DBUtils;

@SuppressWarnings("unchecked")
public class PointsExposed {

	public EntityManager entityManager = null;
	
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public PointsExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(PointsInstance e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(e);
		transaction.commit();
	}
	
	public void updateEntity(PointsInstance e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.merge(e);
		transaction.commit();
	}
	
	public List<PointsInstance> allCodes() {
		Query namedQuery = entityManager.createNamedQuery("allCodes");
		List<PointsInstance> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	public List<PointsInstance> freeCodes(int type) {
		Query namedQuery = entityManager.createNamedQuery("freeCodes");
		namedQuery.setParameter("type", type);
		namedQuery.setMaxResults(10);
		List<PointsInstance> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	public PointsInstance getCode(String code) {
		Query namedQuery = entityManager.createNamedQuery("getCode");
		namedQuery.setParameter("code", code);
		return (PointsInstance) namedQuery.getSingleResult();
	}
	
	public List<PointsInstance> getCodesByType(String type, LoggedUser p) {
		Query namedQuery = entityManager.createNamedQuery("codesByType");
		namedQuery.setParameter("type", Integer.parseInt(type));
		namedQuery.setParameter("person", p);
		List<PointsInstance> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	public void deleteEntity(PointsInstance e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.remove(e);
		transaction.commit();
	}

	public void persistEntities(List<PointsInstance> pi) {
		for (PointsInstance pointsInstance : pi) {
			createEntity(pointsInstance);
		}
		
	}
}
