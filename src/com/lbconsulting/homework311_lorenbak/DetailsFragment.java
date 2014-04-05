package com.lbconsulting.homework311_lorenbak;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbconsulting.homework311_lorenbak.database.ItemsTable;

public class DetailsFragment extends Fragment {

	private long mActiveItemID;
	private LinearLayout fragDetailsLinearLayout;
	private TextView tvEmptyFragDetails;

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

		if (savedInstanceState != null && savedInstanceState.containsKey("itemID")) {
			mActiveItemID = savedInstanceState.getLong("itemID", 0);
		} else {
			Bundle bundle = getArguments();
			if (bundle != null)
				mActiveItemID = bundle.getLong("itemID", 0);
		}

		View view = inflater.inflate(R.layout.frag_details, container, false);
		fragDetailsLinearLayout = (LinearLayout) view.findViewById(R.id.fragDetailsLinearLayout);
		tvEmptyFragDetails = (TextView) view.findViewById(R.id.tvEmptyFragDetails);

		if (mActiveItemID > 0) {
			Cursor cursor = ItemsTable.getItem(getActivity(), mActiveItemID);

			if (fragDetailsLinearLayout != null) {
				fragDetailsLinearLayout.setVisibility(View.VISIBLE);
			}
			if (tvEmptyFragDetails != null) {
				tvEmptyFragDetails.setVisibility(View.GONE);
			}

			if (cursor != null && view != null) {
				cursor.moveToFirst();
				TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
				if (tvTitle != null) {
					String title = cursor.getString(cursor.getColumnIndexOrThrow(ItemsTable.COL_ITEM_TITLE));
					tvTitle.setText(title);
				}

				TextView tvTitleIcon = (TextView) view.findViewById(R.id.tvTitleIcon);
				if (tvTitleIcon != null) {
					String firstLetterInTitle = cursor.getString(cursor
							.getColumnIndexOrThrow(ItemsTable.COL_FIRST_LETTER_IN_TITLE));
					tvTitleIcon.setText(firstLetterInTitle);
				}

				TextView tvContent = (TextView) view.findViewById(R.id.tvContent);

				if (tvContent != null) {
					String content = cursor.getString(cursor.getColumnIndexOrThrow(ItemsTable.COL_ITEM_CONTENT));
					tvContent.setText(content);
				}

			}

			if (cursor != null) {
				cursor.close();
			}

		} else {
			if (fragDetailsLinearLayout != null) {
				fragDetailsLinearLayout.setVisibility(View.GONE);
			}
			if (tvEmptyFragDetails != null) {
				tvEmptyFragDetails.setVisibility(View.VISIBLE);
			}
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		MyLog.i("DetailsFragment", "onSaveInstanceState()");
		// Store our listID
		outState.putLong("itemID", mActiveItemID);
		super.onSaveInstanceState(outState);
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
