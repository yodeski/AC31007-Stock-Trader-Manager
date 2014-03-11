package com.StockTake;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class AgileProjectActivity extends TabActivity {
	
	StockManager myStockmanager = new StockManager();
	
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, SummaryActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Summary").setIndicator("Summary",
	                      res.getDrawable(R.drawable.ic_tab_summary))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	 // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, AlertsActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Alerts!").setIndicator("Alerts!",
	                      res.getDrawable(R.drawable.ic_tab_alerts))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    
		 // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, GraphActivity.class);
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Graphs").setIndicator("Graphs",
	                      res.getDrawable(R.drawable.ic_tab_graph))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, AlertSettingsActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Settings").setIndicator("Settings",
	                      res.getDrawable(R.drawable.ic_tab_settings))
	                  .setContent(intent);
	    tabHost.addTab(spec);


	    tabHost.setCurrentTab(0);
	}
	
	
}


