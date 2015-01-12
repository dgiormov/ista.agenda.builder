package gamification;

import java.util.List;

import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsCategory;

public class Player implements Comparable<Player> {

	private long id;
	private String name;
	private long points = 0;
	private String rank = "";
	private int rankInt = 0;
	private int position = 0;
	
	public Player(LoggedUser p) {
		id = p.getId()+42;
		name = p.getName();
		points = p.getPoints();
		setRank(computeRank(p));
	}

	private String computeRank(LoggedUser p) {
		List<PointsCategory> uniqueCategories = p.getUniqueCategories();
		
		int rankRaw1 = 0;
		int rankRaw2 = 0;
		int rankRaw3 = 0;
		for(PointsCategory cat : uniqueCategories) {
			int rank = cat.getRank();
			switch (rank) {
				case 1:
					rankRaw1 += 1;
					break;
				case 2:
					rankRaw2 += 1;
					break;
				case 3:
					rankRaw3 += 1;
					break;
			}
		}
		rankInt = 0;
		if(rankRaw1 >= 3 && rankRaw2 >=3 && rankRaw3 >=3){
			rankInt = 3;
		} else if(rankRaw1 >= 3 && rankRaw2 >=3){
			rankInt = 2;
		} else if(rankRaw1 >= 3){
			rankInt = 1;
		}
		return "rank"+rankInt;
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

	public String getRank() {
		return rank;
	}

	private void setRank(String rank) {
		this.rank = rank;
	}

	public int getRankInt() {
		return rankInt;
	}
}
