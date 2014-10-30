package service.rest.wrappers;

import java.util.Date;
import java.util.List;

import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.entities.Speaker;

public class SessionWrapped {
	
	private int id;
	private String name;
	private List<Speaker> speakers;
	private String room;
	private String searchTerms;
	private List<String> tags;
	private int startTime;
	private String startTimeString;
	private int duration;
	private boolean isSelected;
	private String description;
	private Date date;
	private String track;
	private boolean hasSecret;
	private int rating;

	public SessionWrapped(Session e, LoggedUser p) {
		id = e.getId();
		name = e.getName();
		speakers = e.getSpeakers();
		room = e.getRoom();
		searchTerms = e.getSearchTerms();
		tags = e.getTags();
		startTime = e.getStartTime();
		duration = e.getDuration();
		isSelected = false;
		if(p != null){
			isSelected = p.getSessions().contains(e);
		}
		description = e.getDescription();
		startTimeString = (startTime+"");
		startTimeString = startTimeString.substring(0, startTimeString.length()-2)+":"+startTimeString.substring(startTimeString.length()-2);
		boolean isAnsweredAlready = false;
		track = e.getTrack();
		date = e.getDate();
		rating = 0;
		if(p != null){
			isAnsweredAlready = p.getViktorina().get(e.getId()) != null;
			Integer integer = p.getSessionRatings().get(e.getId());
			rating = integer == null ? 0 : integer;
		}
		hasSecret = e.getSecretWord()!=null &&  !isAnsweredAlready;
	}
}
