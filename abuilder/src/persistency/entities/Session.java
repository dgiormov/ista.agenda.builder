package persistency.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: Event
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "allEventsSQL", query = "SELECT e FROM Session e"),
		@NamedQuery(name = "getEventById", query = "SELECT e FROM Session e WHERE e.id = :id") })
public class Session implements Serializable {

	@Override
	public String toString() {
		return "Session [id=" + id + ", Name=" + name + "]";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	@Column(length = 10000)
	private String description;
	
	private String secretWord;

	@Transient
	private boolean isSelected = false;

	private String track;
	@Temporal(TemporalType.DATE)
	private Date dateOn;
	
	private int startTime;
	private int duration;

	private String room;

	private List<String> tags = new ArrayList<String>();

	@Transient
	private String searchTerms;
	
	@Transient
	private String startTimeString;

	@Transient
	private int userRating = 0;

	@Transient
	private int sessionRating = 0;
	

	private int totalRating = 0;
	private int timesRated = 0;

	@OneToMany
	private List<Speaker> speakers;

	private int views = 0;

	private static final long serialVersionUID = 1L;

	public Session() {
		super();
	}

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

	public List<Speaker> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<Speaker> speakers) {
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

	public String getSearchTerms() {
		if (this.searchTerms == null) {
			StringBuffer result = new StringBuffer();
			// Add name
			result.append(this.getName());
			result.append(" ");

			// Add all presentors
			for (Speaker presentor : this.getSpeakers()) {
				result.append(presentor.getName());
				result.append(" ");
			}

			List<String> tags2 = getTags();
			if (tags2 != null) {
				for (String string : tags2) {
					result.append(string);
					result.append(" ");
				}
			}

			// Add room name
			result.append(this.getRoom());

			this.searchTerms = result.toString();
		}
		return this.searchTerms;
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

	@Override
	public boolean equals(Object obj) {
		if (Session.class.isInstance(obj)) {
			Session newEvent = (Session) obj;
			return getId() == newEvent.getId();
		}
		return super.equals(obj);
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

	public String getStartTimeString() {
		return startTimeString;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public Date getDate() {
		return dateOn;
	}

	public void setDate(Date date) {
		this.dateOn = date;
	}

}
