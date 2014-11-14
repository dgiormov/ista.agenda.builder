package persistency.exposed.json;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Index;

import persistency.entities.LoggedUser;
import persistency.entities.gamification.PointsInstance;

public class PointsInstanceJson implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getCompositeCodeId() {
		return compositeCodeId;
	}

	public String getEnteredById() {
		return enteredById;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public String getCategorySID() {
		return categorySID;
	}

	private String code;
	
	private String description;

	private String compositeCodeId;
	
	private String enteredById;
	
	private boolean isUsed;
	
	private String categorySID;
	
	public PointsInstanceJson(PointsInstance pi) {
		id = pi.getId();
		code = pi.getCode();
		description = pi.getDescription();
		compositeCodeId = pi.getCompositeCodeId();
		enteredById = pi.getEnteredBy() != null ? pi.getEnteredBy().getId() +"" : null;
		isUsed = pi.isUsed();
		categorySID = pi.getCategory().getShortid();
	}
	
}
