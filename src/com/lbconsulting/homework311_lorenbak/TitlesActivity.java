package com.lbconsulting.homework311_lorenbak;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lbconsulting.homework311_lorenbak.TitlesFragment.OnTitleSelected;

public class TitlesActivity extends FragmentActivity implements OnTitleSelected, SensorEventListener {

	private TitlesFragment mTitlesFragment;
	private DetailsFragment mDetailsFragment;
	private long mActiveArticleID;
	private Boolean mTwoFragmentLayout = false;

	private SensorManager mSensorManager;
	private Sensor mAccelerometerSensor;
	private float mPrevious_forceX = 0;
	private float mPrevious_forceY = 0;
	private float mPrevious_forceZ = 0;
	private long mPrevious_time = System.currentTimeMillis();

	private String DATA_FILENAME = "HRD311_data.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Titles_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_main);

		View frag_details_placeholder = this.findViewById(R.id.frag_details_placeholder);
		mTwoFragmentLayout = frag_details_placeholder != null
				&& frag_details_placeholder.getVisibility() == View.VISIBLE;

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	private void LoadTitlesFragment() {
		View frag_titles_placeholder = this.findViewById(R.id.frag_titles_placeholder);
		if (frag_titles_placeholder != null) {
			mTitlesFragment = (TitlesFragment) this.getSupportFragmentManager().findFragmentByTag("TitlesFragment");
			if (mTitlesFragment == null) {
				// create TitlesFragment
				mTitlesFragment = TitlesFragment.newInstance();
				MyLog.i("Titles_ACTIVITY", "LoadTitlesFragment():NewInstance");

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
			mDetailsFragment = DetailsFragment.newInstance(mActiveArticleID);
			MyLog.i("Titles_ACTIVITY", "LoadItemsDetailsFragment():NewInstance: itmeID=" + mActiveArticleID);

			// add the fragment to the Activity
			this.getSupportFragmentManager().beginTransaction()
					.add(R.id.frag_details_placeholder, mDetailsFragment, "DetailsFragment")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
		} else {
			// mDetailsFragment exists ... so replace it
			mDetailsFragment = DetailsFragment.newInstance(mActiveArticleID);
			// add the fragment to the Activity
			this.getSupportFragmentManager().beginTransaction()
					.replace(R.id.frag_details_placeholder, mDetailsFragment, "DetailsFragment")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
		}

	}

	@Override
	protected void onPause() {
		MyLog.i("Titles_ACTIVITY", "onPause()");
		mSensorManager.unregisterListener(this);

		SharedPreferences preferences = getSharedPreferences("HW311", MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();
		applicationStates.putLong("ActiveItemID", mActiveArticleID);
		applicationStates.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Titles_ACTIVITY", "onResume()");
		SharedPreferences storedStates = getSharedPreferences("HW311", MODE_PRIVATE);
		mActiveArticleID = storedStates.getLong("ActiveItemID", -1);

		mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

		LoadTitlesFragment();
		if (mTwoFragmentLayout) {
			LoadItemsDetailsFragment();
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MyLog.i("Titles_ACTIVITY", "onCreateOptionsMenu()");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {

		/*			case R.id.action_discardItems:
						Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
								Toast.LENGTH_SHORT).show();
						DiscardItems();
						return true;*/

			case R.id.action_refresh:
				RefreshItems();
				return true;

				/*			case R.id.action_acceptItems:
								Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
										Toast.LENGTH_SHORT).show();
								AcceptItems();
								return true;*/

			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private void DiscardItems() {
		// TODO Auto-generated method stub

	}

	private void AcceptItems() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("resource")
	private void RefreshItems() {

		/*		Toast.makeText(this, "\"" + "Refresh" + "\"" + " is under construction.",
						Toast.LENGTH_SHORT).show();*/

		AssetManager assetManager = getAssets();
		InputStream input = null;
		try {
			input = assetManager.open(DATA_FILENAME);
			ArticlesParser.parse(this, input);
			input.close();

		} catch (IOException e) {
			MyLog.e("Titles_ACTIVITY", "RefreshItems(): IOException opening " + DATA_FILENAME);
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			MyLog.e("Titles_ACTIVITY", "RefreshItems(): XmlPullParserException parsing " + DATA_FILENAME);
			e.printStackTrace();
		}

	}

	@Override
	protected void onStart() {
		MyLog.i("Titles_ACTIVITY", "onStart()");
		super.onStart();
	}

	@Override
	protected void onStop() {
		MyLog.i("Titles_ACTIVITY", "onStop()");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		MyLog.i("Titles_ACTIVITY", " onRestart()");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		MyLog.i("Titles_ACTIVITY", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void OnArticleSelected(long articleID) {
		if (articleID > 0) {
			mActiveArticleID = articleID;
			if (mTwoFragmentLayout) {
				LoadItemsDetailsFragment();
			} else {
				StartDetailsActivity();
			}
		}
	}

	private void StartDetailsActivity() {
		Intent detailsActivityIntent = new Intent(this, DetailsActivity.class);
		detailsActivityIntent.putExtra("ActiveArticleID", mActiveArticleID);
		startActivity(detailsActivityIntent);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not used

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometerSensor) {

			long curTime = System.currentTimeMillis();
			long diffTime = (curTime - mPrevious_time);
			if (diffTime > 100) {

				float forceX = event.values[0];
				float forceY = event.values[1];
				float forceZ = event.values[2];

				float shakeEventStrength =
						Math.abs((forceX - mPrevious_forceX) + (forceY - mPrevious_forceY)
								+ (forceZ - mPrevious_forceZ)) / diffTime * 1000;

				if (shakeEventStrength > 150) {
					RefreshItems();
				}

				mPrevious_forceX = forceX;
				mPrevious_forceY = forceY;
				mPrevious_forceZ = forceZ;
				mPrevious_time = curTime;
			}

		}
	}

}
