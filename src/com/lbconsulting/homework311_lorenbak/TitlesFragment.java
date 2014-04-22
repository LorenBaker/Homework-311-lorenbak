package com.lbconsulting.homework311_lorenbak;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.homework311_lorenbak.database.ArticlesTable;
import com.lbconsulting.homework311_lorenbak.database.HW311ContentProvider;

public class TitlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	OnTitleSelected mOnTitleSelectedCallback;

	// Container Activity must implement this interface
	public interface OnTitleSelected {

		public void OnArticleSelected(long itemID);
	}

	private TitlesCursorAdaptor mItemsCursorAdaptor;
	private ListView mTitlesListView;
	private TextProgressBar pbLoadingIndicator;
	private TextView tvEmptyFragTitles;

	private int ITEMS_LOADER_ID = 1;
	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mTitlesFragmentCallbacks;

	public TitlesFragment() {
		// Empty constructor
	}

	public static TitlesFragment newInstance() {
		TitlesFragment f = new TitlesFragment();
		return f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("TitlesFragment", "onActivityCreated()");
		mLoaderManager = getLoaderManager();
		mLoaderManager.initLoader(ITEMS_LOADER_ID, null, mTitlesFragmentCallbacks);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("TitlesFragment", "onAttach()");
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
		MyLog.i("TitlesFragment", "onCreateView()");
		View view = inflater.inflate(R.layout.frag_titles_list, container, false);

		mTitlesListView = (ListView) view.findViewById(R.id.itemsListView);
		if (mTitlesListView != null) {
			mItemsCursorAdaptor = new TitlesCursorAdaptor(getActivity(), null, 0);
			mTitlesListView.setAdapter(mItemsCursorAdaptor);

			// set the list view's contextual mode
			mTitlesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

			mTitlesListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				private int nr = 0;

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// Do nothing
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					ArticlesTable.DeselectAllSelectedArticles(getActivity());
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					nr = 0;
					MenuInflater contextMenueInflater = getActivity().getMenuInflater();
					contextMenueInflater.inflate(R.menu.contextual_menu, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {

						case R.id.item_delete:
							nr = 0;
							mOnTitleSelectedCallback.OnArticleSelected(0);
							ArticlesTable.DeleteAllSelectedArticles(getActivity());
							mode.finish();
							break;

						default:
							break;
					}
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					if (checked) {
						nr++;
					} else {
						nr--;
					}
					ArticlesTable.setArticleSelection(getActivity(), id, checked);
					mode.setTitle(nr + " selected");

				}

			});

			mTitlesListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long itemID) {
					// An item has been selected ... show it's details via a call back to the TitlesActivity
					mOnTitleSelectedCallback.OnArticleSelected(itemID);
				}
			});

			mTitlesListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				// Contextual action mode
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long articleID) {
					mTitlesListView.setItemChecked(position, !ArticlesTable.isArticleSelected(getActivity(), articleID));
					return false;
				}
			});

		}

		tvEmptyFragTitles = (TextView) view.findViewById(R.id.tvEmptyFragTitles);
		pbLoadingIndicator = (TextProgressBar) view.findViewById(R.id.pbLoadingIndicator);
		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setText("Loading Articles");
		}

		mTitlesFragmentCallbacks = this;

		return view;
	}

	@Override
	public void onDestroy() {
		MyLog.i("TitlesFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		MyLog.i("TitlesFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() {
		MyLog.i("TitlesFragment", "onResume()");
		super.onResume();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		MyLog.i("TitlesFragment", "onCreateLoader(): LoaderId = " + id);
		CursorLoader cursorLoader = ArticlesTable.getAllItems(getActivity(), ArticlesTable.SORT_ORDER_ARTICLE_TITLE);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		int id = loader.getId();
		MyLog.i("TitlesFragment", "onLoadFinished: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(newCursor);
		if (newCursor != null && newCursor.getCount() > 0) {
			mTitlesListView.setVisibility(View.VISIBLE);
			tvEmptyFragTitles.setVisibility(View.GONE);
		} else {
			mTitlesListView.setVisibility(View.GONE);
			tvEmptyFragTitles.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("TitlesFragment", "onLoaderReset: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(null);
	}

	public void ShowLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.VISIBLE);
		}
		if (mTitlesListView != null) {
			mTitlesListView.setVisibility(View.GONE);
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}
	}

	public void DismissLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.GONE);
		}
		if (mTitlesListView != null) {
			mTitlesListView.setVisibility(View.VISIBLE);
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}
	}

	public void LoadArticles(String dataFilename) {
		new LoadArticlesTask().execute(dataFilename);
	}

	private void RefreshArticles(String dataFilename) {
		if (dataFilename != null && !dataFilename.isEmpty()) {
			AssetManager assetManager = getActivity().getAssets();
			if (assetManager != null) {
				InputStream input = null;
				try {
					input = assetManager.open(dataFilename);
					if (input != null) {
						ArticlesParser.parse(getActivity(), input);
						input.close();
					}
				} catch (IOException e) {
					MyLog.e("Titles_ACTIVITY",
							"RefreshArticles(): IOException opening " + dataFilename + "\n" + e.toString());

				} catch (XmlPullParserException e) {
					MyLog.e("Titles_ACTIVITY", "RefreshArticles(): XmlPullParserException parsing " + dataFilename
							+ "\n" + e.toString());
				}
			}
		}
	}

	private class LoadArticlesTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			ShowLoadingIndicator();
		}

		@Override
		protected Void doInBackground(String... dataFilename) {

			// Suppress content provider notifying loaders of changes
			// until after all article data has been updated in the database
			HW311ContentProvider.setSupressUpdates(true);
			RefreshArticles(dataFilename[0]);

			// Simulate an Internet download to allow the loading indicator
			// to be seen for a reasonable period of time
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				MyLog.e("Titles_ACTIVITY",
						"doInBackground(): InterruptedException " + dataFilename + "\n" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// Allow normal notification of updates
			HW311ContentProvider.setSupressUpdates(false);
			// Restart the loader to show database update changes
			mLoaderManager.restartLoader(ITEMS_LOADER_ID, null, mTitlesFragmentCallbacks);
			// Hide the loading indicator and show the article list
			DismissLoadingIndicator();

			super.onPostExecute(result);
		}

	}

}
