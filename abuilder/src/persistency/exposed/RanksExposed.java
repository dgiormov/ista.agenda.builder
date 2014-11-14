package persistency.exposed;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import persistency.entities.gamification.Rank;
import utils.DBUtils;

@SuppressWarnings("unchecked")
public class RanksExposed {

	public EntityManager entityManager = null;
	
	public RanksExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(Rank e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(e);
		transaction.commit();
	}
	
	public void updateEntity(Rank e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.merge(e);
		transaction.commit();
	}
	
	
	public void deleteEntity(Rank e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.remove(e);
		transaction.commit();
	}

}
