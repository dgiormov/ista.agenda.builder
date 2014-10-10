package persistency.exposed.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.DateFormatter;

import persistency.entities.Session;
import persistency.entities.Speaker;
import persistency.exposed.SpeakerExposed;

/**
 * Entity implementation class for Entity: Event
 * 
 */
public class SessionJson {

	private int id;
	private String name;
	private String description;
	
	private String secretWord;

	private boolean isSelected = false;

	private int startTime;
	private int duration;

	private String room;

	private List<String> tags = new ArrayList<String>();

	private String searchTerms;
	
	private String startTimeString;

	private int userRating = 0;

	private int sessionRating = 0;
	

	private int totalRating = 0;
	private int timesRated = 0;

	private List<SpeakerJson> speakers;

	private int views = 0;
	
	private String date;
	private String track;


	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SpeakerJson> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<SpeakerJson> speakers) {
		this.speakers = speakers;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getUserRating() {
		return userRating;
	}

	public void setUserRating(int userRating) {
		this.userRating = userRating;
	}

	public int getSessionRating() {
		return sessionRating;
	}

	public void setSessionRating(int sessionRating) {
		this.sessionRating = sessionRating;
	}
	
	public int getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(int totalRating) {
		this.totalRating = totalRating;
	}

	public int getTimesRating() {
		return timesRated;
	}

	public void setTimesRating(int timesRating) {
		this.timesRated = timesRating;
	}

	public void addToTotalRating(int rate) {
		this.totalRating += rate;
		timesRated++;
	}

	public String getSecretWord() {
		return secretWord;
	}

	public void setSecretWord(String secretWord) {
		this.secretWord = secretWord;
	}

	public String getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	public String getStartTimeString() {
		return startTimeString;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public Session toEntity() throws ParseException{
		Session result = new Session();
		result.setId(id);
		result.setDescription(description);
		result.setDuration(duration);
		result.setName(name);
		result.setRoom(room);
		result.setStartTime(startTime);
		result.setStartTimeString(startTimeString);
		result.setTags(tags);
		result.setSpeakers(computeSpeakers(speakers));
		result.setTrack(getTrack());
		result.setDate((new SimpleDateFormat("d.MM.yyyy")).parse(getDate()));
		return result;
	}

	private List<Speaker> computeSpeakers(List<SpeakerJson> speakers2) {
		List<Speaker> result = new ArrayList<Speaker>();
		SpeakerExposed se = new SpeakerExposed();
		for (SpeakerJson speakerJson : speakers2) {
			if(speakerJson.getId().length() == 0){
				continue;
			}
			Speaker userById = se.findUserById(speakerJson.getId());
			if(userById != null){
				result.add(userById);	
			}
			
		}
		return result;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}
}
