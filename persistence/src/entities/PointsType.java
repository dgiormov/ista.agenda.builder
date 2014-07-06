package entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: PointsType
 *
 */
@Entity

public class PointsType implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	private int id;
	
	private String name;
	
	private int maxPoints;
	
	private float points;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}
}
