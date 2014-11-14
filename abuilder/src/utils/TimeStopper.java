package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import persistency.entities.Session;

public class TimeStopper {

	public static boolean canExecuteAround(Session session){
		return timeToSessionAndHalf(session) > 0;
	}

	public static long timeToSessionAndHalf(Session session) {
		Date date = session.getDate();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EET"));
//		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, session.getStartTime()/100);
		calendar.set(Calendar.MINUTE, session.getStartTime()%100);
		calendar.add(Calendar.MINUTE, session.getDuration()/2);
		return (System.currentTimeMillis() - calendar.getTimeInMillis())/(60*1000);
	}
}
