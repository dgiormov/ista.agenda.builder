package persistency.exposed;

public class UserInfoJson {
	private String id;
	private String name;
	private String given_name;
	private String famili_name;
	private String picture;
	private String email;
	
	private String accessToken;
	private String secretAccessToken;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name != null ? name : given_name + " " + famili_name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGiven_name() {
		return given_name;
	}
	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}
	public String getFamili_name() {
		return famili_name;
	}
	public void setFamili_name(String famili_name) {
		this.famili_name = famili_name;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getSecretAccessToken() {
		return secretAccessToken;
	}
	public void setSecretAccessToken(String secretAccessToken) {
		this.secretAccessToken = secretAccessToken;
	}
}
