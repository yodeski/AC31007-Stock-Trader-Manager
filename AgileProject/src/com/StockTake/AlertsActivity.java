package com.StockTake;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AlertsActivity extends Activity {

	/** Called when the activity is first created. */
	StockManager myStockmanager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the StockManager
		myStockmanager = ((StockManager) getApplicationContext());

		setContentView(R.layout.alerts);

		/* Create Error Messages */
		TableLayout table = (TableLayout) this.findViewById(R.id.tableLayout2);
		TableRow errorRow = new TableRow(this);
		TextView error1 = new TextView(this);
		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 4;

		if (checkInternetConnection()) {
			try {
				int runs = myStockmanager.volumeTable(this);
				if (runs == 0) {

					error1.setText(Html.fromHtml(" <big>No Alerts</big><br/><br/>There are no runs, rockets or plummets on any of your share portfolio for the past trading day."));
					errorRow.addView(error1, params);
					table.addView(errorRow);
				}

			} catch (Exception e) {
				/* Parse Error */
				error1.setText(Html.fromHtml(" <big>Oops!</big><br/><br/> Something went wrong when we tried to retrieve your share portfolio.<br/><br/> Please try again later."));
				errorRow.addView(error1, params);
				table.addView(errorRow);
			}

		} else {
			/* No Internet Connection */
			error1.setText(Html.fromHtml(" <big>Oops!</big><br/><br/> It seems there is a problem with your internet connection."));
			errorRow.addView(error1, params);
			table.addView(errorRow);
		}
	}

	private boolean checkInternetConnection() {

		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE INTERNET
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}