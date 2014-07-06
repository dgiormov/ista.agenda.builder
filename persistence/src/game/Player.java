package game;

import java.util.List;
import java.util.Map;
import java.util.Set;

import entities.LoggedUser;
import entities.Points;

public class Player implements Comparable<Player> {

	private long id;
	private String name;
	private long points = 0;
	private int position = 0;
	
	public Player(LoggedUser p) {
		id = p.getId()+42;
		name = p.getName();
		List<Points> allpoints = p.getPoints();
		for (Points point : allpoints) {
			points += point.getType().getPoints();
		}
			
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
}
