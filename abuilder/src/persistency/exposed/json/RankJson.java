package persistency.exposed.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.Rank;
import persistency.exposed.PointsCategoryExposed;

public class RankJson implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private int rankPos;
	private String rankName;
	
	private List<String> rankRequirements;
	
	
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
	public List<String> getRankRequirements() {
		return rankRequirements;
	}
	public void setRankRequirements(List<String> rankRequirements) {
		this.rankRequirements = rankRequirements;
	}
	
	public Rank toEntity(){
		Rank r = new Rank();
		r.setRankName(rankName);
		r.setRankPos(rankPos);
		PointsCategoryExposed pce = new PointsCategoryExposed();
		List<PointsCategory> reqs = new ArrayList<PointsCategory>();
		if(rankRequirements != null){
			for (String string : rankRequirements) {
				PointsCategory findCategoryByShortName = pce.findCategoryByShortName(string);
				if (findCategoryByShortName == null){
					throw new IllegalArgumentException("No such category: "+string);
				}
					
				reqs.add(findCategoryByShortName);
			}
		}
		r.setRankRequirements(reqs);
		return r;
	}
}
