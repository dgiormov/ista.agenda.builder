package service.rest.wrappers;

import persistency.entities.gamification.PointsCategory;

public class UserPointsCategoryJson implements Comparable<UserPointsCategoryJson> {

	private String name;
	private String description;
	private int points;
	private int factor=1;

	public UserPointsCategoryJson(PointsCategory pointsCategory, int factor) {
		name = pointsCategory.getName();
		description = pointsCategory.getDescription();
		setPoints(pointsCategory.getPoints());
		this.setFactor(factor); 
	}

	@Override
	public int compareTo(UserPointsCategoryJson o) {
		return (o.getPoints()*o.getFactor())-(getPoints()*getFactor());
	}

	public int getPoints() {
		return points;
	}

	private void setPoints(int points) {
		this.points = points;
	}

	private int getFactor() {
		return factor;
	}

	public void setFactor(int factor) {
		this.factor = factor;
	}
	
	

}
