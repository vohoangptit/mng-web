package com.nera.nms.utils;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class BEDateUtils {

	public static final String MM_DD_YYYY_HH_MM_SS_WITH_HYPHEN = "MM-dd-yyyy hh:mm:ss";

	public static String formatDate(Timestamp timeStamp, String pattern) {
	    if(timeStamp == null) {
	        return StringUtils.EMPTY;
	    }
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return formatter.format(timeStamp.toLocalDateTime());
	}

	public static String convertDateToString(Date date, String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}
	
	public static Timestamp formatDate(String date, String pattern) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date parsedDate = dateFormat.parse(date);
		return new java.sql.Timestamp(parsedDate.getTime());
	}

	public static Date getCurrentDate() {
		long currentTimeMillis = System.currentTimeMillis();
		return new Date(currentTimeMillis);
	}

	public static String convertDateToStringByFormatOn(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
		return formatter.format(date).replace(" ", " on ");
	}

	private BEDateUtils() {}

	public static String formatSeconds(int timeInSeconds)
	{
		int hours = timeInSeconds / 3600;
		int secondsLeft = timeInSeconds - hours * 3600;
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft - minutes * 60;

		String formattedTime = "";
		if (hours < 10)
			formattedTime += "0";
		formattedTime += hours + ":";

		if (minutes < 10)
			formattedTime += "0";
		formattedTime += minutes + ":";

		if (seconds < 10)
			formattedTime += "0";
		formattedTime += seconds ;

		return formattedTime;
	}
}
