package persistency.exposed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import persistency.entities.LoggedUser;
import persistency.entities.Session;
import service.rest.wrappers.SessionBasic;
import service.rest.wrappers.SessionWrapped;
import utils.DBUtils;

public class SessionExposedBasic {

	public EntityManager entityManager = null;
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public SessionExposedBasic() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(Session e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(e);
		transaction.commit();
	}
	
	public void updateEntity(Session e) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.merge(e);
		transaction.commit();
	}

	public List<SessionBasic> allEntities(HttpServletRequest request) {
		Query namedQuery = entityManager.createNamedQuery("allSessionsSQL");
		List<Session> sessionList = namedQuery.getResultList();
		List<SessionBasic> resultList = toSessionBasic(sessionList, request);
		return resultList;
	}
	
	public List<Session> allEntitiesRaw() {
		Query namedQuery = entityManager.createNamedQuery("allSessionsSQL");
		List<Session> sessionList = namedQuery.getResultList();
		return sessionList;
	}
	
	public List<SessionBasic> allEntitiesOnDate(HttpServletRequest hsr, int date) {
		List<SessionBasic> allEntities = allEntities(hsr);
		List<SessionBasic> entriesOnDate = new ArrayList<SessionBasic>();
		for (SessionBasic sessionBasic : allEntities) {
			if(sessionBasic.getDate().startsWith(date+".")){
				List<String> speakers = sessionBasic.getSpeakers();
				if(speakers.size() > 1){
					for (int i = 0; i < speakers.size(); i++) {
						if(i+1<speakers.size()){
							speakers.set(i, speakers.get(i)+",");
						}
					}
				}
				entriesOnDate.add(sessionBasic);
			}
		}
		return entriesOnDate;
	}
	
	public List<SessionBasic> allEntities() {
		Query namedQuery = entityManager.createNamedQuery("allSessionsSQL");
		List<Session> sessionList = namedQuery.getResultList();
		List<SessionBasic> resultList = toSessionBasic(sessionList, null);
		return resultList;
	}

	private List<SessionBasic> toSessionBasic(List<Session> sessionList, HttpServletRequest request) {
		List<SessionBasic> resultList = new ArrayList<SessionBasic>();
		LoggedUser p = null;
		if(request != null){
			LoggedUserExposed pe = new LoggedUserExposed();
			p = pe.getCurrentUser(request);
		}
		for (Session session : sessionList) {
			if(p != null){
				session.setSelected(p.getSessions().contains(session));	
			}
			resultList.add(new SessionBasic(session));
		}
		Collections.sort(resultList);
		return resultList;
	}
	
	private int getSpeakerRating(LoggedUser person, Session sessionEntry) {
		if (person != null) {
			Map<Integer, Integer> sessions = person.getSpeakerRatings();
			if (sessions != null) {
				Set<Integer> sessionSet = sessions.keySet();
				for (Integer sessionId : sessionSet) {
					if (sessionId == sessionEntry.getId()) {
						return sessions.get(sessionId);
					}
				}
			}
		}
		return 0;
	}

	private boolean isSelected(LoggedUser person, Session sessionEntry) {
		if (person != null) {
			List<Session> sessions = person.getSessions();
			for (Session session : sessions) {
				if (session.getId() == sessionEntry.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	private String computeTimeRange(SessionBasic sessionEntry) {
		int startTimeInt = sessionEntry.getStartTime();
		String startTime = startTimeInt + "";
		String minutes = startTime.substring(startTime.length() - 2);
		int minInt = Integer.parseInt(minutes);
		int duration = sessionEntry.getDuration();
		int durationIndex = 1;
		int endTime = 0;
		if (minInt + duration >= 60) {
			durationIndex = (duration + minInt) / 60;
			endTime = startTimeInt + durationIndex * 40 + duration;
		} else {
			endTime = startTimeInt + duration;
		}
		String endTimeStr = endTime + "";

		return (startTimeInt / 100) + ":" + minutes; // +" - "+(endTime/100)+":"+endTimeStr.substring(endTimeStr.length()-2);
	}

	public Session findSessionById(String id) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		Session result = null;
		try {
			Query namedQuery = entityManager.createNamedQuery("getSessionById");
			namedQuery.setParameter("id", Integer.parseInt(id));
			try {
				result = (Session) namedQuery.getSingleResult();
			} catch (NoResultException e) {
				result = null;
			}
		} finally {
			transaction.commit();
		}
		return result;
	}
	
	public SessionWrapped findSessionByIdWrapped(String id, LoggedUser p) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		Session result = null;
		try {
			Query namedQuery = entityManager.createNamedQuery("getSessionById");
			namedQuery.setParameter("id", Integer.parseInt(id));
			try {
				result = (Session) namedQuery.getSingleResult();
			} catch (NoResultException e) {
				result = null;
			}
		} finally {
			transaction.commit();
		}
		return result == null ? null : new SessionWrapped(result, p);
	}

	public void incSessionViews(Session e) {
		e.setViews(e.getViews() + 1);
		createEntity(e);
	}
}
