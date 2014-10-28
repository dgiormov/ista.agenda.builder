package service.rest.wrappers;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import persistency.entities.Comment;
import persistency.entities.LoggedUser;
import persistency.exposed.LoggedUserExposed;

@XmlRootElement
public class CommentBasic {

	private int id;
	private String name;
	private String text;
	private int likes;
	private String likedBy;
	private boolean likedByMe;

	public CommentBasic(Comment c, boolean likedByMe) {
		id = c.getId();
		name = c.getCowner().getName();
		text = c.getText();
//		likedBy = fetchPeople(c);
		likes = c.getLikes();
		this. likedByMe = likedByMe;  
	}

	private String fetchPeople(Comment c) {
		LoggedUserExposed pe = new LoggedUserExposed();
		List<LoggedUser> allPersons = pe.getAllPersons();
		String result = "";
		for (LoggedUser person : allPersons) {
			if(person.getLikedComments().contains(c)){
				result+=person.getName()+", ";
			}
		}
		if(result.length() > 2){
			result = result.substring(0, result.lastIndexOf(", "));
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "User:" + name +"\n\rComment text: "+ text + " Liked by: "+likedBy;
	}
}
