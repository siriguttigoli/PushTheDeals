package com.virtualkarma.pushthedeals.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.virtualkarma.pushthedeals.DealsFragment;
import com.virtualkarma.pushthedeals.parser.DOMParser;
import com.virtualkarma.pushthedeals.parser.RSSFeed;


/**
 * Loads an RSS feed from a given URL and writes the object
 * to a file in the application's /data directory. Parses 
 * through the feed and starts the main fragment control
 * upon completion.
 * 
 * @author Isaac Whitfield
 * @version 06/08/2013
 */
public class LoadRSSFeed extends AsyncTask<Void, Void, RSSFeed> {

    private static final String LOG_TAG = LoadRSSFeed.class.getSimpleName();

	// The parent context
	private Context context;
	// Dialog displaying a loading message
	private ProgressDialog refreshDialog;
	// The RSSFeed object
	private RSSFeed feed;
	// The URL we're parsing from
	private String RSSFEEDURL;


	public LoadRSSFeed(Context c, String url){
		// Set the parent
		context = c;
		// Set the feed URL
		RSSFEEDURL = url;
	}

	@Override
	protected RSSFeed doInBackground(Void... params) {
		// Parse the RSSFeed and save the object
        Log.d(LOG_TAG, "Url - " + RSSFEEDURL);
		feed = new DOMParser().parseXML(RSSFEEDURL);
		return feed;
	}

	@Override
	protected void onPreExecute(){
//		// Create a new dialog
//		refreshDialog = new ProgressDialog(new ContextThemeWrapper(parent, R.style.AlertBox));
//		// Inform of the refresh
//		refreshDialog.setMessage("Loading feed...");
//		// Spin the wheel whilst the dialog exists
//		refreshDialog.setIndeterminate(false);
//		// Don't exit the dialog when the screen is touched
//		refreshDialog.setCanceledOnTouchOutside(false);
//		// Don't exit the dialog when back is pressed
//		refreshDialog.setCancelable(true);
//		// Show the dialog
//		refreshDialog.show();
	}

	@Override
	protected void onPostExecute(RSSFeed result) {
		super.onPostExecute(result);

        DealsFragment.loadTaskCompleted(result);

	}
}