package rest.wrappers;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import entities.Session;
import entities.Speaker;

@XmlRootElement
public class SessionBasic {

	private int id;
	public int getId() {
		return id;
	}

	private String name;
	private String demo = "yes";
	private List<Speaker> speakers;
	private String room;
	private String searchTerms;
	private List<String> tags;
	private int startTime;
	private String startTimeString;
	private int duration;
	private boolean isSelected;

	public SessionBasic(Session e) {
		id = e.getId();
		setName(e.getName());
		speakers = e.getSpeakers();
		room = e.getRoom();
		searchTerms = e.getSearchTerms();
		tags = e.getTags();
		startTime = e.getStartTime();
		duration = e.getDuration();
		isSelected = e.isSelected();
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getDuration() {
		return duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
