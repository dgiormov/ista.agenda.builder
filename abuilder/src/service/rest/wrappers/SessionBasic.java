package service.rest.wrappers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import persistency.entities.Session;
import persistency.entities.Speaker;

@XmlRootElement
public class SessionBasic implements Comparable<SessionBasic>{

	private int id;
	public int getId() {
		return id;
	}

	private String name;
	private String demo = "yes";
	private List<String> speakers;
	public List<String> getSpeakers() {
		return speakers;
	}

	private String room;
	private String searchTerms;
	private List<String> tags;
	private int startTime;
	private String startTimeString;
	private int duration;
	private String date;
	private String track;
	private boolean isSelected;

	public SessionBasic(Session e) {
		id = e.getId();
		setName(e.getName());
		speakers = getSpeakerNames(e.getSpeakers());
		room = e.getRoom();
		searchTerms = e.getSearchTerms();
		tags = e.getTags();
		startTime = e.getStartTime();
		startTimeString = e.getStartTimeString();
		duration = e.getDuration();
		track = e.getTrack();
		SimpleDateFormat df = new SimpleDateFormat("d.MM");
		setDate(df.format(e.getDate()));
		isSelected = e.isSelected();
	}
	
	private List<String> getSpeakerNames(List<Speaker> speakers2) {
		List<String> speakerNames = new ArrayList<String>();
		if(speakers2 != null){
			for (Speaker speaker : speakers2) {
				speakerNames.add(speaker.getName());
			}
		}
		return speakerNames;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int compareTo(SessionBasic o) {
		return this.getStartTime() - o.getStartTime();
	}

}
