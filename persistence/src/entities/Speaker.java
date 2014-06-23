package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Entity implementation class for Entity: LoggedUser
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "allUsersSQL", query = "SELECT e FROM Speaker e"),
		@NamedQuery(name = "getUserByMail", query = "SELECT e FROM Speaker e WHERE e.email = :email"),
		@NamedQuery(name = "getUserById", query = "SELECT e FROM Speaker e WHERE e.id = :id") })
public class Speaker implements Serializable {

	@Id
	private String id;
	private String name;
	private String email;
	private String twitterAccount;
	private String bio;
	private String company;
	private String position;
	private long clickedUpon = 0;

	private static final long serialVersionUID = 1L;

	public Speaker() {
		super();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String firstname) {
		this.name = firstname;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTwitterAccount() {
		return twitterAccount;
	}

	public void setTwitterAccount(String twitterAccount) {
		this.twitterAccount = twitterAccount;
	}

	public long getClickedUpon() {
		return clickedUpon;
	}

	public void setClickedUpon(long clickedUpon) {
		this.clickedUpon = clickedUpon;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}
