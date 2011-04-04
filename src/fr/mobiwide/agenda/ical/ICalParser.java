package fr.mobiwide.agenda.ical;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import android.util.Log;

public class ICalParser {
	private static final String LOG_TAG = "ICalParser";
	
    /* Ical Dateformat */
    private static SimpleDateFormat ICAL_DATETIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static SimpleDateFormat ICAL_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
    public ArrayList<ICalEvent> events = new ArrayList<ICalEvent>();
    
    
	private TimeZone icalDefaultTimeZone;
	private TimeZone userTimeZone;
	
	public ICalParser(String icalDefaultTimeZone, String userTimeZone, String toParse) {
		
		if (icalDefaultTimeZone.equalsIgnoreCase(ICalPrefs.DEFAULT_TIMEZONE_PREF_VALUE)) {
			this.icalDefaultTimeZone = TimeZone.getTimeZone("UTC");
		}
		else {
			this.icalDefaultTimeZone = TimeZone.getTimeZone(icalDefaultTimeZone);
		}
		
		if (userTimeZone.equals(ICalPrefs.DEFAULT_TIMEZONE_PREF_VALUE)) {
			this.userTimeZone = TimeZone.getDefault();
		}
		else {
			this.userTimeZone = TimeZone.getTimeZone(userTimeZone);
		}
		
        ICAL_DATETIME_FORMAT.setTimeZone(this.icalDefaultTimeZone);
        ICAL_DATE_FORMAT.setTimeZone(this.icalDefaultTimeZone);
		

		
		String[] lines = toParse.split("\n");
		
		
		ICalEvent event = null;
		
		int i = 0;
		
		boolean inDescription = false;
		String description = "";
		
		for (String line : lines) {
			line = StringUtils.chomp(line);
			
			if (event == null) {
				if (line.contains(ICalTag.EVENT_START)) {
					event = new ICalEvent(this.userTimeZone);
				}
				inDescription = false;
			}
			else  if (line.contains(ICalTag.EVENT_END)) {
				event.setDescription(cleanText(description));
				
				if (event != null && event.getStart() != null) {
					events.add(event);
//					System.out.println(event);
				}
				event = null;
				description = "";
				inDescription = false;
			}
			else {
				if (line.contains(ICalTag.EVENT_SUMMARY)) {
					event.setSummary(cleanText(line.substring(ICalTag.EVENT_SUMMARY.length())));
				}
				else if (line.contains(ICalTag.EVENT_DATE_START)) {
					String dateLine = line.substring(ICalTag.EVENT_DATE_START.length());
					event.setStart(parseIcalDate(dateLine));
				}
				else if (line.contains(ICalTag.EVENT_DATE_END)) {
					String dateLine = line.substring(ICalTag.EVENT_DATE_END.length());
					event.setEnd(parseIcalDate(dateLine));
				}
				else if (line.contains(ICalTag.EVENT_DATE_STAMP)) {
					String dateLine = line.substring(ICalTag.EVENT_DATE_STAMP.length());
					event.setStamp(parseIcalDate(dateLine));
				}
				else if (line.contains(ICalTag.EVENT_DATE_LAST_MODIFIED)) {
					String dateLine = line.substring(ICalTag.EVENT_DATE_LAST_MODIFIED.length());
					event.setLastModif(parseIcalDate(dateLine));
				}
				else if (line.contains(ICalTag.EVENT_LOCATION)) {
					String location = line.substring(ICalTag.EVENT_LOCATION.length());
					event.setLocation(location);
				}
				else if (line.contains(ICalTag.EVENT_UID)) {
					String uid = line.substring(ICalTag.EVENT_UID.length());
					event.setUid(uid);
				}
				
				if (inDescription) {
					if (line.charAt(0) == ' ') {
						description += line.substring(1);
					}
				}
				else if (line.contains(ICalTag.EVENT_DESCRIPTION)) {
					description = line.substring(ICalTag.EVENT_DESCRIPTION.length());
					inDescription = true;
				}
				else {
					inDescription = false;
				}
			}
			i++;
		}
		
//		System.out.println("Total Event =" + events.size());
		
	}
	
	private String cleanText(String text) {
		text = StringUtils.replace(text, "\\n", "\n");
		text = StringUtils.replace(text, "\\,", ",");
		text = StringUtils.replace(text, "\\\"", "\"");
		return text;
	}
	
	private Date parseIcalDate(String dateLine)  {
		try {
			dateLine = StringUtils.replace(dateLine, ";", "");
			Date date = null;
			if (dateLine.contains(ICalTag.DATE_TIMEZONE)) {
				String[] parts = StringUtils.split(dateLine,":");
				ICAL_DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone(parts[0].substring(ICalTag.DATE_TIMEZONE.length(), parts[0].length())));
				date = ICAL_DATETIME_FORMAT.parse(parts[1]);
				ICAL_DATETIME_FORMAT.setTimeZone(icalDefaultTimeZone);
			}
			else if (dateLine.contains(ICalTag.DATE_VALUE)) {
				String[] parts = StringUtils.split(dateLine,":");
				date = ICAL_DATE_FORMAT.parse(parts[1]);
				date.setHours(0);
				date.setMinutes(0);
				date.setSeconds(0);
			}
			else {
				dateLine = StringUtils.replace(dateLine, ":", "");
				date = ICAL_DATETIME_FORMAT.parse(dateLine);
			}
			return date;
		}
		catch (ParseException e) {
			Log.e(LOG_TAG, "Cant't parse date!", e);
			return null;
		}
    }
}
