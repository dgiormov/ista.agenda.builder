package auth.twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterUtil {
	
	private static Map<String, Long> termLastCalled = new HashMap<String, Long>();
	
	private static Map<String, Long> photosLastCalled = new HashMap<String, Long>();
	private static Map<String, List<String>> photoCache = new HashMap<String, List<String>>();

	public static synchronized List<String> searchTwitterForImages(Twitter twitter,
			String queryString) throws TwitterException {
		Long timeUpdated = photosLastCalled.get(queryString);
		if(timeUpdated != null){
			if((System.currentTimeMillis() -timeUpdated) < (120 * 1000)){
				List<String> lastResult = photoCache.get(queryString);
				return lastResult == null? new ArrayList<String>(): lastResult;
			} 
		}
		Query query = new Query(queryString);
		QueryResult result;
		List<String> resultArr = new ArrayList<String>();
		result = twitter.search(query);
		List<Status> tweets = result.getTweets();
		for (Status status : tweets) {
			MediaEntity[] mediaEntities = status.getMediaEntities();
			if (mediaEntities != null && mediaEntities.length > 0) {
				for (MediaEntity mediaEntity : mediaEntities) {
					resultArr.add(mediaEntity.getMediaURL());
				}
			}
		}
		photoCache.put(queryString, resultArr);
		photosLastCalled.put(queryString, System.currentTimeMillis());
		return resultArr;
	}

	public static boolean isTwiterActive(Twitter twitter) {
		boolean isTwitterActive = false;
		try {
			if (twitter != null && twitter.getOAuthAccessToken() != null) {
				twitter.verifyCredentials();
				isTwitterActive = true;
			}
		} catch (TwitterException e) {
			isTwitterActive = false;
		}
		return isTwitterActive;
	}
}
