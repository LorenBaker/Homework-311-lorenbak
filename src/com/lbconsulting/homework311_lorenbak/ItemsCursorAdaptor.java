package com.lbconsulting.homework311_lorenbak;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.homework311_lorenbak.database.ItemsTable;

public class ItemsCursorAdaptor extends CursorAdapter {

	public ItemsCursorAdaptor(Context context, Cursor c, int flags) {
		super(context, c, flags);
		MyLog.i("ItemsCursorAdaptor", "ItemsCursorAdaptor constructor.");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (cursor != null && view != null) {
			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			if (tvTitle != null) {
				String title = cursor.getString(cursor.getColumnIndexOrThrow(ItemsTable.COL_ITEM_TITLE));
				tvTitle.setText(title);
			}

			ImageView ivTitleIcon = (ImageView) view.findViewById(R.id.ivTitleIcon);
			if (ivTitleIcon != null) {

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
