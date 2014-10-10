package persistency.entities;

import java.io.Serializable;

import javax.persistence.Column;
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
	private String twitter;
	@Column(length = 2000)
	private String bio;
	private String company;
	private String country;
	private String city;
	private String job_title;
	private long clickedUpon = 0;

	private static final long serialVersionUID = 1L;

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

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getJob_title() {
		return job_title;
	}

	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	
	@Override
	public String toString() {
		return "{name: "+name+ " company: "+company+" job title: "+job_title+"}";
	}
}
