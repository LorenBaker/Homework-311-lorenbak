package com.lbconsulting.homework311_lorenbak;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DetailsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Details_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MyLog.i("Details_ACTIVITY", "onCreateOptionsMenu()");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.action_refresh:
				Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.", Toast.LENGTH_SHORT)
						.show();
				RefreshItems();
				return true;

			default:
				return super.onMenuItemSelected(featureId, item);
		}
	}

	private void RefreshItems() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart() {
		MyLog.i("Details_ACTIVITY", "onStart()");
		super.onStart();
	}

	@Override
	protected void onStop() {
		MyLog.i("Details_ACTIVITY", "onStop()");
		super.onStop();
	}

	@Override
	protected void onPause() {
		MyLog.i("Details_ACTIVITY", "onPause()");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Details_ACTIVITY", "onResume()");
		super.onResume();
	}

	@Override
	protected void onRestart() {
		MyLog.i("Details_ACTIVITY", " onRestart()");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		MyLog.i("Details_ACTIVITY", "onDestroy()");
		super.onDestroy();
	}

}
