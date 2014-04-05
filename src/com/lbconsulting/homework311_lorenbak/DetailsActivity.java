package com.lbconsulting.homework311_lorenbak;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class DetailsActivity extends FragmentActivity {

	private long mActiveItemID;
	private DetailsFragment mDetailsFragment;
	private View frag_details_placeholder;

	/*	private ActionBar mActionBar;*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Details_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_details);

		View frag_titles_placeholder = this.findViewById(R.id.frag_titles_placeholder);
		if (frag_titles_placeholder != null) {
			finish();
		}

		Bundle args = getIntent().getExtras();
		mActiveItemID = args.getLong("ActiveItemID", -1);

		/*		mActionBar = getActionBar();
				mActionBar.setDisplayShowTitleEnabled(false);
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);*/

		frag_details_placeholder = this.findViewById(R.id.frag_details_placeholder);
		if (frag_details_placeholder != null) {
			LoadItemsDetailsFragment();
		}

	}

	private void LoadItemsDetailsFragment() {
		mDetailsFragment = (DetailsFragment) this.getSupportFragmentManager().findFragmentByTag("DetailsFragment");
		if (mDetailsFragment == null) {
			// create DetailsFragment
			mDetailsFragment = DetailsFragment.newInstance(mActiveItemID);
			MyLog.i("Main_ACTIVITY", "LoadItemsDetailsFragment():NewInstance: itmeID=" + mActiveItemID);

			// add the fragment to the Activity
			this.getSupportFragmentManager().beginTransaction()
					.add(R.id.frag_details_placeholder, mDetailsFragment, "DetailsFragment")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
		} else {
			// mDetailsFragment exists ... so replace it
			mDetailsFragment = DetailsFragment.newInstance(mActiveItemID);
			// add the fragment to the Activity
			this.getSupportFragmentManager().beginTransaction()
					.replace(R.id.frag_details_placeholder, mDetailsFragment, "DetailsFragment")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
		}

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
				Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
						Toast.LENGTH_SHORT).show();
				RefreshItems();
				return true;

			case R.id.action_discardItems:
				Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
						Toast.LENGTH_SHORT).show();
				DiscardItems();
				return true;

			case R.id.action_acceptItems:
				Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
						Toast.LENGTH_SHORT).show();
				AcceptItems();
				return true;

			default:
				return super.onMenuItemSelected(featureId, item);
		}
	}

	private void RefreshItems() {
		// TODO Auto-generated method stub

	}

	private void DiscardItems() {
		// TODO Auto-generated method stub

	}

	private void AcceptItems() {
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
		SharedPreferences preferences = getSharedPreferences("HW311", MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();
		applicationStates.putLong("ActiveItemID", mActiveItemID);
		applicationStates.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Details_ACTIVITY", "onResume()");
		SharedPreferences storedStates = getSharedPreferences("HW311", MODE_PRIVATE);
		mActiveItemID = storedStates.getLong("ActiveItemID", -1);

		/*		if (frag_details_placeholder != null) {
					LoadItemsDetailsFragment();
				}*/
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
