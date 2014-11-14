package gamification;

import java.util.List;

import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsInstance;

public class Player implements Comparable<Player> {

	private long id;
	private String name;
	private long points = 0;
	private int rank = 0;
	private int position = 0;
	
	public Player(LoggedUser p) {
		id = p.getId()+42;
		name = p.getName();
		points = p.getPoints();
		setRank(computeRank(p));
	}

	private int computeRank(LoggedUser p) {
		List<PointsInstance> pointsInstances = p.getPointsInstances();
		int rankRaw = 0;
		for (PointsInstance pointsInstance : pointsInstances) {
			int rank = pointsInstance.getCategory().getRank();
			switch (rank) {
				case 1:
					rankRaw += rank*100;
					break;
				case 2:
					rankRaw += rank*10;
					break;
				case 3:
					rankRaw += rank*1;
					break;
			}
		}
		if(rankRaw >= 333){
			return 3;
		} else if(rankRaw >=330){
			return 2;
		} else if(rankRaw >=300){
			return 1;
		}
		return 0;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	@Override
	public int compareTo(Player o) {
		if(this.getPoints() == o.getPoints()){
			return 0;	
		}
		return this.getPoints() > o.getPoints() ? -1 : 1;
	}
	
	public void setPosition(int position){
		this.position = position;
	}

	public long getId() {
		return id;
	}

	public int getRank() {
		return rank;
	}

	private void setRank(int rank) {
		this.rank = rank;
	}
}
