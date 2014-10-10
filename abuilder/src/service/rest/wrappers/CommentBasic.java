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

	public CommentBasic(Comment c) {
		id = c.getId();
		name = c.getCowner().getName();
//		name = "noname";
		text = c.getText();
		likes = c.getLikes();
		likedBy = fetchPeople(c);
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
