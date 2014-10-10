package service.rest.wrappers;

public class OidClaimSetJsonObject {
	
	private String email;
	private String exp;

	private String sub;

	
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public String getSub() {
		return sub;
	}
	public String getEmail() {
		return email;
	}
}