package com.lbconsulting.homework311_lorenbak;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lbconsulting.homework311_lorenbak.database.ItemsTable;

public class ItemsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private ItemsCursorAdaptor mItemsCursorAdaptor;
	private ListView mItemsListView;

	public ItemsFragment() {
		// Empty constructor
	}

	public static ItemsFragment newInstance() {
		ItemsFragment f = new ItemsFragment();
		return f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("ItemsFragment", "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("ItemsFragment", "onAttach()");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("ItemsFragment", "onCreateView()");

		View view = inflater.inflate(R.layout.frag_titles_list, container, false);

		mItemsListView = (ListView) view.findViewById(R.id.itemsListView);
		if (mItemsListView != null) {
			mItemsCursorAdaptor = new ItemsCursorAdaptor(getActivity(), null, 0);
			mItemsListView.setAdapter(mItemsCursorAdaptor);
		}

		return view;
	}

	@Override
	public void onDestroy() {
		MyLog.i("ItemsFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		MyLog.i("ItemsFragment", "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		MyLog.i("ItemsFragment", "onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		MyLog.i("ItemsFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() {
		MyLog.i("ItemsFragment", "onResume()");
		super.onResume();
	}

	@Override
	public void onStart() {
		MyLog.i("ItemsFragment", "onStart()");
		super.onStart();
	}

	@Override
	public void onStop() {
		MyLog.i("ItemsFragment", "onStop()");
		super.onStop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		MyLog.i("ItemsFragment", "onCreateLoader(): LoaderId = " + id);
		CursorLoader cursorLoader = ItemsTable.getAllItems(getActivity(), ItemsTable.SORT_ORDER_ITEM_TITLE);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		int id = loader.getId();
		MyLog.i("ItemsFragment", "onLoadFinished: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(newCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("ItemsFragment", "onLoaderReset: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(null);
	}

}
