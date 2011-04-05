package net.mobiwide.agenda.ical;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.database.Cursor;


public class ICalEvent implements Comparable<ICalEvent> {

	
	private String ID = "LOCAL";
	
	private Date start;
	private Date end;
	private String summary;
	private String description;
	private String location;
	private TimeZone timeZone;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	// Extra
	private Date stamp;
	private Date lastModif;
	private String uid;
	
	public ICalEvent(TimeZone timeZone) {
		super();
		this.timeZone = timeZone;
		DATE_FORMAT.setTimeZone(timeZone);
	}
	
	@Override
	public int compareTo (ICalEvent another) {
		return (this.uid.equals(another.uid))?0:-1;
	}

	public Boolean isModified (ICalEvent another) {
		
		if ( this.start.compareTo(another.start) != 0 
			|| this.end.compareTo(another.end) != 0
			|| ! this.location.equals(another.getLocation())
					) // ADD TITLE , SUMMARY ....
			return true;
		
		return false;
	}

	public boolean isWholeDayEvent() {
		return start.getHours() == 0 && start.getMinutes() == 0 && start.getSeconds() == 0
			&& end.getHours() == 0 && end.getMinutes() == 0 && end.getSeconds() == 0;
	}
	
	static public ICalEvent fromCursor ( Cursor managedCursor ) {
		
		ICalEvent event = new ICalEvent(TimeZone.getDefault());
		
//        for (int i = 0; i < managedCursor.getColumnCount(); i++) {
//            Log.i(LOG_TAG, managedCursor.getColumnName(i) + "="
//                    + managedCursor.getString(i));
//        }
		
		event.setID(managedCursor.getString(managedCursor.getColumnIndex("_id")));
		event.setDescription(managedCursor.getString(managedCursor.getColumnIndex("description")));
		event.setLocation(managedCursor.getString(managedCursor.getColumnIndex("eventLocation")));
		event.setSummary(managedCursor.getString(managedCursor.getColumnIndex("title")));
		
		String extra =  managedCursor.getString(managedCursor.getColumnIndex("originalEvent"));
		if ( extra == null )
			return null;
		
		System.out.println("originalEvent=" + extra);

		
		String [] info = extra.split(";");
		
		if ( info.length != 2 )
			return null ; // event not of ours
		
		event.setUid(info[0]);
		
		long lastmodif = Long.valueOf(info[1]).longValue();		// SERT A QUOI LE LAST MODIF REPLACE BY ID TREE !!
		long startTime = managedCursor.getLong(managedCursor.getColumnIndex("dtstart"));
		long endTime = managedCursor.getLong(managedCursor.getColumnIndex("dtend"));
		
		event.setStart(new Date(startTime));
		event.setEnd(new Date(endTime));
		event.setLastModif(new Date(lastmodif));
		
		return event;
	}

	public String getID() {
		return ID;
	}


	public void setID(String iD) {
		ID = iD;
	}


	public Date getStart() {
		return start;
	}


	public void setStart(Date start) {
		this.start = start;
	}


	public Date getEnd() {
		return end;
	}


	public void setEnd(Date end) {
		this.end = end;
	}


	public String getSummary() {
		return summary;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public TimeZone getTimeZone() {
		return timeZone;
	}


	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}


	public Date getStamp() {
		return stamp;
	}


	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}


	public Date getLastModif() {
		return lastModif;
	}


	public void setLastModif(Date lastModif) {
		this.lastModif = lastModif;
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public static SimpleDateFormat getDateFormat() {
		return DATE_FORMAT;
	}
	
	
	

}
