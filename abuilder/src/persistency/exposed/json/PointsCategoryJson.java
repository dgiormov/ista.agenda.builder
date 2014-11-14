package persistency.exposed.json;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsCategory;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.PointsExposed;

public class PointsCategoryJson implements Serializable {

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
	private int instancesOfThisType;
	
	public PointsCategoryJson(PointsCategory original) {
		id = original.getId();
		shortid = original.getShortid();
		name = original.getName();
		description = original.getDescription();
		reusable = original.isReusable();
		maxInstacesPerPerson = original.getMaxInstacesPerPerson();
		codeLength = original.getCodeLength();
		selfGeneratingInstances = original.isSelfGeneratingInstances();
		maxNumberOfInstances = original.getMaxNumberOfInstances();
		areCompositeCodes = original.areCompositeCodes();
		compositeCodePossitions = original.getCompositeCodePossitions();
		requiresPlayerLevel = original.getRequiresPlayerLevel();
		points = original.getPoints();
		playerPositionAbove = original.getPlayerPositionAbove();
		rank = original.getRank();
		instancesOfThisType = original.getInstancesOfThisType() == null ? 0 : original.getInstancesOfThisType().size();
	}

}
