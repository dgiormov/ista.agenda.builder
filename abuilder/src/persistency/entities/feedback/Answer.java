package persistency.entities.feedback;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Answer
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "getAnswersByUserId", query = "SELECT e FROM Answer e WHERE e.currentUser = :userid"),
	@NamedQuery(name = "allAnswers", query = "SELECT e FROM Answer e")
})
public class Answer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;
	
	private Question question;
	
	private long currentUser;
	
	private int groupq = 0;
	private String groupLink;
	
	private int rating;
	
	@Column(length = 10000)
	private String freeText;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public long getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(long currentUser) {
		this.currentUser = currentUser;
	}

	public int getGroupq() {
		return groupq;
	}

	public void setGroupq(int groupq) {
		this.groupq = groupq;
	}

	public String getGroupLink() {
		return groupLink;
	}

	public void setGroupLink(String groupLink) {
		this.groupLink = groupLink;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}
   
}
