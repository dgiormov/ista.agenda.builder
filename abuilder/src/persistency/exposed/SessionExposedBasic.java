package persistency.exposed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
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
		entityManager.getTransaction().begin();
			entityManager.persist(e);
			entityManager.getTransaction().commit();
	}
	
	public void updateEntity(Session e) {
		entityManager.getTransaction().begin();
			entityManager.merge(e);
			entityManager.getTransaction().commit();
	}

	public List<SessionBasic> allEntities(HttpServletRequest request) {
		Query namedQuery = entityManager.createNamedQuery("allEventsSQL");
		List<Session> eventList = namedQuery.getResultList();
		List<SessionBasic> resultList = toSessionBasic(eventList, request);
		return resultList;
	}
	
	public List<SessionBasic> allEntitiesOnDate(HttpServletRequest hsr, int date) {
		List<SessionBasic> allEntities = allEntities(hsr);
		List<SessionBasic> entriesOnDate = new ArrayList<SessionBasic>();
		for (SessionBasic sessionBasic : allEntities) {
			if(sessionBasic.getDate().startsWith(date+".")){
				entriesOnDate.add(sessionBasic);
			}
		}
		return entriesOnDate;
	}
	
	public List<SessionBasic> allEntities() {
		Query namedQuery = entityManager.createNamedQuery("allEventsSQL");
		List<Session> eventList = namedQuery.getResultList();
		List<SessionBasic> resultList = toSessionBasic(eventList, null);
		return resultList;
	}

	private List<SessionBasic> toSessionBasic(List<Session> eventList, HttpServletRequest request) {
		List<SessionBasic> resultList = new ArrayList<SessionBasic>();
		LoggedUser p = null;
		if(request != null){
			LoggedUserExposed pe = new LoggedUserExposed();
			p = pe.getCurrentUser(request);
		}
		for (Session event : eventList) {
			if(p != null){
				event.setSelected(p.getSessions().contains(event));	
			}
			resultList.add(new SessionBasic(event));
		}
		return resultList;
	}
	
	private int getSpeakerRating(LoggedUser person, Session eventEntry) {
		if (person != null) {
			Map<Integer, Integer> sessions = person.getSpeakerRatings();
			if (sessions != null) {
				Set<Integer> sessionSet = sessions.keySet();
				for (Integer eventId : sessionSet) {
					if (eventId == eventEntry.getId()) {
						return sessions.get(eventId);
					}
				}
			}
		}
		return 0;
	}

	private boolean isSelected(LoggedUser person, Session eventEntry) {
		if (person != null) {
			List<Session> sessions = person.getSessions();
			for (Session event : sessions) {
				if (event.getId() == eventEntry.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	private String computeTimeRange(SessionBasic eventEntry) {
		int startTimeInt = eventEntry.getStartTime();
		String startTime = startTimeInt + "";
		String minutes = startTime.substring(startTime.length() - 2);
		int minInt = Integer.parseInt(minutes);
		int duration = eventEntry.getDuration();
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

	public Session findEventById(String id) {
		entityManager.getTransaction().begin();
		Session result = null;
		try {
			Query namedQuery = entityManager.createNamedQuery("getEventById");
			namedQuery.setParameter("id", Integer.parseInt(id));
			try {
				result = (Session) namedQuery.getSingleResult();
			} catch (NoResultException e) {
				result = null;
			}
		} finally {
			entityManager.getTransaction().commit();
		}
		return result;
	}
	
	public SessionWrapped findEventByIdWrapped(String id, LoggedUser p) {
		entityManager.getTransaction().begin();
		Session result = null;
		try {
			Query namedQuery = entityManager.createNamedQuery("getEventById");
			namedQuery.setParameter("id", Integer.parseInt(id));
			try {
				result = (Session) namedQuery.getSingleResult();
			} catch (NoResultException e) {
				result = null;
			}
		} finally {
			entityManager.getTransaction().commit();
		}
		return new SessionWrapped(result, p);
	}

	public void incEventViews(Session e) {
		e.setViews(e.getViews() + 1);
		createEntity(e);
	}
}
