package entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "all", query = "SELECT e FROM EnabledFunctionality e WHERE e.id = 1")
public class EnabledFunctionality {

	@Id
	private int id = 1;
	private String userName;
	
	private boolean isEnabled;
	
	
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
