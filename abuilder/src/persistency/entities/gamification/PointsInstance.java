package persistency.entities.gamification;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Index;

import persistency.entities.LoggedUser;

/**
 * Entity implementation class for Entity: Code
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "allCodes", query = "SELECT e FROM PointsInstance e"),
	@NamedQuery(name = "getCode", query = "SELECT e FROM PointsInstance e WHERE e.code = :code"),
	@NamedQuery(name = "getCodeById", query = "SELECT e FROM PointsInstance e WHERE e.id = :id")
	
})
public class PointsInstance implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Index
	private String code;
	
	private String description;

	private String compositeCodeId;
	
	private LoggedUser enteredBy;
	
	private boolean isUsed;
	
	private PointsCategory category;
	
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LoggedUser getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(LoggedUser enteredBy) {
		this.enteredBy = enteredBy;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	@Override
	public String toString() {
		return getCategory().getName() +" ("+ getCategory().getPoints()+") "+ code;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(PointsInstance.class.isInstance(obj)){
			if(getCode() != null){
				return getCode().equals(((PointsInstance) obj).getCode());
			}
			if(getId() == ((PointsInstance) obj).getId()){
				return true;
			}
		}
		return super.equals(obj);
	}

	public String getCompositeCodeId() {
		return compositeCodeId;
	}

	public void setCompositeCodeId(String compositeCodeId) {
		this.compositeCodeId = compositeCodeId;
	}

	public PointsCategory getCategory() {
		return category;
	}

	public void setCategory(PointsCategory category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
