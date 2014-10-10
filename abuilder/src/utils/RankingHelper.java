package utils;

import java.util.List;

import gamification.Player;

public class RankingHelper {

	private Player me;
	
	public RankingHelper(Player me, List<Player> ranking) {
		super();
		this.me = me;
		this.ranking = ranking;
	}

	private List<Player> ranking;
	
}
