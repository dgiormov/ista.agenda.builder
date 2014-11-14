package persistency.entities.feedback;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: QuestionInstance
 *
 */
@Entity
public class Question implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	private int id;
	
	private String question;
	private String description;
	
	@OneToMany
	private List<Answer> answers;
	
}
