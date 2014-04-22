package com.lbconsulting.homework311_lorenbak;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class DetailsActivity extends FragmentActivity {

	private long mActiveItemID;
	private DetailsFragment mDetailsFragment;
	private View frag_details_placeholder;

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

		frag_details_placeholder = this.findViewById(R.id.frag_details_placeholder);
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

		if (frag_details_placeholder != null) {
			LoadItemsDetailsFragment();
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		MyLog.i("Details_ACTIVITY", "onDestroy()");
		super.onDestroy();
	}

}
