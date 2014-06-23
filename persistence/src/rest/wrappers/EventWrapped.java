package rest.wrappers;

import java.util.List;

import entities.Event;
import entities.Speaker;
import entities.LoggedUser;

public class EventWrapped {
	
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
	private boolean hasSecret;

	public EventWrapped(Event e, LoggedUser p) {
		id = e.getId();
		name = e.getName();
		speakers = e.getSpeakers();
		room = e.getRoom();
		searchTerms = e.getSearchTerms();
		tags = e.getTags();
		startTime = e.getStartTime();
		duration = e.getDuration();
		isSelected = e.isSelected();
		description = e.getDescription();
		startTimeString = (startTime+"");
		startTimeString = startTimeString.substring(0, startTimeString.length()-2)+":"+startTimeString.substring(startTimeString.length()-2);
		boolean isAnsweredAlready = false;
		if(p != null){
			isAnsweredAlready = p.getViktorina().get(e.getId()) != null;
		}
		hasSecret = e.getSecretWord()!=null &&  !isAnsweredAlready;
	}
}
