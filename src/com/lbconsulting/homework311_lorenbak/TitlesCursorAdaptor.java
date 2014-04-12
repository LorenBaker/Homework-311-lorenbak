package com.lbconsulting.homework311_lorenbak;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lbconsulting.homework311_lorenbak.database.ArticlesTable;

public class TitlesCursorAdaptor extends CursorAdapter {

	public TitlesCursorAdaptor(Context context, Cursor c, int flags) {
		super(context, c, flags);
		MyLog.i("TitlesCursorAdaptor", "TitlesCursorAdaptor constructor.");
	}

	@Override
	public void bindView(View view, final Context context, final Cursor cursor) {
		if (cursor != null && view != null) {
			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			if (tvTitle != null) {
				String title = cursor.getString(cursor.getColumnIndexOrThrow(ArticlesTable.COL_ARTICLE_TITLE));
				tvTitle.setText(title);
			}

			TextView tvTitleIcon = (TextView) view.findViewById(R.id.tvTitleIcon);
			if (tvTitleIcon != null) {
				String firstLetterInTitle = cursor.getString(cursor
						.getColumnIndexOrThrow(ArticlesTable.COL_FIRST_LETTER_IN_TITLE));
				tvTitleIcon.setText(firstLetterInTitle);
			}

			long articleID = cursor.getLong(cursor.getColumnIndexOrThrow(ArticlesTable.COL_ARTICLE_ID));
			boolean isArticleSelected = ArticlesTable.isArticleSelected(context, articleID);
			boolean isArticleRead = ArticlesTable.isArticleRead(context, articleID);

			if (isArticleRead) {
				// view.setBackground(context.getResources().getDrawable(R.drawable.read_rectangle_black_stroke));
				view.setBackgroundColor(context.getResources().getColor(R.color.greyLight3));

			} else {
				// view.setBackground(context.getResources().getDrawable(R.drawable.default_rectangle_no_stroke));
				view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
			}

			if (isArticleSelected) {
				// view.setBackground(context.getResources().getDrawable(R.drawable.selected_rectangle_red_stroke));
				view.setBackgroundColor(context.getResources().getColor(R.color.blueLight));
			}

		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.title, parent, false);
		return view;
	}

}
