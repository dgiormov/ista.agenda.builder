package exposed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

import rest.wrappers.EventBasic;
import rest.wrappers.EventWrapped;
import util.DBUtils;
import entities.Event;
import entities.LoggedUser;

public class EventExposedBasic {

	public EntityManager entityManager = null;
	public static final String JTA_PU_NAME = "statCreateTablesJTA";

	public EventExposedBasic() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(Event e) {
		entityManager.getTransaction().begin();
			entityManager.persist(e);
			entityManager.getTransaction().commit();
	}
	
	public void updateEntity(Event e) {
		entityManager.getTransaction().begin();
			entityManager.merge(e);
			entityManager.getTransaction().commit();
	}

	public List<TimeTableEntry> allEntities(HttpServletRequest request) {
		Query namedQuery = entityManager.createNamedQuery("allEventsSQL");
		List<Event> eventList = namedQuery.getResultList();
		List<EventBasic> resultList = toEventBasic(eventList, request);
		
		return transformToTimeTable(resultList);
	}
	
	public List<TimeTableEntry> allEntities() {
		Query namedQuery = entityManager.createNamedQuery("allEventsSQL");
		List<Event> eventList = namedQuery.getResultList();
		List<EventBasic> resultList = toEventBasic(eventList, null);
		
		return transformToTimeTable(resultList);
	}

	private List<EventBasic> toEventBasic(List<Event> eventList, HttpServletRequest request) {
		List<EventBasic> resultList = new ArrayList<EventBasic>();
		LoggedUser p = null;
		if(request != null){
			LoggedUserExposed pe = new LoggedUserExposed();
			p = pe.getCurrentUser(request);
		}
		for (Event event : eventList) {
			if(p != null){
				event.setSelected(p.getSessions().contains(event));	
			}
			resultList.add(new EventBasic(event));
		}
		return resultList;
	}
	
	private List<TimeTableEntry> transformToTimeTable(List<EventBasic> resultList) {
		List<TimeTableEntry> result = new ArrayList<TimeTableEntry>();
		for (EventBasic eventEntry : resultList) {
			TimeTableEntry tte = findTimeTableEntry(result, eventEntry);
			if (tte == null) {
				tte = new TimeTableEntry(eventEntry.getStartTime());
				tte.setTimeRange(computeTimeRange(eventEntry));
				tte.getEvents().add(eventEntry);
				result.add(tte);
			} else {
				tte.getEvents().add(eventEntry);
			}
		}
		Collections.sort(result);
		return result;
	}

	private int getSpeakerRating(LoggedUser person, Event eventEntry) {
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

	private boolean isSelected(LoggedUser person, Event eventEntry) {
		if (person != null) {
			List<Event> sessions = person.getSessions();
			for (Event event : sessions) {
				if (event.getId() == eventEntry.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	private String computeTimeRange(EventBasic eventEntry) {
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

	private TimeTableEntry findTimeTableEntry(List<TimeTableEntry> timeTable,
			EventBasic eventEntry) {
		for (TimeTableEntry timeTableEntry : timeTable) {
			if (timeTableEntry.getStartTime() == eventEntry.getStartTime()) {
				return timeTableEntry;
			}
		}
		return null;
	}

	public Event findEventById(String id) {
		entityManager.getTransaction().begin();
		Event result = null;
		try {
			Query namedQuery = entityManager.createNamedQuery("getEventById");
			namedQuery.setParameter("id", Integer.parseInt(id));
			try {
				result = (Event) namedQuery.getSingleResult();
			} catch (NoResultException e) {
				result = null;
			}
		} finally {
			entityManager.getTransaction().commit();
		}
		return result;
	}
	
	public EventWrapped findEventByIdWrapped(String id, LoggedUser p) {
		entityManager.getTransaction().begin();
		Event result = null;
		try {
			Query namedQuery = entityManager.createNamedQuery("getEventById");
			namedQuery.setParameter("id", Integer.parseInt(id));
			try {
				result = (Event) namedQuery.getSingleResult();
			} catch (NoResultException e) {
				result = null;
			}
		} finally {
			entityManager.getTransaction().commit();
		}
		return new EventWrapped(result, p);
	}

	public void incEventViews(Event e) {
		e.setViews(e.getViews() + 1);
		createEntity(e);
	}

}
