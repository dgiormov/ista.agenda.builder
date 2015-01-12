package persistency.entities.admin;

import java.io.Serializable;

import javax.persistence.*;

import persistency.entities.LoggedUser;

/**
 * Entity implementation class for Entity: PowerUser
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "isPowerUser", query = "SELECT e FROM PowerUser e WHERE e.id = :id")
})
public class PowerUser implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	private long id; 
	
}
