package com.lbconsulting.homework311_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.lbconsulting.homework311_lorenbak.MyLog;

public class ArticlesTable {

	// Items data table
	// Version 1
	public static final String TABLE_ARTICLES = "tblArticles";
	public static final String COL_ARTICLE_ID = "_id";
	public static final String COL_ARTICLE_TITLE = "articleTitle";
	public static final String COL_FIRST_LETTER_IN_TITLE = "firstLetterInTitle";
	public static final String COL_ARTICLE_CONTENT = "articleContent";
	public static final String COL_ARTICLE_ICON = "articleIcon";
	public static final String COL_ARTICLE_DATE = "articleDateTime";
	public static final String COL_ARTICLE_SELECTED = "articleSelected";
	public static final String COL_ARTICLE_READ = "articleRead";

	public static final String[] PROJECTION_ALL = { COL_ARTICLE_ID, COL_ARTICLE_TITLE, COL_FIRST_LETTER_IN_TITLE,
			COL_ARTICLE_CONTENT, COL_ARTICLE_ICON, COL_ARTICLE_DATE, COL_ARTICLE_SELECTED, COL_ARTICLE_READ };

	public static final String CONTENT_PATH = "articles";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + HW311ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	public static final String SORT_ORDER_ARTICLE_TITLE = COL_ARTICLE_TITLE + " ASC";

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_ARTICLES
			+ " ("
			+ COL_ARTICLE_ID + " integer primary key autoincrement, "
			+ COL_ARTICLE_TITLE + " text collate nocase, "
			+ COL_FIRST_LETTER_IN_TITLE + " text collate nocase, "
			+ COL_ARTICLE_CONTENT + " text collate nocase, "
			+ COL_ARTICLE_ICON + " text collate nocase, "
			+ COL_ARTICLE_DATE + " integer, "
			+ COL_ARTICLE_SELECTED + " integer default 0, " // 0 = article not Selected; 1 = article Selected
			+ COL_ARTICLE_READ + " integer default 0 " // 0 = article not Read; 1 = article Read
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("ArticlesTable", "onCreate: " + TABLE_ARTICLES + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_ARTICLES, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateArticle(Context context, String articleTitle, String articleContent) {
		// NOTE: COL_REFRESH_DATE_TIME is updated via the content provider
		long newItemID = -1;
		if (!articleTitle.isEmpty()) {
			// determine if the title is already in the database
			@SuppressWarnings("resource")
			Cursor articleCursor = getItem(context, articleTitle);
			if (articleCursor != null) {
				if (articleCursor.getCount() > 0) {
					// the article exists in the database
					// replace the article's content and update the refresh date/time
					articleCursor.moveToFirst();
					newItemID = articleCursor.getLong(articleCursor.getColumnIndexOrThrow(COL_ARTICLE_ID));
					ContentValues newFieldValues = new ContentValues();
					newFieldValues.put(COL_ARTICLE_CONTENT, articleContent);
					UpdateArticleFieldValues(context, newItemID, newFieldValues);
					articleCursor.close();
					return newItemID;
				}
			}
			// the article does not exist in the database ... so create it
			String firstLetterInTitle = articleTitle.substring(0, 1).toUpperCase();

			ContentValues newFieldValues = new ContentValues();
			newFieldValues.put(COL_ARTICLE_TITLE, articleTitle);
			newFieldValues.put(COL_FIRST_LETTER_IN_TITLE, firstLetterInTitle);
			newFieldValues.put(COL_ARTICLE_CONTENT, articleContent);

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

	public static Cursor getArticle(Context context, long articleID) {
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(articleID));
		String[] projection = PROJECTION_ALL;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = SORT_ORDER_ARTICLE_TITLE;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ArticlesTable", "Exception error in getItem:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getItem(Context context, String articleTitle) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_ARTICLE_TITLE + " = ?";
		String selectionArgs[] = new String[] { articleTitle };
		String sortOrder = SORT_ORDER_ARTICLE_TITLE;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ArticlesTable", "Exception error in getItem:");
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
			MyLog.e("ArticlesTable", "Exception error in getAllItems:");
			e.printStackTrace();
		}
		return cursorLoader;
	}

	public static boolean isArticleSelected(Context context, long articleID) {
		boolean result = false;
		Cursor cursor = getArticle(context, articleID);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int selectedValue = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ARTICLE_SELECTED));
			if (selectedValue == 1) {
				result = true;
			}
		}

		if (cursor != null) {
			cursor.close();
		}
		return result;
	}

	public static boolean isArticleRead(Context context, long articleID) {
		boolean result = false;
		Cursor cursor = getArticle(context, articleID);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int selectedValue = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ARTICLE_READ));
			if (selectedValue == 1) {
				result = true;
			}
		}

		if (cursor != null) {
			cursor.close();
		}
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateArticleFieldValues(Context context, long articleID, ContentValues newFieldValues) {
		int numberOfUpdatedRecords = -1;
		if (articleID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri articleUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(articleID));
			String selection = null;
			String[] selectionArgs = null;
			numberOfUpdatedRecords = cr.update(articleUri, newFieldValues, selection, selectionArgs);
		}
		return numberOfUpdatedRecords;
	}

	public static void setArticleSelection(Context context, long articleID, boolean articleSelected) {
		Cursor cursor = getArticle(context, articleID);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(COL_ARTICLE_SELECTED);

			int selectedValue = 0; // not selected
			if (articleSelected) {
				selectedValue = 1; // selected
			}

			ContentValues newFieldValues = new ContentValues();
			newFieldValues.put(COL_ARTICLE_SELECTED, selectedValue);
			UpdateArticleFieldValues(context, articleID, newFieldValues);

			cursor.close();
		}
	}

	public static int DeselectAllSelectedArticles(Context context) {
		int numberOfUpdatedRecords = -1;

		Uri uri = CONTENT_URI;
		String selection = COL_ARTICLE_SELECTED + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(1) };

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ARTICLE_SELECTED, 0);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	public static int setArticleAsRead(Context context, long articleID) {
		int numberOfUpdatedRecords = -1;
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(articleID));
		String selection = null;
		String[] selectionArgs = null;

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ARTICLE_READ, 1);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int DeleteArticle(Context context, long articleID) {
		int numberOfDeletedRecords = -1;
		if (articleID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri articleUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(articleID));
			String where = null;
			String[] selectionArgs = null;
			cr.delete(articleUri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllSelectedArticles(Context context) {
		int numberOfDeletedRecords = -1;

		Uri uri = CONTENT_URI;
		String where = COL_ARTICLE_SELECTED + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(1) };
		ContentResolver cr = context.getContentResolver();
		numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

		return numberOfDeletedRecords;
	}

}
