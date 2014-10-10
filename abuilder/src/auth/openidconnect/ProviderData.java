package auth.openidconnect;

public class ProviderData {

	private final String authzEndpoint;

	private final String tokenEndpoint;
	
	private final String localClientId;
	private final String localClientSecret;
	
	private final String devClientId;
	private final String devClientSecret;
	
	private final String productiveClientId;
	private final String productiveClientSecret;
	
	private final String userInfoEndpoint;

	private final String scope;

	private final String userEmailEndpoint;
	
	public ProviderData(String authzEndpoint, String tokenEndpoint,
			String localClientId, String localClientSecret, String devClientId,
			String devClientSecret, String productiveClientId,
			String productiveClientSecret, String userInfoEndpoint, String userEmailEndpoint, String scope) {
		super();
		this.authzEndpoint = authzEndpoint;
		this.tokenEndpoint = tokenEndpoint;
		this.localClientId = localClientId;
		this.localClientSecret = localClientSecret;
		this.devClientId = devClientId;
		this.devClientSecret = devClientSecret;
		this.productiveClientId = productiveClientId;
		this.productiveClientSecret = productiveClientSecret;
		this.userInfoEndpoint = userInfoEndpoint;
		this.userEmailEndpoint = userEmailEndpoint;
		this.scope = scope;
	}

	public String getAuthzEndpoint() {
		return authzEndpoint;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public String getLocalClientId() {
		return localClientId;
	}

	public String getLocalClientSecret() {
		return localClientSecret;
	}

	public String getDevClientId() {
		return devClientId;
	}

	public String getDevClientSecret() {
		return devClientSecret;
	}

	public String getProductiveClientId() {
		return productiveClientId;
	}

	public String getProductiveClientSecret() {
		return productiveClientSecret;
	}

	public String getUserInfoEndpoint() {
		return userInfoEndpoint;
	}

	public String getScope() {
		return scope;
	}

	public String getUserEmailEndpoint() {
		return userEmailEndpoint;
	}
}
