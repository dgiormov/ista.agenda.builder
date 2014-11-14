package persistency.entities.feedback;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Answer
 *
 */
@Entity

public class Answer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;
	
	private Question question;
	
	private long currentUser;
	
	private int group;
	private String groupLink;
	
	private int rating;
	
	private String freeText;
   
}
