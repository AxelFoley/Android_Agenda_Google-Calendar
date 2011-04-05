package net.mobiwide.gcalendar;

import net.mobiwide.agenda.google.GoogleAgenda;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        // Test
        GoogleAgenda agenda = new GoogleAgenda(this);
        agenda.listAllCalendarDetails();
        
        
    }
}