package persistency.exposed;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import persistency.entities.admin.EnabledFunctionality;
import utils.DBUtils;

public class EnabledFuncExposed {

	public EntityManager entityManager = null;
	
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public EnabledFuncExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(EnabledFunctionality e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.merge(e);
		transaction.commit();
	}
	
	public EnabledFunctionality getEntity() {
		Query namedQuery = entityManager.createNamedQuery("all");
		EnabledFunctionality isEnabled = (EnabledFunctionality) namedQuery.getSingleResult();
		return isEnabled;
	}
	
	public boolean isEnabled() {
		Query namedQuery = entityManager.createNamedQuery("all");
		EnabledFunctionality isEnabled = (EnabledFunctionality) namedQuery.getSingleResult();
		if(isEnabled == null){
			return true;
		}
		return isEnabled.isEnabled();
	}
}
