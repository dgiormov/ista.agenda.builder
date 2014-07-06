package entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Points
 *
 */
@Entity
public class Points implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	private int id;

	@OneToOne
	private PointsType type;
	
	private LoggedUser owner;


	public PointsType getType() {
		return type;
	}


	public void setType(PointsType type) {
		this.type = type;
	}


	public LoggedUser getOwner() {
		return owner;
	}


	public void setOwner(LoggedUser owner) {
		this.owner = owner;
	}
}
