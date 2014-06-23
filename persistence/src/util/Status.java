package util;


public class Status {

	public STATE severity;
	public float points = 0;

	public Status(STATE s, float points) {
		this.severity = s;
		this.points = points;
	}
	
	public Status(STATE s, String message) {
		this.severity = s;
		this.message = message;
	}

	public enum STATE {OK, ERROR}
	
	public String message;
	
	
}
