package exposed;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import util.DBUtils;
import entities.Code;
import entities.LoggedUser;

@SuppressWarnings("unchecked")
public class CodesExposed {

	public EntityManager entityManager = null;
	
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public CodesExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(Code e) {
		entityManager.getTransaction().begin();
		entityManager.persist(e);
		entityManager.getTransaction().commit();
	}
	
	public void updateEntity(Code e) {
		entityManager.getTransaction().begin();
		entityManager.merge(e);
		entityManager.getTransaction().commit();
	}
	
	public void updateEntityRAW(Code e) {
		entityManager.merge(e);
		entityManager.getTransaction().commit();
	}

	public List<Code> allCodes() {
		Query namedQuery = entityManager.createNamedQuery("allCodes");
		List<Code> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	public List<Code> freeCodes(int type) {
		Query namedQuery = entityManager.createNamedQuery("freeCodes");
		namedQuery.setParameter("type", type);
		namedQuery.setMaxResults(10);
		List<Code> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	public Code getCode(String code) {
		Query namedQuery = entityManager.createNamedQuery("getCode");
		namedQuery.setParameter("code", code);
		return (Code) namedQuery.getSingleResult();
	}
	
	public List<Code> getCodesByType(String type, LoggedUser p) {
		Query namedQuery = entityManager.createNamedQuery("codesByType");
		namedQuery.setParameter("type", Integer.parseInt(type));
		namedQuery.setParameter("person", p);
		List<Code> codeList = namedQuery.getResultList();
		return codeList;
	}
	
	public void deleteEntity(Code e) {
		entityManager.getTransaction().begin();
		entityManager.remove(e);
		entityManager.getTransaction().commit();
	}
}
