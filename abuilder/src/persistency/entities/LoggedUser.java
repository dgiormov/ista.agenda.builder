package persistency.entities;

import gamification.Player;
import gamification.Score;
import gamification.ScoreCategories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.Index;

import persistency.exposed.PointsExposed;
import utils.PointsHelper;
import utils.Status;
import utils.Status.STATE;

/**
 * Entity implementation class for Entity: Person
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getPersonById", query = "SELECT e FROM LoggedUser e WHERE e.id = :id"),
		@NamedQuery(name = "getPersonByOpenId", query = "SELECT e FROM LoggedUser e WHERE e.openId = :openId"),
		@NamedQuery(name = "getPersonByEmail", query = "SELECT e FROM LoggedUser e WHERE e.email = :email"),
		@NamedQuery(name = "getPersonByUserName", query = "SELECT e FROM LoggedUser e WHERE e.userName = :userName"),
		@NamedQuery(name = "getAllPersons", query = "SELECT e FROM LoggedUser e"),
		@NamedQuery(name = "getPersonByAccessToken", query = "SELECT e FROM LoggedUser e WHERE e.accessToken = :aToken") })
public class LoggedUser implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;

	@Index(unique=true)
	private String email;
	
	@Index(unique=true)
	private String openId;
	
	private long sessionExpires;

	@Index(unique=true)
	private String accessToken;
	@Index
	private String secretAccess;
	
	private Set<String> platform = new HashSet<String>();
	
	/**
	 * ten attempts for cheating, after that ban...
	 */
	private int strikes = 10;

	@OneToMany
	private List<Session> sessions = new ArrayList<Session>();
	
	@OneToOne(mappedBy="enteredBy")
	private PointsInstance puzzlePiece;
	
	@OneToMany(mappedBy="enteredBy")
	private List<PointsInstance> codes = new ArrayList<PointsInstance>();
	
	@ManyToMany(mappedBy="likedBy")
	private List<Comment> likedComments = new ArrayList<Comment>();
	
	@OneToMany(mappedBy="cowner")
	private List<Comment> comments = new ArrayList<Comment>();
	
	@ElementCollection
	private Map<Integer, Integer> sessionRatings = new HashMap<Integer, Integer>();
	
	@ElementCollection
	private Map<Integer, Integer> speakerRatings = new HashMap<Integer, Integer>();
	
	@ElementCollection
	private Map<Integer, String> viktorina = new HashMap<Integer, String>();
	
	private static final long serialVersionUID = 1L;

	@Index
	private String userName;

	public String getUserName() {
		return userName;
	}

	public LoggedUser() {
		super();
	}

	public long getId() {
		return this.id;
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public Map<Integer, Integer> getSessionRatings() {
		return sessionRatings;
	}

	public Set<String> getPlatform() {
		return platform;
	}

	public void addPlatform(String platform) {
		this.platform.add(platform);
	}

	public boolean isActive() {
		return getSessionRatings() != null && getSessionRatings().size() > 0
				|| getSessions() != null && getSessions().size() > 1;
	}
	

	public Map<Integer, Integer> getSpeakerRatings() {
		return speakerRatings;
	}

	public void setSpeakerRatings(Map<Integer, Integer> speakerRatings) {
		this.speakerRatings = speakerRatings;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public List<Comment> getLikedComments() {
		return likedComments;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public Player getPlayer() {
		return new Player(this);
	}
	
	public boolean canEnterCodes(){
		return strikes>0;
	}
	
	public int getStrikesLeft(){
		return strikes;
	}
	
	public void strike(){
		strikes -= 1;
	}
	
	public void resetStrikes(){
		strikes = 10;
	}
	
	public boolean canPlay(){
		return getName() != null;
	}
	
	public Status addCode(PointsInstance code){
		if(code.isUsed()){
			return new Status(STATE.ERROR, "This code has been used.");
		}
		if(code.getEnteredBy() != null && !this.getUserName().equals(code.getEnteredBy().getUserName())) {
			return new Status(STATE.ERROR, "This code has been used by "+code.getEnteredBy().getName());
		}
		int maxCodesOfThisType = code.getCategory().getMaxInstacesPerPerson();
		List<PointsInstance> allCodesOfAType = findAllCodesOfAType(code.getCategory());
		if(allCodesOfAType.size() >= maxCodesOfThisType) {
			return new Status(STATE.ERROR, "Sorry the rules do not allow you to enter more codes of this type ("+code.getCategory().getName()+").");
		}
		PointsExposed ce = new PointsExposed();
		code.setUsed(true);
		code.setEnteredBy(this);
		ce.updateEntity(code);
		if(!getCodes().contains(code)){
			getCodes().add(code);	
		}
		return new Status(STATE.OK, code.getCategory().getPoints()); 
	}

	private List<PointsInstance> findAllCodesOfAType(PointsCategory c) {
		List<PointsInstance> result = new ArrayList<PointsInstance>();
		if(getCodes() != null){
			for (PointsInstance code : getCodes()) {
				if(code.getCategory().equals(c)){
					result.add(code);
				}
			}	
		}
		return result;
	}

	public void addComment(Comment c) {
		if(!getComments().contains(c)){
			getComments().add(c);
		}
		
	}

	public void addToAgenda(Session session) {
		if(!getSessions().contains(session)){
			getSessions().add(session);
		}
	}
	
	public PointsInstance getPuzzlePiece() {
		return puzzlePiece;
	}
	
	public boolean hasPuzzlePiece() {
		return getPuzzlePiece() != null;
	}

	public void setPuzzlePiece(PointsInstance puzzlePiece) {
		this.puzzlePiece = puzzlePiece;
	}

	public List<PointsInstance> getCodes() {
		return codes;
	}

	public Map<Integer, String> getViktorina() {
		return viktorina;
	}
	
	public boolean isSessionExpired(){
		return System.currentTimeMillis() - getSessionExpires() > 0;
	}

	public long getSessionExpires() {
		return sessionExpires;
	}

	public void setSessionExpires(long sessionExpires) {
		this.sessionExpires = sessionExpires;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSecretAccess() {
		return secretAccess;
	}

	public void setSecretAccess(String secretAccess) {
		this.secretAccess = secretAccess;
	}

	public int getPoints() {
		List<PointsInstance> codes2 = getCodes();
		int points = 0;
		for (PointsInstance pointsInstance : codes2) {
			points+=pointsInstance.getCategory().getPoints();
		}
		return points;
	}

}
