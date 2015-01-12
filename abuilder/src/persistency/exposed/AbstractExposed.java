package persistency.exposed;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import utils.DBUtils;

public abstract class AbstractExposed<T> {

	protected EntityManager entityManager = null;
	
	public AbstractExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}
	
	public void createEntity(T e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(e);
		transaction.commit();
	}
	
	public void updateEntity(T e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.merge(e);
		transaction.commit();
	}
	
	public void deleteEntity(T e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.remove(e);
		transaction.commit();
	}
}