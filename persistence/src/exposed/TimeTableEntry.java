package exposed;

import java.util.ArrayList;
import java.util.List;

import rest.wrappers.EventBasic;
import entities.Event;

public class TimeTableEntry implements Comparable<TimeTableEntry> {
	private int startTime;
	private String timeRange;
	private List<EventBasic> events = new ArrayList<EventBasic>();

	public TimeTableEntry(int startTime0) {
		startTime = startTime0;
	}

	@Override
	public int compareTo(TimeTableEntry arg0) {
		if (this.getStartTime() > arg0.getStartTime()) {
			return 1;
		} else if (this.getStartTime() < arg0.getStartTime()) {
			return -1;
		}
		return 0;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public List<EventBasic> getEvents() {
		return events;
	}

	@Override
	public boolean equals(Object obj) {
		if (TimeTableEntry.class.isInstance(obj)) {
			TimeTableEntry newbie = (TimeTableEntry) obj;
			if (getStartTime() == newbie.getStartTime()) {
				return true;
			}
		}
		return super.equals(obj);
	}
}
