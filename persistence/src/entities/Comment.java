package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQueries({
	@NamedQuery(name = "allComments", query = "SELECT e FROM Comment e WHERE e.event.id = :id"),
	@NamedQuery(name = "allCommentsRaw", query = "SELECT e FROM Comment e"),
	@NamedQuery(name = "allCommentsByUser", query = "SELECT e FROM Comment e WHERE e.cowner.id = :id"),
	@NamedQuery(name = "commentById", query = "SELECT e FROM Comment e WHERE e.id = :id")})
@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String text;
	
	private int inReplyTo;
	
	@OneToOne
	private Event event;
	
	private LoggedUser cowner;
	
	private int likes = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(int inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	
	public LoggedUser getCowner() {
		return cowner;
	}

	public void setCowner(LoggedUser cowner) {
		this.cowner = cowner;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getLikes() {
		return likes;
	}
	
	public void incLikes(){
		likes +=1;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Comment){
			Comment c = (Comment) obj;
			return (c.getCowner().getId() == this.cowner.getId()) && 
					(c.getText().equals(this.getText())) &&
					(c.getEvent().getId() == this.getEvent().getId());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Text: "+ text + " likes: "+likes;
	}
}
