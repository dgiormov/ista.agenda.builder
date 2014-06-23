package game;

import java.util.Map;
import java.util.Set;

import entities.LoggedUser;

public class Player implements Comparable<Player> {

	private long id;
	private String name;
	private long points = 0;
	private int position = 0;
	
	public Player(LoggedUser p) {
		id = p.getId()+42;
		name = p.getName();
		Map<Integer, Float> map = p.getPoints();
		Set<Integer> keySet = map.keySet();
		for (Integer integer : keySet) {
			points += map.get(integer);
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
