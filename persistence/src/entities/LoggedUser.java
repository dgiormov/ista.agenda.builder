package entities;

import exposed.CodesExposed;
import game.Player;
import game.Score;
import game.ScoreCategories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.Index;

import util.PointsHelper;
import util.Status;
import util.Status.STATE;

/**
 * Entity implementation class for Entity: Person
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getPersonById", query = "SELECT e FROM LoggedUser e WHERE e.id = :id"),
		@NamedQuery(name = "getPersonByEmail", query = "SELECT e FROM LoggedUser e WHERE e.email = :email"),
		@NamedQuery(name = "getPersonByUserName", query = "SELECT e FROM LoggedUser e WHERE e.userName = :userName"),
		@NamedQuery(name = "getAllPersons", query = "SELECT e FROM LoggedUser e"),
		@NamedQuery(name = "getPersonByTwiiterId", query = "SELECT e FROM LoggedUser e WHERE e.tAccess = :tAccess AND e.tSAccess = :tSAccess") })
public class LoggedUser implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;

	@Index
	private String email;

	@Index
	private String tAccess;
	@Index
	private String tSAccess;
	
	private String platform;
	
	/**
	 * ten attempts for cheating, after that ban...
	 */
	private int strikes = 10;

	@OneToMany
	private List<Session> sessions = new ArrayList<Session>();
	
	@OneToMany(mappedBy="owner")
	private List<Points> points = new ArrayList<Points>();
	
	@OneToOne(mappedBy="enteredBy")
	private Code puzzlePiece;
	
	@OneToMany(mappedBy="enteredBy")
	private List<Code> codes = new ArrayList<Code>();
	
	@OneToMany
	private List<Comment> likedComments = new ArrayList<Comment>();
	
	@OneToMany(mappedBy="cowner")
	private List<Comment> comments = new ArrayList<Comment>();
	
	@ElementCollection
	private Map<Integer, Integer> eventRatings = new HashMap<Integer, Integer>();
	
	@ElementCollection
	private Map<Integer, Integer> speakerRatings = new HashMap<Integer, Integer>();
	
	@ElementCollection
	private Map<Integer, String> viktorina = new HashMap<Integer, String>();
	
	@ElementCollection
	private Map<Integer, Long> bookedWhen = new HashMap<Integer, Long>();
	
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

	public String gettAccess() {
		return tAccess;
	}

	public void settAccess(String tAccess) {
		this.tAccess = tAccess;
	}

	public String gettSAccess() {
		return tSAccess;
	}

	public void settSAccess(String tSAccess) {
		this.tSAccess = tSAccess;
	}

	public Map<Integer, Integer> getEventRatings() {
		return eventRatings;
	}

	public Map<Integer, Long> getBookedWhen() {
		return bookedWhen;
	}

	public void setBookedWhen(Map<Integer, Long> bookedWhen) {
		this.bookedWhen = bookedWhen;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public boolean isActive() {
		return getEventRatings() != null && getEventRatings().size() > 0
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
	
	public Status addCode(Code code){
		if(code.isUsed()){
			return new Status(STATE.ERROR, "This code has been used.");
		}
		if(code.getEnteredBy() != null && !this.getUserName().equals(code.getEnteredBy().getUserName())) {
			return new Status(STATE.ERROR, "This code has been used by "+code.getEnteredBy().getName());
		}
		int maxCodesOfThisType = code.getMaxCodesOfThisType();
		List<Code> allCodesOfAType = findAllCodesOfAType(code.getGid());
		if(code.getType() != Code.PUZZLE_CODE && allCodesOfAType.size() >= maxCodesOfThisType) {
			return new Status(STATE.ERROR, "Sorry the rules do not allow you to enter more codes of this type ("+code.getName()+").");
		} else if(code.getType() == Code.PUZZLE_CODE && allCodesOfAType.size() > maxCodesOfThisType) {
			return new Status(STATE.ERROR, "Sorry the rules do not allow you to enter more codes of this type ("+code.getName()+").");
		}
		CodesExposed ce = new CodesExposed();
		code.setUsed(true);
		code.setEnteredBy(this);
		ce.updateEntity(code);
		if(!getCodes().contains(code)){
			getCodes().add(code);	
		}
		//FIXME points
//		addPoints(code.getGid(), code.getPoints());
		return new Status(STATE.OK, code.getPoints()); 
	}

	private List<Code> findAllCodesOfAType(int gid) {
		List<Code> result = new ArrayList<Code>();
		if(getCodes() != null){
			for (Code code : getCodes()) {
				if(code.getGid() == gid){
					result.add(code);
				}
			}	
		}
		return result;
	}

	public List<Points> getPoints() {
		return points;
	}

	public void addComment(Comment c) {
		if(!getComments().contains(c)){
			getComments().add(c);
		}
		
	}

	public void addToAgenda(Session event) {
		if(!getSessions().contains(event)){
			getSessions().add(event);
			if(getSessions().size() > 4){
				//FIXME add points
//				addPoints(ScoreCategories.PROGRAM_PREPARED, Score.AGENDA);	
			}
		}
	}
	
	public Map<String, List<PointsHelper>> getPlayerStats(){
		List<PointsHelper> genericData = new ArrayList<PointsHelper>();
		List<PointsHelper> codesData = new ArrayList<>();
		Map<String, List<PointsHelper>> map = new HashMap<String, List<PointsHelper>>();
		genericData.add(new PointsHelper("Logging in", Score.LOGIN+""));
		if(getSessions().size() > 4 ){
			genericData.add(new PointsHelper("Agenda prepared", Score.AGENDA+""));
		}
		if(viktorina.size()>0){
			genericData.add(new PointsHelper("Secret words", (Score.SESSION_SPECIAL*viktorina.size())+""));
		}
		
		int likes = 0;
		int bonuses = 0;
		for (Comment comment : comments) {
			likes+=comment.getLikes();
			if(comment.getLikes() > 3){
				bonuses++;
			}
		}
		genericData.add(new PointsHelper("My comment liked", likes +"x"+Score.LIKED));
		genericData.add(new PointsHelper("4 likes of my comment", bonuses  +"x"+Score.LIKED5));
		map.put("generic", genericData);
		for (Code code : getCodes()) {
			if(code.getType() == Code.PUZZLE_CODE){
				if(code.isUsed()){
					codesData.add(new PointsHelper(code.getName(), code.getPoints()+""));
				}
			} else {
				codesData.add(new PointsHelper(code.getName(), code.getPoints()+""));
			}
		}
		
		map.put("codes", codesData);
		return map;
	}

	public Code getPuzzlePiece() {
		return puzzlePiece;
	}
	
	public boolean hasPuzzlePiece() {
		return getPuzzlePiece() != null;
	}

	public void setPuzzlePiece(Code puzzlePiece) {
		this.puzzlePiece = puzzlePiece;
	}

	public List<Code> getCodes() {
		return codes;
	}

	public Map<Integer, String> getViktorina() {
		return viktorina;
	}

}
