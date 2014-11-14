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
		if(user != null) {
			if(actionName != null){
				PointsCategoryExposed pce = new PointsCategoryExposed();
				PointsCategory pointsCategory = pce.findCategoryByShortName(actionName);
				//			Player oldPlayer = user.getPlayer();
				Status result = addPoints(pointsCategory, user, null);
				//			if(result.severity == STATE.OK){
				//				result = checkForChangingRank(result, user, oldPlayer);
				//			}
				return result;
			} else if(code != null){
				PointsExposed pe = new PointsExposed();
				PointsInstance pointsInstance = pe.getCode(code);
				PointsCategory pointsCategory = pointsInstance.getCategory();
				Status result = addPoints(pointsCategory, user, pointsInstance);
				return result;
			}
		}
		return null;
	}

//	private Status checkForChangingRank(Status result, LoggedUser user, Player oldPlayer) {
//		Player newPlayer = user.getPlayer();
//
//		result.setChangingRank(oldPlayer.getRank() != newPlayer.getRank());
//		return null;
//	}

	private Status addPoints(PointsCategory pointsCategory, LoggedUser user, PointsInstance code) {
		List<PointsInstance> pointsInstances = user.getPointsInstancesOfCategory(pointsCategory);
		Player playerBefore = user.getPlayer();
		if(pointsCategory.getMaxInstacesPerPerson() <0 || pointsCategory.getMaxInstacesPerPerson() > pointsInstances.size()){
			PointsInstance pi = null;
			if(pointsCategory.isSelfGeneratingInstances()){
				pi = pointsCategory.createPointsInstance(user);
			} else{
				PointsExposed pe = new PointsExposed();
				pi = code;
				if(pi == null){
					return new Status(STATE.ERROR, "Wrong code");
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
			Player playerAfter = user.getPlayer();
			if(playerBefore.getRank() < playerAfter.getRank()){
				execute("rank"+playerAfter.getRank(), user, null);
			}
			return new Status(Status.STATE.OK, pointsCategory.getPoints());
		}
		return new Status(STATE.ERROR, "No more codes of this type.");
	}

	private void persistData(LoggedUser user, PointsInstance pi) {
		user.addCode(pi);
		LoggedUserExposed lue = new LoggedUserExposed();
		lue.updateEntity(user);
	}
}
