package fr.mobiwide.agenda.ical;

public class ICalTag {

	public static String EVENT_START = "BEGIN:VEVENT";
	public static String EVENT_END = "END:VEVENT";
	
	public static String EVENT_UID = "UID:";	// Id of the event .
	public static String EVENT_DATE_STAMP = "DTSTAMP"; // Time the file was generated
	
	public static String EVENT_DATE_START = "DTSTART"; // Time the event start
	public static String EVENT_DATE_END = "DTEND"; // Time the event ends
	public static String EVENT_DATE_LAST_MODIFIED = "LAST-MODIFIED"; // When was last modified this event
	
	public static String EVENT_LOCATION = "LOCATION:"; // Where is the course held .

	public static String EVENT_SUMMARY = "SUMMARY:";
	public static String ICAL_TIMEZONE = "TZID:"; //?
	
	public static String EVENT_DESCRIPTION = "DESCRIPTION:";	
	public static String DATE_VALUE = "VALUE="; //?
	public static String DATE_TIMEZONE = "TZID="; //?
}
