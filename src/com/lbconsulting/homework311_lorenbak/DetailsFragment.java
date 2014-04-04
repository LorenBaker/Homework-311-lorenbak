package com.lbconsulting.homework311_lorenbak;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailsFragment extends Fragment {

	public DetailsFragment() {
		// Empty constructor
	}

	public static DetailsFragment newInstance(long itemID) {
		DetailsFragment f = new DetailsFragment();
		// Supply itemID input as an argument.
		Bundle args = new Bundle();
		args.putLong("itemID", itemID);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("DetailsFragment", "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("DetailsFragment", "onAttach()");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("DetailsFragment", "onCreateView()");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		MyLog.i("DetailsFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		MyLog.i("DetailsFragment", "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		MyLog.i("DetailsFragment", "onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		MyLog.i("DetailsFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() {
		MyLog.i("DetailsFragment", "onResume()");
		super.onResume();
	}

	@Override
	public void onStart() {
		MyLog.i("DetailsFragment", "onStart()");
		super.onStart();
	}

	@Override
	public void onStop() {
		MyLog.i("DetailsFragment", "onStop()");
		super.onStop();
	}

}
