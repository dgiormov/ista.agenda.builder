package persistency.entities.admin;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: RequestPower
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "requestById", query = "SELECT e FROM RequestPower e WHERE e.id = :id"),
	@NamedQuery(name = "getAll", query = "SELECT e FROM RequestPower e")})
public class RequestPower implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
   
}
