package persistency.exposed;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import persistency.entities.PointsCategory;
import persistency.entities.PointsInstance;
import persistency.entities.LoggedUser;
import utils.DBUtils;

@SuppressWarnings("unchecked")
public class PointsCategoryExposed {

	public EntityManager entityManager = null;
	
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public PointsCategoryExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(PointsCategory e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(e);
		transaction.commit();
	}
	
	public void updateEntity(PointsCategory e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.merge(e);
		transaction.commit();
	}
	
	public List<PointsCategory> allCodes() {
		Query namedQuery = entityManager.createNamedQuery("allCategories");
		List<PointsCategory> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	
	public void deleteEntity(PointsCategory e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.remove(e);
		transaction.commit();
	}
}
