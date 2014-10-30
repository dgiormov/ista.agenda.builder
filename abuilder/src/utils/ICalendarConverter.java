package utils;

import persistency.entities.Session;

public class ICalendarConverter {

	private Session e;

	public ICalendarConverter(Session e) {
		this.e = e;
	}

//	@Override
//	public String toString() {
//		return "BEGIN:VCALENDAR\n" 
//				+ "PRODID:-//" + Constants.EVENT_SHORT_NAME + "//Event//EN\n"
//				+ "VERSION:2.0\n"
//				+ "METHOD:PUBLISH\n"
//				+ "BEGIN:VEVENT\n"
//				+ "UID:" + Constants.EVENT_SHORT_NAME + "Event" + e.getId() + "\n"
//				+ "DTSTART:" + Constants.EVENT_DATE + "T" + getFormatedTime(e.getStartTime() + Constants.TIME_DIFERENCE) + "00Z\n"
//				+ "DTEND:" + Constants.EVENT_DATE + "T" + getFormatedTime(e.getStartTime() + Constants.TIME_DIFERENCE + e.getDuration()) + "00Z\n" 
//				+ "SUMMARY:" + e.getName() + "\n" 
//				+ "DESCRIPTION:" + e.getDescription() + "\n"
//				+ "LOCATION:" + e.getRoom() + "\n" 
//				+ "CLASS:PUBLIC\n"
//				+ "PRIORITY:3\n" 
//				+ "BEGIN:VALARM\n" 
//				+ "TRIGGER:-PT5M\n"
//				+ "ACTION:DISPLAY\n" 
//				+ "DESCRIPTION:Reminder\n"
//				+ "END:VALARM\n" 
//				+ "END:VEVENT\n" 
//				+ "END:VCALENDAR\n";
//	}

	private String getFormatedTime(int startTime) {
		if (startTime < 1000) {
			return "0" + startTime;
		}
		return Integer.toString(startTime);
	}
}
