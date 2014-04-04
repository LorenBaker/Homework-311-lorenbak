package com.lbconsulting.homework311_lorenbak;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private ItemsFragment mItemsFragment;
	private DetailsFragment mDetailsFragment;
	private long mActiveItemID;
	private Boolean mTwoFragmentLayout = false;

	private String DATA_FILENAME = "HRD311_data.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Main_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_main);

		LoadTitlesFragment();

		View frag_details_placeholder = this.findViewById(R.id.frag_details_placeholder);
		mTwoFragmentLayout = frag_details_placeholder != null
				&& frag_details_placeholder.getVisibility() == View.VISIBLE;

		if (mTwoFragmentLayout) {
			LoadItemsDetailsFragment();
		}
	}

	private void LoadTitlesFragment() {
		View frag_titles_placeholder = this.findViewById(R.id.frag_titles_placeholder);
		if (frag_titles_placeholder != null) {
			mItemsFragment = (ItemsFragment) this.getSupportFragmentManager().findFragmentByTag("ItemsFragment");
			if (mItemsFragment == null) {
				// create ItemsFragment
				mItemsFragment = ItemsFragment.newInstance();
				MyLog.i("Main_ACTIVITY", "LoadTitlesFragment():NewInstance");

				// add the fragment to the Activity
				this.getSupportFragmentManager().beginTransaction()
						.add(R.id.frag_titles_placeholder, mItemsFragment, "ItemsFragment")
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();
			} else {
				// mDetailsFragment exists ... so replace it
				mItemsFragment = ItemsFragment.newInstance();
				// add the fragment to the Activity
				this.getSupportFragmentManager().beginTransaction()
						.replace(R.id.frag_titles_placeholder, mItemsFragment, "ItemsFragment")
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();
			}

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
	protected void onPause() {
		MyLog.i("Main_ACTIVITY", "onPause()");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Main_ACTIVITY", "onResume()");
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MyLog.i("Main_ACTIVITY", "onCreateOptionsMenu()");
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
		MyLog.i("Main_ACTIVITY", "onStart()");
		super.onStart();
	}

	@Override
	protected void onStop() {
		MyLog.i("Main_ACTIVITY", "onStop()");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		MyLog.i("Main_ACTIVITY", " onRestart()");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		MyLog.i("Main_ACTIVITY", "onDestroy()");
		super.onDestroy();
	}

}
