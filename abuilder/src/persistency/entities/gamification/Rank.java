package persistency.entities.gamification;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: Rank
 *
 */
@Entity
public class Rank implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	private int rankPos;
	private String rankName;
	
	private List<PointsCategory> rankRequirements;
	
	
	public String getRankName() {
		return rankName;
	}
	public void setRankName(String rankName) {
		this.rankName = rankName;
	}
	public int getRankPos() {
		return rankPos;
	}
	public void setRankPos(int rankPos) {
		this.rankPos = rankPos;
	}
	public List<PointsCategory> getRankRequirements() {
		return rankRequirements;
	}
	public void setRankRequirements(List<PointsCategory> rankRequirements) {
		this.rankRequirements = rankRequirements;
	}
	
	@Override
	public String toString() {
		return rankPos + " - " + rankName;
	}
}
