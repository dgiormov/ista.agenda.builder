package persistency.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: PointsCategory
 *
 */
@Entity
public class PointsCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1173079643293327695L;

	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	private String description;
	
	private int maxInstacesPerPerson;
	
	private int codeLength = 4;
	
	private boolean selfGeneratingInstances;
	
	private int maxNumberOfInstances = -1;
	
	private boolean areCompositeCodes;
	
	private List<String> compositeCodePossitions;
	
	private int points;
	
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
}
