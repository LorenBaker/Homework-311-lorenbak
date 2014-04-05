package com.lbconsulting.homework311_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.lbconsulting.homework311_lorenbak.MyLog;

public class ItemsTable {

	// Items data table
	// Version 1
	public static final String TABLE_ITEMS = "tblItems";
	public static final String COL_ITEM_ID = "_id";
	public static final String COL_ITEM_TITLE = "itemTitle";
	public static final String COL_FIRST_LETTER_IN_TITLE = "firstLetterInTitle";
	public static final String COL_ITEM_CONTENT = "itemContent";
	public static final String COL_ITEM_CHECKED = "itemChecked";
	public static final String COL_REFRESH_DATE_TIME = "refreshDateTime";

	public static final String[] PROJECTION_ALL = { COL_ITEM_ID, COL_ITEM_TITLE, COL_FIRST_LETTER_IN_TITLE,
			COL_ITEM_CONTENT, COL_ITEM_CHECKED, COL_REFRESH_DATE_TIME };

	public static final String CONTENT_PATH = "articles";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + HW311ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	public static final String SORT_ORDER_ITEM_TITLE = COL_ITEM_TITLE + " ASC";

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_ITEMS
			+ " ("
			+ COL_ITEM_ID + " integer primary key autoincrement, "
			+ COL_ITEM_TITLE + " text collate nocase, "
			+ COL_FIRST_LETTER_IN_TITLE + " text collate nocase, "
			+ COL_ITEM_CONTENT + " text collate nocase, "
			+ COL_ITEM_CHECKED + " integer default 0, " // 0 = item not checked; 1 = item checked
			+ COL_REFRESH_DATE_TIME + " integer"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("ItemsTable", "onCreate: " + TABLE_ITEMS + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_ITEMS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateItem(Context context, String itemTitle, String itemContent) {
		// NOTE: COL_REFRESH_DATE_TIME is updated via the content provider
		long newItemID = -1;
		if (!itemTitle.isEmpty()) {
			// determine if the title is already in the database
			@SuppressWarnings("resource")
			Cursor itemCursor = getItem(context, itemTitle);
			if (itemCursor != null) {
				if (itemCursor.getCount() > 0) {
					// the item exists in the database
					// replace the item's content and update the refresh date/time
					itemCursor.moveToFirst();
					newItemID = itemCursor.getLong(itemCursor.getColumnIndexOrThrow(COL_ITEM_ID));
					ContentValues newFieldValues = new ContentValues();
					newFieldValues.put(COL_ITEM_CONTENT, itemContent);
					UpdateItemFieldValues(context, newItemID, newFieldValues);
					itemCursor.close();
					return newItemID;
				}
			}
			// the item does not exist in the database ... so create it
			String firstLetterInTitle = itemTitle.substring(0, 1).toUpperCase();

			ContentValues newFieldValues = new ContentValues();
			newFieldValues.put(COL_ITEM_TITLE, itemTitle);
			newFieldValues.put(COL_FIRST_LETTER_IN_TITLE, firstLetterInTitle);
			newFieldValues.put(COL_ITEM_CONTENT, itemContent);

			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, newFieldValues);
			if (newItemUri != null) {
				newItemID = Long.parseLong(newItemUri.getLastPathSegment());
			}

		}
		return newItemID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getItem(Context context, long itemID) {
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
		String[] projection = PROJECTION_ALL;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = SORT_ORDER_ITEM_TITLE;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ItemsTable", "Exception error in getItem:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getItem(Context context, String itemTitle) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_ITEM_TITLE + " = ?";
		String selectionArgs[] = new String[] { itemTitle };
		String sortOrder = SORT_ORDER_ITEM_TITLE;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ItemsTable", "Exception error in getItem:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static CursorLoader getAllItems(Context context, String sortOrder) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = null;
		String selectionArgs[] = null;

		CursorLoader cursorLoader = null;
		try {
			cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ItemsTable", "Exception error in getAllItems:");
			e.printStackTrace();
		}
		return cursorLoader;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateItemFieldValues(Context context, long itemID, ContentValues newFieldValues) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri itemUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
			String selection = null;
			String[] selectionArgs = null;
			numberOfUpdatedRecords = cr.update(itemUri, newFieldValues, selection, selectionArgs);
		}
		return numberOfUpdatedRecords;
	}

	public static void ToggleCheckBox(Context context, long itemID) {
		Cursor cursor = getItem(context, itemID);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(COL_ITEM_CHECKED);
			int checkValue = cursor.getInt(columnIndex);

			int newCheckValue = -1;
			if (checkValue == 0) {
				newCheckValue = 1;
			} else {
				newCheckValue = 0;
			}

			ContentValues newFieldValues = new ContentValues();
			newFieldValues.put(COL_ITEM_CHECKED, newCheckValue);
			UpdateItemFieldValues(context, itemID, newFieldValues);

			cursor.close();
		}
	}

	public static int UncheckAllCheckedItems(Context context) {
		int numberOfUpdatedRecords = -1;

		Uri uri = CONTENT_URI;
		String selection = COL_ITEM_CHECKED + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(1) };

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_CHECKED, 0);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int DeleteItem(Context context, long itemID) {
		int numberOfDeletedRecords = -1;
		if (itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri itemUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
			String where = null;
			String[] selectionArgs = null;
			cr.delete(itemUri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllCheckedItems(Context context) {
		int numberOfDeletedRecords = -1;

		Uri uri = CONTENT_URI;
		String where = COL_ITEM_CHECKED + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(1) };
		ContentResolver cr = context.getContentResolver();
		numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

		return numberOfDeletedRecords;
	}

}
