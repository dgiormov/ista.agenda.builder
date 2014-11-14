package persistency.entities.gamification;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import persistency.entities.LoggedUser;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.PointsExposed;

/**
 * Entity implementation class for Entity: PointsCategory
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "getCategoryByName", query = "SELECT e FROM PointsCategory e WHERE e.name = :name"), 
	@NamedQuery(name = "getCategoryByShortName", query = "SELECT e FROM PointsCategory e WHERE e.shortid = :sname"),
	@NamedQuery(name = "allCategories", query = "SELECT e FROM PointsCategory e")})
public class PointsCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1173079643293327695L;

	@Id
	@GeneratedValue
	private int id;

	private String shortid;
	private String name;
	private String description;
	
	private boolean reusable = false;
	
	private int maxInstacesPerPerson;
	
	private int codeLength = -1;
	
	private boolean selfGeneratingInstances;
	
	private int maxNumberOfInstances = -1;
	
	private boolean areCompositeCodes;
	
	private List<String> compositeCodePossitions;
	
	private int requiresPlayerLevel;
	
	private int points;
	
	private int playerPositionAbove = -1;
	
	private int rank = 0;
	
	private int nextCodeFactor = 1;
	
	@OneToMany
	private List<PointsInstance> instancesOfThisType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMaxInstacesPerPerson() {
		return maxInstacesPerPerson;
	}

	public void setMaxInstacesPerPerson(int maxInstacesPerPerson) {
		this.maxInstacesPerPerson = maxInstacesPerPerson;
	}

	public boolean isSelfGeneratingInstances() {
		return selfGeneratingInstances;
	}

	public void setSelfGeneratingInstances(boolean selfGeneratingInstances) {
		this.selfGeneratingInstances = selfGeneratingInstances;
	}

	public int getMaxNumberOfInstances() {
		return maxNumberOfInstances;
	}

	public void setMaxNumberOfInstances(int maxNumberOfInstances) {
		this.maxNumberOfInstances = maxNumberOfInstances;
	}

	public List<PointsInstance> getInstancesOfThisType() {
		return instancesOfThisType;
	}

	public void setInstancesOfThisType(List<PointsInstance> instancesOfThisType) {
		this.instancesOfThisType = instancesOfThisType;
	}

	public boolean areCompositeCodes() {
		return areCompositeCodes;
	}

	public void setAreCompositeCodes(boolean areCompositeCodes) {
		this.areCompositeCodes = areCompositeCodes;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public List<String> getCompositeCodePossitions() {
		return compositeCodePossitions;
	}

	public void setCompositeCodePossitions(List<String> compositeCodePossitions) {
		this.compositeCodePossitions = compositeCodePossitions;
	}

	public int getCodeLength() {
		return codeLength;
	}

	public void setCodeLength(int codeLength) {
		this.codeLength = codeLength;
	}
	
	public PointsInstance createPointsInstance(LoggedUser lu, String description){
		PointsInstance pi = new PointsInstance();
		pi.setCategory(this);
		pi.setEnteredBy(lu);
		pi.setUsed(true);
		pi.setDescription(description);
		(new PointsExposed()).createEntity(pi);
		PointsCategoryExposed pce = new PointsCategoryExposed();
		pce.updateEntity(this);
		return pi;
	}
	
	public PointsInstance createPointsInstance(LoggedUser lu){
		return createPointsInstance(lu, null);
	}

	public int getRequiresPlayerLevel() {
		return requiresPlayerLevel;
	}

	public void setRequiresPlayerLevel(int requiresPlayerLevel) {
		this.requiresPlayerLevel = requiresPlayerLevel;
	}

	public int getPlayerPositionAbove() {
		return playerPositionAbove;
	}

	public void setPlayerPositionAbove(int playerPositionAbove) {
		this.playerPositionAbove = playerPositionAbove;
	}

	public String getShortid() {
		return shortid;
	}

	public void setShortid(String shortid) {
		this.shortid = shortid;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isReusable() {
		return reusable;
	}

	public void setReusable(boolean reusable) {
		this.reusable = reusable;
	}
	
	public boolean isCodeCategory(){
		return codeLength > 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(PointsCategory.class.isInstance(obj)){
			return ((PointsCategory) obj).getId() == getId();
		}
		return super.equals(obj);
	}

	public int getNextCodeFactor() {
		return nextCodeFactor;
	}

	public void setNextCodeFactor(int nextCodeFactor) {
		this.nextCodeFactor = nextCodeFactor;
	}
}
