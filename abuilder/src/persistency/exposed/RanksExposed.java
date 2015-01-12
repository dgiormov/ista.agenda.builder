package persistency.exposed;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import persistency.entities.gamification.Rank;

public class RanksExposed extends AbstractExposed<Rank> {


	public Rank findRankById(int id) {
		Query namedQuery = entityManager.createNamedQuery("getRankById");
		namedQuery.setParameter("id", id);
		Rank result = null;
		try {
			result = (Rank) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	
	public List<Rank> allRanks(){
		Query namedQuery = entityManager.createNamedQuery("allRanks");
		List<Rank> result = null;
		try {
			result = (List<Rank>) namedQuery.getResultList();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
}
