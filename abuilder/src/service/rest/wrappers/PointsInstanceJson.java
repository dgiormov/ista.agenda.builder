package service.rest.wrappers;

import persistency.entities.gamification.PointsInstance;

public class PointsInstanceJson {

	private String name;
	private String description;
	private int points;
	private String shortid;

	public PointsInstanceJson(PointsInstance pointsInstance) {
		name = pointsInstance.getCategory().getName();
		description = pointsInstance.getDescription();
		points = pointsInstance.getCategory().getPoints();
		shortid = pointsInstance.getCategory().getShortid();
	}

}
