package fr.mobiwide.agenda.google;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import fr.mobiwide.agenda.ical.ICalEvent;

public class GoogleAgenda {

	public static String LOG_TAG = "GoogleAgenda";
	
	private String CALENDAR_NAME = "ESIL PLANNING";
	private String sync_account = "rickghanem@gmail.com";
	private Activity activity;
//	private String calendarDisplayName;
//	private List<ICalEvent> events;
//	private boolean removeCalendar = false;

	
	public GoogleAgenda(Activity activity/*, String calendarDisplayName, List<ICalEvent> events*/) {
		this.activity = activity;
//		this.calendarDisplayName = calendarDisplayName;
//		this.events = events;
	}
	
//	
//	@Override
//	public void run() {
//		
//		if (removeCalendar) {
//			super.sendInitMessage(R.string.removingCalendarEntries);
//			removeExistingCalendar();
//			super.sendFinishedMessage();
//			return;
//		}
//		
//		super.sendInitMessage(R.string.writingCalendarEntries);
//		
//		int calendarId;
//		removeExistingCalendar();
//		
//		
//		try {
//			createNewCalendar(CALENDAR_NAME, calendarDisplayName);	
//		}
//		catch (IllegalArgumentException e) {
//			Log.e(LOG_TAG, "Error when trying to create calendar!", e);
//			super.sendErrorMessage(R.string.errorWhenCreatingCalendar);
//			return;
//		}
//		calendarId = findUpdateCalendar();
//		
//		if (calendarId == -1) {
//			Log.e(LOG_TAG, "Couldn' find and create calendar for updating!");
//			super.sendErrorMessage(R.string.couldNotAccessLocalCalendar);
//			return;
//		}
//		
//		super.sendMaximumMessage(events.size());
//		
//		int i = 0;
//		for (ICalEvent event : events) {
//			Log.d(LOG_TAG,"Creating event: " + event);
//			createEvent(calendarId, event);
//			i++;
//			super.sendProgressMessage(i);
//		}
//		
//		super.sendFinishedMessage();
//	}


	public void removeExistingCalendar() {
		int calendarId = findUpdateCalendar();
		
		if (calendarId != -1) {
			deleteCalendar(calendarId);
		}
	}
	
	public void createNewCalendar(String name, String displayName) {
		ContentValues calendar = new ContentValues();
		//calendar.put("_", 3);
		calendar.put("_sync_account", sync_account); // My account
		calendar.put("_sync_account_type","com.google"); 
		//calendar.put("_sync_id", 1); // null
		calendar.put("name", name);
		calendar.put("displayName",displayName);
		calendar.put("hidden",0);
		calendar.put("color",0xFF008080);
		calendar.put("access_level", 700);
		//calendar.put("selected", 0); // 0
		calendar.put("sync_events", 1);
		//calendar.put("createdByCategory", 0); //Doesn't work on Motorola Droid / Milestone
		calendar.put("timezone", "Europe/Paris");
		calendar.put("ownerAccount", sync_account);
		Uri calendarUri = Uri.parse(getCalendarUriBase() + "calendars");
		activity.getContentResolver().insert(calendarUri, calendar);		
	}
	
	public int deleteCalendar(int calendarId) {
        int iNumRowsDeleted = 0;

        Uri eventsUri = Uri.parse(getCalendarUriBase()+"calendars");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, calendarId);
        iNumRowsDeleted = activity.getContentResolver().delete(eventUri, null, null);

        Log.i(LOG_TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");

        return iNumRowsDeleted;
    }
	
    public ArrayList<ICalEvent> listAllCalendarEntries(int calendarId, Date startSync, Date endSync) {

    	ArrayList<ICalEvent> events = new ArrayList<ICalEvent>();
    	
    	long startDate = startSync.getTime();
    	long endDate = endSync.getTime();
    	
        Cursor managedCursor = getCalendarManagedCursor(null, "calendar_id="
                + calendarId + " AND dtstart BETWEEN " + startDate + " AND " + endDate, "events");

        if (managedCursor != null && managedCursor.moveToFirst()) {

            Log.i(LOG_TAG, "Listing Calendar Event Details");

            do {


                ICalEvent event = ICalEvent.fromCursor(managedCursor);

                if ( event != null) {
                	events.add(event);
                	
					Log.i(LOG_TAG, "**START Calendar Event Description**");
					System.out.println(event);
					Log.i(LOG_TAG, "**END Calendar Event Description**");

                }
                else
                	System.out.println("Event not ours !!!");
                


            } while (managedCursor.moveToNext());
            
            managedCursor.close();
            
            return events;
        } else {
            Log.i(LOG_TAG, "No Event for Calendar");
        }
        
        
        return null;
    }
    

    public int updateEvent ( ICalEvent event ) {
    	
    	ContentValues eventValues = new ContentValues();
    	
        eventValues.put("title", event.getSummary());
        eventValues.put("description", event.getDescription());
        eventValues.put("eventLocation", event.getLocation());
    	
        eventValues.put("originalEvent", event.getUid() + ";" + event.getLastModif().getTime() );
        
        long startTime = event.getStart().getTime();//System.currentTimeMillis() + 1000 * 60 * 60;
        long endTime = event.getEnd().getTime();//System.currentTimeMillis() + 1000 * 60 * 60 * 2;

        eventValues.put("dtstart", (event.isWholeDayEvent() ? endTime : startTime));
        eventValues.put("dtend", endTime);
    	
        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events/");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, Integer.valueOf(event.getID()).intValue());

        return activity.getContentResolver().update(eventUri, eventValues, null, null);
    }
    
	public int deleteEvent ( String id ) {
		Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, Integer.valueOf(id).intValue());
		return activity.getContentResolver().delete(eventUri, null, null);
	}
	
    public Uri createEvent(int calId, ICalEvent event) {
        ContentValues eventValues = new ContentValues();

        eventValues.put("calendar_id", calId);
        eventValues.put("title", event.getSummary());
        eventValues.put("description", event.getDescription());
        eventValues.put("eventLocation", event.getLocation());
        
//      eventValues.put("UID", "ADE52556e6976657273697465323031302f323031312d323637392d302d3131");
// 		originalEvent
        eventValues.put("originalEvent", event.getUid() + ";" + event.getLastModif().getTime() );
        
        long startTime = event.getStart().getTime();//System.currentTimeMillis() + 1000 * 60 * 60;
        long endTime = event.getEnd().getTime();//System.currentTimeMillis() + 1000 * 60 * 60 * 2;

        eventValues.put("dtstart", (event.isWholeDayEvent() ? endTime : startTime));
        eventValues.put("dtend", endTime);

        eventValues.put("allDay", (event.isWholeDayEvent() ? 1 : 0)); // 0 for false, 1 for true
        eventValues.put("eventStatus", 1);
        eventValues.put("visibility", 0);
        eventValues.put("transparency", 0);
        eventValues.put("hasAlarm", 0); // 0 for false, 1 for true
        
        System.out.println("USING SYNC ACCOUNT " + sync_account);
        
        eventValues.put("_sync_account_type", sync_account);

        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");

        Uri insertedUri = activity.getContentResolver().insert(eventsUri, eventValues);
        return insertedUri;
    }

	
	public HashMap<String, String> listAllCalendarDetails() {
		
		String[] projection = new String[] { "_id", "_sync_account", "name", "displayName" };
		String selection = "selected=1";
        Cursor managedCursor = getCalendarManagedCursor(projection, selection, "calendars");
        
        HashMap<String, String> calendars = new HashMap<String, String>();
        
        if (managedCursor != null && managedCursor.moveToFirst()) {
        	
            Log.i(LOG_TAG, "Listing Calendars with Details");

            do {

                Log.i(LOG_TAG, "**START Calendar Description**");

                for (int i = 0; i < managedCursor.getColumnCount(); i++) {
                    Log.i(LOG_TAG, managedCursor.getColumnName(i) + "="
                            + managedCursor.getString(i));
                }
                
                String id = managedCursor.getString(managedCursor.getColumnIndex("_id"));
                String title = managedCursor.getString(managedCursor.getColumnIndex("displayName"));
                
                if ( id.equals("1") ) {
                    sync_account = managedCursor.getString(managedCursor.getColumnIndex("_sync_account"));
                }
                
                calendars.put(id, title);
                
                Log.i(LOG_TAG, "**END Calendar Description**");
            } while (managedCursor.moveToNext());
            
            managedCursor.close();
        } else {
            Log.i(LOG_TAG, "No Calendars");
        }
        
        return calendars;
    }
	
	public int findUpdateCalendar() {
        int result = -1;
        
        String[] projection = new String[] { "_id", "name" };
        String selection = "selected=1";
        String path = "calendars";

        Cursor managedCursor = getCalendarManagedCursor(projection, selection,
                path);

        if (managedCursor != null && managedCursor.moveToFirst()) {

            Log.i(LOG_TAG, "Listing Selected Calendars Only");

            int nameColumn = managedCursor.getColumnIndex("name");
            int idColumn = managedCursor.getColumnIndex("_id");

            do {
                String calName = managedCursor.getString(nameColumn);
                String calId = managedCursor.getString(idColumn);
                Log.i(LOG_TAG, "Found Calendar '" + calName + "' (ID=" + calId + ")");
                if (calName != null && calName.equals(CALENDAR_NAME)) {
                	result = Integer.parseInt(calId);
                }
            } while (managedCursor.moveToNext());
        } else {
            Log.i(LOG_TAG, "No Calendars");
        }

        return result;

    }

    private Cursor getCalendarManagedCursor(String[] projection, String selection, String path) {
        Uri calendars = Uri.parse("content://calendar/" + path);

        Cursor managedCursor = null;
        try {
            managedCursor = activity.managedQuery(calendars, projection, selection, null, null);
        } catch (IllegalArgumentException e) {
            Log.w(LOG_TAG, "Failed to get provider at [" + calendars.toString() + "]");
        }

        if (managedCursor == null) {
            // try again
            calendars = Uri.parse("content://com.android.calendar/" + path);
            try {
                managedCursor = activity.managedQuery(calendars, projection, selection,  null, null);
            } catch (IllegalArgumentException e) {
                Log.w(LOG_TAG, "Failed to get provider at ["  + calendars.toString() + "]");
            }
        }
        return managedCursor;
    }
    

    /*
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
     */
    private String getCalendarUriBase() {
   	
        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = activity.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
            // eat
        }

        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = activity.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
                // eat
            }

            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }

        }

        managedCursor.close();
        
        return calendarUriBase;
    }

    
//	public void setRemoveCalendar(boolean remove) {
//		this.removeCalendar = remove;
//	}
}
