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

import javax.persistence.CascadeType;
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

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.jpa.config.Cascade;

import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.exposed.PointsCategoryExposed;
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
	@GeneratedValue
	private long id;
	
	private String name;

//	@Index(unique=true)
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
	private List<PointsInstance> pointsIntances = new ArrayList<PointsInstance>();
	
	@ManyToMany(mappedBy="likedBy")
	private List<Comment> likedComments = new ArrayList<Comment>();
	
	@OneToMany(mappedBy="cowner", cascade=CascadeType.ALL)
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
	
	public Status addCode(PointsInstance code){
		if(!getPointsInstances().contains(code)){
			getPointsInstances().add(code);	
		}
		return new Status(STATE.OK, code.getCategory().getPoints()); 
	}
	
	private List<PointsInstance> findAllPointsOfAType(PointsCategory c) {
		List<PointsInstance> result = new ArrayList<PointsInstance>();
		if(getPointsInstances() != null){
			for (PointsInstance code : getPointsInstances()) {
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

	public List<PointsInstance> getPointsInstances() {
		return pointsIntances;
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
		List<PointsInstance> codes2 = getPointsInstances();
		int points = 0;
		for (PointsInstance pointsInstance : codes2) {
			points+=pointsInstance.getCategory().getPoints();
		}
		return points;
	}

	public List<PointsInstance> getPointsInstancesOfCategory(
			PointsCategory pointsCategory) {
		List<PointsInstance> result = new ArrayList<PointsInstance>();
		List<PointsInstance> instances = getPointsInstances();
		if(instances != null){
			for (PointsInstance pointsInstance : instances) {
				if(pointsInstance.getCategory() != null && pointsInstance.getCategory().getId() == pointsCategory.getId()){
					result.add(pointsInstance);
				}
			}
		}
		return result;
	}

	public void updateRatingPoints(Map<Integer, Integer> ratings, Session session, boolean add) {
		String shortCode = "ratesession";
		String who = "Rated Session: ";
		if(ratings == getSpeakerRatings()){
			shortCode = "ratespeaker";
			who = "Rated Speaker(s): ";
		}
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory category = pce.findCategoryByShortName(shortCode);
		List<PointsInstance> allPointsOfAType = findAllPointsOfAType(category);
		PointsInstance p = null;
		String pointsDescription = who+session.getName();
		for (PointsInstance pointsInstance : allPointsOfAType) {
			if(pointsInstance.getDescription().equals(pointsDescription)){
				p = pointsInstance;
				break;
			}
		}
		
		if(add){
			if(p == null){
				PointsInstance instance = category.createPointsInstance(this, pointsDescription);
				getPointsInstances().add(instance);
			}
		} else {
			if(p!=null){
				PointsExposed pe = new PointsExposed();
				pe.deleteEntity(p);
				getPointsInstances().remove(p);
			}
		}
	}
	
	public void updateLikesPoints(Map<Integer, Integer> ratings, Session session, boolean add) {
		String shortCode = "ratesession";
		String who = "Rated Session: ";
		if(ratings == getSpeakerRatings()){
			shortCode = "ratespeaker";
			who = "Rated Speaker(s): ";
		}
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory category = pce.findCategoryByShortName(shortCode);
		List<PointsInstance> allPointsOfAType = findAllPointsOfAType(category);
		PointsInstance p = null;
		String pointsDescription = who+session.getName();
		for (PointsInstance pointsInstance : allPointsOfAType) {
			if(pointsInstance.getDescription().equals(pointsDescription)){
				p = pointsInstance;
				break;
			}
		}
		
		if(add){
			if(p == null){
				PointsInstance instance = category.createPointsInstance(this, pointsDescription);
				getPointsInstances().add(instance);
			}
		} else {
			if(p!=null){
				PointsExposed pe = new PointsExposed();
				pe.deleteEntity(p);
				getPointsInstances().remove(p);
			}
		}
	}
	
	public void updateLiked(Comment comment){
		String key = "liked";
		String commentId = comment.getId()+"";
		String who = key+" on comment: ";
		String pointsDescription = who+commentId;
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory category = pce.findCategoryByShortName(key);

		List<PointsInstance> allPointsWithDescription = findAllPointsWithDescription(category, pointsDescription);
		for(int i=allPointsWithDescription.size(); i<comment.getLikes(); i++){
			PointsInstance instance = category.createPointsInstance(this, pointsDescription);
			getPointsInstances().add(instance);
		}
	}
	
	public void updateLikes(Comment comment){
		String key = "like";
		String commentId = comment.getId()+"";
		String who = key+" on comment: ";
		String pointsDescription = who+commentId;
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory category = pce.findCategoryByShortName(key);
		List<PointsInstance> findAllPointsOfAType = findAllPointsOfAType(category);
		for (PointsInstance pointsInstance : findAllPointsOfAType) {
			if(pointsInstance.getDescription() == null || pointsInstance.getDescription().length() == 0){
				getPointsInstances().remove(pointsInstance);
				category.getInstancesOfThisType().remove(pointsInstance);
			}
		}
		pce.updateEntity(category);
		List<PointsInstance> allPointsWithDescription = findAllPointsWithDescription(category, pointsDescription);
		if(allPointsWithDescription == null || allPointsWithDescription.size() == 0){
			PointsInstance instance = category.createPointsInstance(this, pointsDescription);
			getPointsInstances().add(instance);
		}
	}
	
	public List<PointsInstance> findAllPointsWithDescription(PointsCategory pointsCategory, String pointsDescription){
		List<PointsInstance> allPointsOfAType = findAllPointsOfAType(pointsCategory);
		PointsInstance p = null;
		List<PointsInstance> result = new ArrayList<PointsInstance>();
		for (PointsInstance pointsInstance : allPointsOfAType) {
			if(pointsInstance.getDescription()!= null && pointsDescription!= null && pointsInstance.getDescription().equals(pointsDescription)){
				result.add(pointsInstance);
			}
		}
		return result;
	}
	
	public void updateLiked5Points(Comment comment) {
		String key = "liked5";
		String commentId = comment.getId()+"";
		String who = key+" on comment: ";
		String pointsDescription = who+commentId;
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory category = pce.findCategoryByShortName(key);

		List<PointsInstance> allPointsWithDescription = findAllPointsWithDescription(category, pointsDescription);
		if(allPointsWithDescription.size() == 0){
			PointsInstance instance = category.createPointsInstance(this, pointsDescription);
			getPointsInstances().add(instance);
		} else if(allPointsWithDescription.size()>1){
			PointsExposed pe = new PointsExposed();
			for(int i=1; i<allPointsWithDescription.size(); i++){
				pe.deleteEntity(allPointsWithDescription.get(i));
			}
		}
	}
	
	public List<PointsCategory> getUniqueCategories() {
		List<PointsCategory> uniqueCategories = new ArrayList<PointsCategory>();
		for (PointsInstance pointsInstance : getPointsInstances()) {
			PointsCategory category = pointsInstance.getCategory();
			if(!uniqueCategories.contains(category)){
				uniqueCategories.add(category);
			}
		}
		return uniqueCategories;
	}
}
