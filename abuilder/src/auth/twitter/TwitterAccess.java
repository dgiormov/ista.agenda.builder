package auth.twitter;

import twitter4j.auth.AccessToken;

public class TwitterAccess {

	private static final AccessToken defaultAccessToken = new AccessToken(
			"1640251524-taSLNXTQxCvLehrxGTO7Ce7skoqn7ExSgQ71bdG",
			"FWUXSHsONrRkT35QC93DkkG69aR0LHyLS9ryIqRoY");
	private static final String defaultSearchTerm = "#ISTA2014";

	public static AccessToken getDefaultAccessToken() {
		return defaultAccessToken;
	}

	public static String getDefaultSearchTerm() {
		return defaultSearchTerm;
	}

}
