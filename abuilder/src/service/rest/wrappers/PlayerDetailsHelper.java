package service.rest.wrappers;

import java.util.List;


public class PlayerDetailsHelper  {

	private String name;
	private List<UserPointsCategoryJson> pointsDetails;
	private int total;

	public PlayerDetailsHelper(String name, List<UserPointsCategoryJson> pointsDetails, int total) {
		this.name = name;
		this.pointsDetails = pointsDetails;
		this.total = total;
	}

}
