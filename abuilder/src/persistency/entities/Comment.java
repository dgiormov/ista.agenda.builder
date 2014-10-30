package persistency.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@NamedQueries({
	@NamedQuery(name = "allComments", query = "SELECT e FROM Comment e WHERE e.session.id = :id"),
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
	private Session session;
	
	private LoggedUser cowner;
	
	@ManyToMany
	private List<LoggedUser> likedBy;
	
	@Transient
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

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public int getLikes() {
		return getLikedBy()==null?0:getLikedBy().size();
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
					(c.getSession().getId() == this.getSession().getId());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Text: "+ text + " likes: "+likes;
	}

	public List<LoggedUser> getLikedBy() {
		return likedBy;
	}

	public void setLikedBy(List<LoggedUser> likedBy) {
		this.likedBy = likedBy;
	}
}
