package utils;


public class Status {

	public STATE severity;
	public float points = 0;
	private boolean changingRank = false;

	public Status(STATE s, float points) {
		this.severity = s;
		this.points = points;
	}
	
	public Status(STATE s, String message) {
		this.severity = s;
		this.message = message;
	}

	public boolean isChangingRank() {
		return changingRank;
	}

	public void setChangingRank(boolean changesRank) {
		this.changingRank = changesRank;
	}

	public enum STATE {OK, ERROR}
	
	public String message;
	
	
}
