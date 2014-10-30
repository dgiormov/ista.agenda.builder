package persistency.entities;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Index;

/**
 * Entity implementation class for Entity: Code
 *
 */
@Entity
public class PointsInstance implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Index(unique=true)
	private String code;

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
		return getCategory().getName() + (getCategory().getPoints());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(PointsInstance.class.isInstance(obj)){
			return ((PointsInstance) obj).getCode().equals(getCode());
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
}
