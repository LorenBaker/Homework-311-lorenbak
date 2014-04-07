package com.lbconsulting.homework311_lorenbak;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lbconsulting.homework311_lorenbak.TitlesFragment.OnTitleSelected;

public class TitlesActivity extends FragmentActivity implements OnTitleSelected {

	private TitlesFragment mTitlesFragment;
	private DetailsFragment mDetailsFragment;
	private long mActiveItemID;
	private Boolean mTwoFragmentLayout = false;

	private String DATA_FILENAME = "HRD311_data.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Main_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_main);

		View frag_details_placeholder = this.findViewById(R.id.frag_details_placeholder);
		mTwoFragmentLayout = frag_details_placeholder != null
				&& frag_details_placeholder.getVisibility() == View.VISIBLE;

	}

	private void LoadTitlesFragment() {
		View frag_titles_placeholder = this.findViewById(R.id.frag_titles_placeholder);
		if (frag_titles_placeholder != null) {
			mTitlesFragment = (TitlesFragment) this.getSupportFragmentManager().findFragmentByTag("TitlesFragment");
			if (mTitlesFragment == null) {
				// create TitlesFragment
				mTitlesFragment = TitlesFragment.newInstance();
				MyLog.i("Main_ACTIVITY", "LoadTitlesFragment():NewInstance");

				// add the fragment to the Activity
				this.getSupportFragmentManager().beginTransaction()
						.add(R.id.frag_titles_placeholder, mTitlesFragment, "TitlesFragment")
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();
			} else {
				// mDetailsFragment exists ... so replace it
				mTitlesFragment = TitlesFragment.newInstance();
				// add the fragment to the Activity
				this.getSupportFragmentManager().beginTransaction()
						.replace(R.id.frag_titles_placeholder, mTitlesFragment, "TitlesFragment")
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

		SharedPreferences preferences = getSharedPreferences("HW311", MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();
		applicationStates.putLong("ActiveItemID", mActiveItemID);
		applicationStates.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Main_ACTIVITY", "onResume()");
		SharedPreferences storedStates = getSharedPreferences("HW311", MODE_PRIVATE);
		mActiveItemID = storedStates.getLong("ActiveItemID", -1);

		LoadTitlesFragment();
		if (mTwoFragmentLayout) {
			LoadItemsDetailsFragment();
		}
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

	private void DiscardItems() {
		// TODO Auto-generated method stub

	}

	private void AcceptItems() {
		// TODO Auto-generated method stub

	}

	private void RefreshItems() {
		AssetManager assetManager = getAssets();
		InputStream input = null;
		try {
			input = assetManager.open(DATA_FILENAME);
			ItemsParser.parse(this, input);
			if (input != null) {
				input.close();
			}

		} catch (IOException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): IOException opening " + DATA_FILENAME);
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): XmlPullParserException parsing " + DATA_FILENAME);
			e.printStackTrace();
		}

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

	@Override
	public void OnArticleSelected(long itemID) {
		if (itemID > 0) {
			mActiveItemID = itemID;
			if (mTwoFragmentLayout) {
				LoadItemsDetailsFragment();
			} else {
				StartDetailsActivity();
			}
		}
	}

	private void StartDetailsActivity() {
		Intent detailsActivityIntent = new Intent(this, DetailsActivity.class);
		detailsActivityIntent.putExtra("ActiveItemID", mActiveItemID);
		startActivity(detailsActivityIntent);
	}
}
