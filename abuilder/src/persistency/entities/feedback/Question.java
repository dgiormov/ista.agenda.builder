package persistency.entities.feedback;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: QuestionInstance
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "getQuestions", query = "SELECT e FROM Question e"),
	@NamedQuery(name = "getQuestionById", query = "SELECT e FROM Question e WHERE e.id = :id")
})
public class Question implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	private int id;
	
	private String question;
	private String description;
	
	@OneToMany
	private List<Answer> answers;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
}
