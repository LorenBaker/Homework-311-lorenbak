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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.homework311_lorenbak.database.ItemsTable;

public class ItemsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	OnTitleSelected mOnTitleSelectedCallback;

	// Container Activity must implement this interface
	public interface OnTitleSelected {

		public void OnArticleSelected(long itemID);
	}

	private ItemsCursorAdaptor mItemsCursorAdaptor;
	private ListView mItemsListView;
	private TextView mEmptyView;

	private int ITEMS_LOADER_ID = 1;
	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mItemsFragmentCallbacks;

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
		mLoaderManager = getLoaderManager();
		mLoaderManager.initLoader(ITEMS_LOADER_ID, null, mItemsFragmentCallbacks);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("ItemsFragment", "onAttach()");
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnTitleSelectedCallback = (OnTitleSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedCallback");
		}
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

		mItemsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long itemID) {
				mOnTitleSelectedCallback.OnArticleSelected(itemID);
			}
		});

		mEmptyView = (TextView) view.findViewById(R.id.tvEmpty);

		mItemsFragmentCallbacks = this;

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
		if (newCursor != null && newCursor.getCount() > 0) {
			mItemsListView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);
		} else {
			mItemsListView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("ItemsFragment", "onLoaderReset: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(null);
	}

}
