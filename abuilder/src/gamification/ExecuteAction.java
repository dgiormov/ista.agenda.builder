package gamification;

import java.util.List;

import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.PointsExposed;
import utils.Status;
import utils.Status.STATE;

public class ExecuteAction {

	
	public static ExecuteAction getInstance(){
		return new ExecuteAction();
	}
	
	public Status execute(String actionName, LoggedUser user, String code){
		Status result = null;
		if(user != null) {
			if(actionName != null){
				result = addNonCodePoints(actionName, user);
			} else if(code != null){
				result = addCodePoints(user, code);
			}
			addRankPoints(user);
		}
		return result;
	}

	private Status addCodePoints(LoggedUser user, String code) {
		Status result;
		PointsExposed pe = new PointsExposed();
		PointsInstance pointsInstance = pe.getCode(code);
		if(pointsInstance == null){
			return new Status(STATE.ERROR, "Wrong code");
		}
		PointsCategory pointsCategory = pointsInstance.getCategory();
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory categoryByName = pce.findCategoryByShortName("charity");
		if(pointsCategory.getShortid().equals("tshirt")){
			if (user.getPointsInstancesOfCategory(categoryByName).size()<1){
				return new Status(STATE.ERROR, "You need to unlock the challenge before entering code from T-Shirt, go to charity booth and find the public code.");
			}
		}
		result = addPoints(pointsCategory, user, pointsInstance);
		return result;
	}

	private Status addNonCodePoints(String actionName, LoggedUser user) {
		Status result;
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory pointsCategory = pce.findCategoryByShortName(actionName);
		result = addPoints(pointsCategory, user, null);
		return result;
	}

	private Status addPoints(PointsCategory pointsCategory, LoggedUser user, PointsInstance code) {
		List<PointsInstance> pointsInstances = user.getPointsInstancesOfCategory(pointsCategory);
		if(pointsCategory.getMaxInstacesPerPerson() <0 || pointsCategory.getMaxInstacesPerPerson() > pointsInstances.size()){
			PointsInstance pi = null;
			if(pointsCategory.isSelfGeneratingInstances()){
				pi = pointsCategory.createPointsInstance(user);
			} else{
				PointsExposed pe = new PointsExposed();
				pi = code;
				if(pi == null){
					return new Status(STATE.ERROR, "Incorrect code");
				}
				if(pi.isUsed()){
					return new Status(STATE.ERROR, "This code has been used.");
				}
				if(pi.getEnteredBy() != null && !user.getName().equals(pi.getEnteredBy().getName())) {
					return new Status(STATE.ERROR, "This code has been used by "+pi.getEnteredBy().getName());
				}
				
				pi.setCategory(pointsCategory);
				pi.setEnteredBy(user);
				pi.setUsed(true);
				pe.updateEntity(pi);
			}
			persistData(user, pi);
			return new Status(Status.STATE.OK, pointsCategory.getPoints());
		}
		return new Status(STATE.ERROR, "You have reached the limit ("+pointsCategory.getMaxInstacesPerPerson()+") for this type of code - "+pointsCategory.getName());
	}

private void addRankPoints(LoggedUser user) {
	Player playerAfter = user.getPlayer();
	int rankInt = playerAfter.getRankInt();
	while(rankInt>0) {
		addNonCodePoints("rank"+rankInt, user);
		rankInt--;
	}
}

	private void persistData(LoggedUser user, PointsInstance pi) {
		user.addCode(pi);
		LoggedUserExposed lue = new LoggedUserExposed();
		lue.updateEntity(user);
	}
}
