package persistency.entities;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Index;

/**
 * Entity implementation class for Entity: Code
 *
 */
@Entity
@NamedQueries({
@NamedQuery(name = "allCodes", query = "SELECT e FROM Code e"),
@NamedQuery(name = "freeCodes", query = "SELECT e FROM Code e WHERE e.type = :type AND e.enteredBy IS NULL"),
@NamedQuery(name = "codesByType", query = "SELECT e FROM Code e WHERE e.type = :type AND e.enteredBy = :person"),
@NamedQuery(name = "getCode", query = "SELECT e FROM Code e WHERE e.code = :code"),
@NamedQuery(name = "getUsedCodes", query = "SELECT e FROM Code e WHERE e.enteredBy IS NOT NULL")
})
public class Code implements Serializable {
	
	public final static int PUZZLE_CODE = 1;
	public static final int EARLY_BIRD = 2;
	public static final int SESSION_QA = 3000;
	public static final int SESSION_SPECIAL = 4000;

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Index(unique=true)
	private String code;

	@Index
	private int type;
	
	@Index
	private int gid;
	
	private float points;
	
	private String puzzleCodeId;
	
	private LoggedUser enteredBy;
	
	private boolean isUsed;
	
	private String name;
	
	private int maxCodesOfThisType;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPuzzleCodeId() {
		return puzzleCodeId;
	}

	public void setPuzzleCodeId(String puzzleCodeId) {
		this.puzzleCodeId = puzzleCodeId;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}
	
	@Override
	public String toString() {
		return getName() + (points);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxCodesOfThisType() {
		return maxCodesOfThisType;
	}

	public void setMaxCodesOfThisType(int maxCodesOfThisType) {
		this.maxCodesOfThisType = maxCodesOfThisType;
	}
   
}
