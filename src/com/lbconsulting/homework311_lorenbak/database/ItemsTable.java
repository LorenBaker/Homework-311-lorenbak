package com.lbconsulting.homework311_lorenbak.database;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.lbconsulting.alist_03.database.contentprovider.AListContentProvider;
import com.lbconsulting.alist_03.utilities.AListUtilities;
import com.lbconsulting.alist_03.utilities.MyLog;

public class ItemsTable {

	// Items data table
	// Version 1
	public static final String TABLE_ITEMS = "tblItems";
	public static final String COL_ITEM_ID = "_id";
	public static final String COL_ITEM_TITLE = "itemTitle";
	public static final String COL_ITEM_CONTENT = "itemContent";
	public static final String COL_ITEM_ICON = "itemIcon";
	public static final String COL_REFRESH_DATE_TIME = "refreshDateTime";

	public static final String[] PROJECTION_ALL = { COL_ITEM_ID, COL_ITEM_TITLE, COL_ITEM_ICON, COL_REFRESH_DATE_TIME};

	public static final String CONTENT_PATH = TABLE_ITEMS;s";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ TABLE_ITEMS;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ TABLE_ITEMS;
	public static final Uri CONTENT_URI = Uri.parse("content://" + HW311ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	public static final Uri CONTENT_URI_ITEMS_WITH_GROUPS = Uri.parse("content://" + HW311ContentProvider.AUTHORITY
			+ "/" + CONTENT_PATH_ITEMS_WITH_GROUPS);

	public static final Uri CONTENT_URI_ITEMS_WITH_LOCATIONS = Uri.parse("content://" + HW311ContentProvider.AUTHORITY
			+ "/" + CONTENT_PATH_ITEMS_WITH_LOCATIONS);

	public static final String SORT_ORDER_ITEM_NAME = COL_ITEM_NAME + " ASC";
	public static final String SORT_ORDER_SELECTED_AT_TOP = COL_SELECTED + " DESC, " + SORT_ORDER_ITEM_NAME;
	public static final String SORT_ORDER_SELECTED_AT_BOTTOM = COL_SELECTED + " ASC, " + SORT_ORDER_ITEM_NAME;
	public static final String SORT_ORDER_LAST_USED = COL_DATE_TIME_LAST_USED + " DESC, " + SORT_ORDER_ITEM_NAME;
	public static final String SORT_ORDER_MANUAL = COL_MANUAL_SORT_ORDER + " ASC";

	public static final String ITEM_MOVE_BROADCAST_KEY = "itemMoved";
	public static final String ITEM_CHANGED_BROADCAST_KEY = "itemChanged";

	// TODO: SORT by group name not id!
	// public static final String SORT_ORDER_BY_GROUP = COL_GROUP_ID + " ASC, "
	// + SORT_ORDER_ITEM_NAME;

	public static final int SELECTED_TRUE = 1;
	public static final int SELECTED_FALSE = 0;

	public static final int STRUCKOUT_TRUE = 1;
	public static final int STRUCKOUT_FALSE = 0;

	public static final int CHECKED_TRUE = 1;
	public static final int CHECKED_FALSE = 0;

	public static final int MANUAL_SORT_SWITCH_INVISIBLE = 0;
	public static final int MANUAL_SORT_SWITCH_VISIBLE = 1;
	public static final int MANUAL_SORT_SWITCH_ITEM_SWITCHED = 2;

	// private static final long milliSecondsPerDay = 1000;
	private static final long milliSecondsPerDay = 1000 * 60 * 60 * 24;

	// Database creation SQL statements
	private static final String DATATABLE_CREATE =
			"create table " + TABLE_ITEMS
					+ " ("
					+ COL_ITEM_ID + " integer primary key autoincrement, "
					+ COL_ITEM_NAME + " text collate nocase, "
					+ COL_ITEM_NOTE + " text collate nocase, "
					+ COL_LIST_ID + " integer not null references " + ListsTable.TABLE_LISTS + " (" + ListsTable.COL_LIST_ID + ") default 1, "
					+ COL_GROUP_ID + " integer not null references " + GroupsTable.TABLE_GROUPS + " (" + GroupsTable.COL_GROUP_ID + ") default 1, "
					+ COL_SELECTED + " integer default 0, "
					+ COL_STRUCK_OUT + " integer default 0, "
					+ COL_CHECKED + " integer default 0, "
					+ COL_MANUAL_SORT_ORDER + " integer default -1, "
					+ COL_MANUAL_SORT_SWITCH + " integer default 1, "
					+ COL_DATE_TIME_LAST_USED + " integer"
					+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("ItemsTable", "onCreate: " + TABLE_ITEMS + " created.");

		/*
		 * String insertProjection = "insert into " + TABLE_ITEMS + " (" +
		 * COL_ITEM_ID + ", " + COL_ITEM_NAME + ", " + COL_ITEM_NOTE + ", " +
		 * COL_LIST_ID + ", " + COL_SELECTED + ", " + COL_DATE_TIME_LAST_USED +
		 * ") VALUES ";
		 */

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_ITEMS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		int upgradeToVersion = oldVersion + 1;
		switch (upgradeToVersion) {
		// fall through each case to upgrade to the newVersion
		case 2:
		case 3:
		case 4:
			// No changes in TABLE_ITEMS
			break;

		default:
			// upgrade version not found!
			MyLog.e(TABLE_ITEMS, "Upgrade version " + newVersion + " not found!");
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
			onCreate(database);
			break;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method creates a new item in the provided list.
	 * 
	 * @param context
	 * @param listID
	 * @param itemName
	 * @return Returns the new item's ID.
	 */
	public static long CreateNewItem(Context context, long listID, String itemName) {
		long newItemID = -1;
		if (listID > 1) {
			itemName = itemName.trim();
			// verify that the item does not already exist in the table
			if (itemName != null && !itemName.isEmpty()) {
				@SuppressWarnings("resource")
				Cursor cursor = getItem(context, listID, itemName);
				if (cursor != null && cursor.getCount() > 0) {
					// the item exists in the table ... so return its id
					cursor.moveToFirst();
					newItemID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
					cursor.close();
				} else {
					// item does not exist in the table ... so add it
					ContentResolver cr = context.getContentResolver();
					Uri uri = CONTENT_URI;
					ContentValues values = new ContentValues();
					values.put(COL_ITEM_NAME, itemName);
					values.put(COL_LIST_ID, listID);
					// Note: Content Provider inserts the current date/time when
					// creating a new item
					try {
						Uri newListUri = cr.insert(uri, values);
						if (newListUri != null) {
							newItemID = Long.parseLong(newListUri.getLastPathSegment());
							values = new ContentValues();
							values.put(COL_MANUAL_SORT_ORDER, newItemID);
							UpdateItemFieldValues(context, newItemID, values);
						}
					} catch (Exception e) {
						MyLog.e("Exception error in CreateNewList. ", e.toString());
					}
				}
			}
		}
		return newItemID;
	}

	public static long CreateNewItem(Context context, long listID, String itemName, long groupID) {
		long newItemID = CreateNewItem(context, listID, itemName);
		if (newItemID > 0) {
			ContentValues values = new ContentValues();
			values.put(COL_GROUP_ID, groupID);
			UpdateItemFieldValues(context, newItemID, values);
		}
		return newItemID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Cursor getItem(Context context, long itemID) {
		Cursor cursor = null;
		if (itemID > 0) {
			Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
			String[] projection = PROJECTION_ALL;
			String selection = null;
			String selectionArgs[] = null;
			String sortOrder = null;
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error in ItemsTable: getItem. ", e.toString());
			}
		}
		return cursor;
	}

	public static Cursor getItem(Context context, long listID, String itemName) {
		Cursor cursor = null;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_LIST_ID + " = ? AND " + COL_ITEM_NAME + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), itemName };
			String sortOrder = null;
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error in ItemsTable: getItem. ", e.toString());
			}
		}
		return cursor;
	}

	public static long getListID(Context context, long itemID) {
		long listID = -1;
		Cursor cursor = getItem(context, itemID);
		if (cursor != null) {
			cursor.moveToFirst();
			listID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LIST_ID));
			cursor.close();
		}

		return listID;
	}

	/**
	 * This method gets all items in the provided list
	 * 
	 * @param context
	 * @param listID
	 * @param itemName
	 * @return Returns all items associated with the provided list ID
	 */
	public static CursorLoader getAllItemsInList(Context context, long listID, String sortOrder) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_LIST_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID) };
			try {
				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllItemsInList. ", e.toString());
			}
		}
		return cursorLoader;
	}

	public static CursorLoader getAllItemsInList(Context context, long listID, String selection, String sortOrder) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			if (selection != null) {
				selection = selection + " AND " + TABLE_ITEMS + "." + COL_LIST_ID + " = ?";
			} else {
				selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ?";
			}
			String selectionArgs[] = new String[] { String.valueOf(listID) };
			try {
				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllItemsInList. ", e.toString());
			}
		}
		return cursorLoader;
	}

	public static CursorLoader getAllItemsInListWithGroups(Context context, long listID, String selection) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			Uri uri = CONTENT_URI_ITEMS_WITH_GROUPS;
			String[] projection = ItemsTable.PROJECTION_WITH_GROUP_NAME;

			if (selection != null) {
				selection = selection + " AND " + TABLE_ITEMS + "." + COL_LIST_ID + " = ?";
			} else {
				selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ?";
			}
			String selectionArgs[] = new String[] { String.valueOf(listID) };
			String sortOrder = GroupsTable.SORT_ORDER_GROUP + ", " + ItemsTable.SORT_ORDER_ITEM_NAME;
			try {
				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllItemsInListWithGroups. ", e.toString());
			}
		}
		return cursorLoader;
	}

	public static CursorLoader getAllItemsInListWithLocations(Context context, long listID, long storeID) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			Uri uri = CONTENT_URI_ITEMS_WITH_LOCATIONS;
			String[] projection = ItemsTable.PROJECTION_WITH_LOCATION_NAME;
			String selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ? AND "
					+ BridgeTable.TABLE_BRIDGE + "." + BridgeTable.COL_STORE_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(storeID) };
			String sortOrder = LocationsTable.SORT_ORDER_LOCATION + ", " + ItemsTable.SORT_ORDER_ITEM_NAME;
			try {
				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllItemsInListWithLocations. ", e.toString());
			}
		}
		return cursorLoader;
	}

	/**
	 * This method gets all items in the provided list that are selected (True) or not selected (False)
	 * 
	 * @param context
	 * @param listID
	 * @param selected
	 * @return Returns all selected (or not selected) items in the list.
	 */
	public static CursorLoader getAllSelectedItemsInList(Context context, long listID, boolean selected, String sortOrder) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			int selectedValue = AListUtilities.boolToInt(selected);
			if (sortOrder == null) {
				sortOrder = SORT_ORDER_ITEM_NAME;
			}
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_LIST_ID + " = ? AND " + COL_SELECTED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(selectedValue) };
			/* ContentResolver cr = context.getContentResolver(); */
			try {

				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
				/*
				 * cursor = cr.query(uri, projection, selection, selectionArgs,
				 * sortOrder);
				 */
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllSelectedItemsInList. ", e.toString());
			}
		}
		return cursorLoader;
	}

	public static CursorLoader getAllSelectedItemsInListWithGroups(Context context, long listID, boolean selected) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			int selectedValue = AListUtilities.boolToInt(selected);
			Uri uri = CONTENT_URI_ITEMS_WITH_GROUPS;
			String[] projection = ItemsTable.PROJECTION_WITH_GROUP_NAME;
			String selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ? AND "
					+ TABLE_ITEMS + "." + COL_SELECTED + " = ?";
			// String selection = COL_LIST_ID + " = ? AND " + COL_SELECTED +
			// " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(selectedValue) };
			String sortOrder = GroupsTable.SORT_ORDER_GROUP + ", " + ItemsTable.SORT_ORDER_ITEM_NAME;
			try {
				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllSelectedItemsInListWithGroups. ", e.toString());
			}
		}
		return cursorLoader;
	}

	public static CursorLoader getAllSelectedItemsInListWithLocations(Context context, long listID, long storeID, boolean selected) {
		CursorLoader cursorLoader = null;
		if (listID > 1) {
			int selectedValue = AListUtilities.boolToInt(selected);
			Uri uri = CONTENT_URI_ITEMS_WITH_LOCATIONS;
			String[] projection = ItemsTable.PROJECTION_WITH_LOCATION_NAME;
			String selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ? AND "
					+ BridgeTable.TABLE_BRIDGE + "." + BridgeTable.COL_STORE_ID + " = ? AND "
					+ TABLE_ITEMS + "." + COL_SELECTED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(storeID),
					String.valueOf(selectedValue) };
			String sortOrder = LocationsTable.SORT_ORDER_LOCATION + ", " + ItemsTable.SORT_ORDER_ITEM_NAME;
			try {
				cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllSelectedItemsInListWithLocations. ", e.toString());
			}
		}
		return cursorLoader;
	}

	public static Cursor getAllSelectedItems(Context context, long listID, boolean selected, String sortOrder) {
		Cursor cursor = null;
		if (listID > 1) {
			int selectedValue = AListUtilities.boolToInt(selected);
			if (sortOrder == null) {
				sortOrder = SORT_ORDER_ITEM_NAME;
			}
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_LIST_ID + " = ? AND " + COL_SELECTED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(selectedValue) };
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllSelectedItems. ", e.toString());
			}
		}
		return cursor;
	}

	public static Cursor getAllSelectedItemsWithGroups(Context context, long listID, boolean selected) {
		Cursor cursor = null;
		if (listID > 1) {

			Uri uri = CONTENT_URI_ITEMS_WITH_GROUPS;
			String[] projection = ItemsTable.PROJECTION_WITH_GROUP_NAME;
			int selectedValue = AListUtilities.boolToInt(selected);
			String selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ? AND "
					+ TABLE_ITEMS + "." + COL_SELECTED + " = ?";

			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(selectedValue) };
			String sortOrder = GroupsTable.SORT_ORDER_GROUP + ", " + ItemsTable.SORT_ORDER_ITEM_NAME;
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllSelectedItemsWithGroups. ", e.toString());
			}
		}
		return cursor;
	}

	public static Cursor getAllSelectedItemsWithLocations(Context context, long listID, long storeID, boolean selected) {
		Cursor cursor = null;
		if (listID > 1) {
			int selectedValue = AListUtilities.boolToInt(selected);
			Uri uri = CONTENT_URI_ITEMS_WITH_LOCATIONS;
			String[] projection = ItemsTable.PROJECTION_WITH_LOCATION_NAME;
			String selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ? AND "
					+ BridgeTable.TABLE_BRIDGE + "." + BridgeTable.COL_STORE_ID + " = ? AND "
					+ TABLE_ITEMS + "." + COL_SELECTED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(storeID), String.valueOf(selectedValue) };
			String sortOrder = LocationsTable.SORT_ORDER_LOCATION + ", " + ItemsTable.SORT_ORDER_ITEM_NAME;
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllSelectedItemsWithLocations. ", e.toString());
			}
		}
		return cursor;
	}

	/**
	 * This method gets all items in the provided list that are struck out (True) or not struck out (False)
	 * 
	 * @param context
	 * @param listID
	 * @param struckOut
	 * @param sortOrder
	 * @return
	 */
	private static Cursor getItems(Context context, long listID) {
		Cursor cursor = null;
		if (listID > 1) {

			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			/*
			 * String selection = COL_LIST_ID + " = ? AND " + COL_STRUCK_OUT +
			 * " = ?"; String selectionArgs[] = new String[] {
			 * String.valueOf(listID), String.valueOf(struckOutValue) };
			 */

			String selection = COL_LIST_ID + " = ? ";
			String selectionArgs[] = { String.valueOf(listID) };

			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, SORT_ORDER_ITEM_NAME);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getItems. ", e.toString());
			}
		}
		return cursor;
	}

	public static Cursor getAllItemsWithGroups(Context context, long listID) {
		Cursor cursor = null;
		if (listID > 1) {
			Uri uri = CONTENT_URI_ITEMS_WITH_GROUPS;
			String[] projection = ItemsTable.PROJECTION_WITH_ITEM_NAME_AND_GROUP_NAME;
			String selection = TABLE_ITEMS + "." + COL_LIST_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID) };
			String sortOrder = ItemsTable.SORT_ORDER_ITEM_NAME + ", " + GroupsTable.SORT_ORDER_GROUP;
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllItemsWithGroups. ", e.toString());
			}
		}
		return cursor;
	}

	public static Cursor getAllItems(Context context) {
		Cursor cursor = null;

		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("Exception error  in ItemsTable: getAllItems. ", e.toString());
		}
		return cursor;
	}

	public static Cursor getAllCheckedItemsInList(Context context, long listID, boolean checked) {
		Cursor cursor = null;
		int checkedValue = AListUtilities.boolToInt(checked);
		if (listID > 1) {

			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_LIST_ID + " = ? AND " + COL_CHECKED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(checkedValue) };
			String sortOrder = null;
			ContentResolver cr = context.getContentResolver();
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("Exception error  in ItemsTable: getAllCheckedItemsInList. ", e.toString());
			}
		}
		return cursor;
	}

	public static int getNumberOfCheckedItmes(Context context, long listID) {
		int numberOfCheckedItmes = -1;
		Cursor cursor = getAllCheckedItemsInList(context, listID, true);
		if (cursor != null) {
			numberOfCheckedItmes = cursor.getCount();
			cursor.close();
		}
		return numberOfCheckedItmes;
	}

	/**
	 * This method gets all items in the provided group.
	 * 
	 * @param context
	 * @param groupID
	 * @return
	 */
	/*
	 * public static Cursor getAllItemsInGroup(Context context, long groupID,
	 * String sortOrder) { Cursor cursor = null; if (groupID > 0) { if
	 * (sortOrder == null) { sortOrder = SORT_ORDER_ITEM_NAME; } Uri uri =
	 * CONTENT_URI; String[] projection = PROJECTION_ALL; String selection =
	 * COL_GROUP_ID + " = ?"; String[] selectionArgs = { String.valueOf(groupID)
	 * }; ContentResolver cr = context.getContentResolver(); try { cursor =
	 * cr.query(uri, projection, selection, selectionArgs, sortOrder); } catch
	 * (Exception e) {
	 * MyLog.e("Exception error in ItemsTable: getAllItemsInGroup. ",
	 * e.toString()); } } return cursor; }
	 */

	/**
	 * This method gets all items in the provided group that are selected (True) or not selected (False).
	 * 
	 * @param context
	 * @param groupID
	 * @param selected
	 * @return
	 */
	/*	public static Cursor getAllSelectedItemsInGroup(Context context, long groupID, boolean selected, String sortOrder) {
			Cursor cursor = null;
			if (groupID > 0) {
				int selectedValue = AListUtilities.boolToInt(selected);
				if (sortOrder == null) {
					sortOrder = SORT_ORDER_ITEM_NAME;
				}
				Uri uri = CONTENT_URI;
				String[] projection = PROJECTION_ALL;
				String selection = COL_GROUP_ID + " = ? AND " + COL_SELECTED + " = ?";
				String[] selectionArgs = { String.valueOf(groupID), String.valueOf(selectedValue) };
				ContentResolver cr = context.getContentResolver();
				try {
					cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
				} catch (Exception e) {
					MyLog.e("Exception error in ItemsTable: getAllSelectedItemsInGroup. ", e.toString());
				}
			}
			return cursor;
		}*/

	public static boolean isItemSwitched(Context context, long itemID) {
		boolean result = false;
		Cursor itemCursor = getItem(context, itemID);
		if (itemCursor != null) {
			itemCursor.moveToFirst();
			int switchValue = itemCursor.getInt(itemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_SWITCH));
			if (switchValue > 1) {
				result = true;
			}
			itemCursor.close();
		}
		return result;
	}

	/*	public static boolean isItemVisible(Context context, long itemID) {
			boolean result = false;
			Cursor itemCursor = getItem(context, itemID);
			if (itemCursor != null) {
				itemCursor.moveToFirst();
				int switchValue = itemCursor.getInt(itemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_SWITCH));
				if (switchValue > 0) {
					result = true;
				}
				itemCursor.close();
			}
			return result;
		}*/

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

	public static void setItemInvisible(Context context, long itemID) {
		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_INVISIBLE);
		UpdateItemFieldValues(context, itemID, newFieldValues);
	}

	public static void setItemVisible(Context context, long itemID) {
		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_VISIBLE);
		UpdateItemFieldValues(context, itemID, newFieldValues);
	}

	/*
	 * public static int UpdateItemName(Context context, long itemID, String
	 * newItemName) { int numberOfUpdatedRecords = -1; if (itemID > 0) {
	 * newItemName = newItemName.trim(); try { ContentResolver cr =
	 * context.getContentResolver(); Uri uri = CONTENT_URI; String where =
	 * COL_ITEM_ID + " = ?"; String[] whereArgs = { String.valueOf(itemID) };
	 * ContentValues values = new ContentValues(); values.put(COL_ITEM_NAME,
	 * newItemName); numberOfUpdatedRecords = cr.update(uri, values, where,
	 * whereArgs); } catch (Exception e) {
	 * MyLog.e("Exception error in UpdateItemName. ", e.toString()); } } return
	 * numberOfUpdatedRecords; }
	 * 
	 * public static int UpdateItemNote(Context context, long itemID, String
	 * newItemNote) { int numberOfUpdatedRecords = -1; if (itemID > 0) {
	 * newItemNote = newItemNote.trim(); try { ContentResolver cr =
	 * context.getContentResolver(); Uri uri = CONTENT_URI; String where =
	 * COL_ITEM_ID + " = ?"; String[] whereArgs = { String.valueOf(itemID) };
	 * ContentValues values = new ContentValues(); values.put(COL_ITEM_NOTE,
	 * newItemNote); numberOfUpdatedRecords = cr.update(uri, values, where,
	 * whereArgs); } catch (Exception e) {
	 * MyLog.e("Exception error in UpdateItemNote. ", e.toString()); } } return
	 * numberOfUpdatedRecords; }
	 */

	/*
	 * public static int ChangeGroupID(Context context, long itemID, long
	 * newGroupID) { int numberOfUpdatedRecords = -1; if (itemID > 0 &&
	 * newGroupID > 0) { try { ContentResolver cr =
	 * context.getContentResolver(); Uri uri = CONTENT_URI; String where =
	 * COL_ITEM_ID + " = ?"; String[] whereArgs = { String.valueOf(itemID) };
	 * ContentValues values = new ContentValues(); values.put(COL_GROUP_ID,
	 * newGroupID); numberOfUpdatedRecords = cr.update(uri, values, where,
	 * whereArgs); } catch (Exception e) {
	 * MyLog.e("Exception error in ChangeGroupID. ", e.toString()); } } return
	 * numberOfUpdatedRecords; }
	 */

	public static int UpdateItem(Context context, long itemID, String itemName, String itemNote, long itemGroupID) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			itemName = itemName.trim();
			itemNote = itemNote.trim();
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_ITEM_ID + " = ?";
				String[] whereArgs = { String.valueOf(itemID) };
				ContentValues values = new ContentValues();
				values.put(COL_ITEM_NAME, itemName);
				values.put(COL_ITEM_NOTE, itemNote);
				values.put(COL_GROUP_ID, itemGroupID);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in UpdateItem. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;

	}

	public static int SelectItem(Context context, long itemID, boolean selected) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_ITEM_ID + " = ?";
				String[] whereArgs = { String.valueOf(itemID) };

				ContentValues values = new ContentValues();
				int selectedValue = AListUtilities.boolToInt(selected);
				if (selected) {
					Calendar now = Calendar.getInstance();
					values.put(COL_DATE_TIME_LAST_USED, now.getTimeInMillis());
				}
				values.put(COL_SELECTED, selectedValue);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in SelectItem. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int DeselectAllItemsInList(Context context, long listID, boolean deleteNoteUponDeslectingItem) {
		int numberOfUpdatedRecords = -1;
		if (listID > 1) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_LIST_ID + " = ? AND " + COL_SELECTED + " = ?";
				String[] whereArgs = { String.valueOf(listID), String.valueOf(SELECTED_TRUE) };

				ContentValues values = new ContentValues();
				values.put(COL_STRUCK_OUT, STRUCKOUT_FALSE);
				values.put(COL_SELECTED, SELECTED_FALSE);
				if (deleteNoteUponDeslectingItem) {
					values.put(COL_ITEM_NOTE, "");
				}
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in DeselectAllItemsInList. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int DeselectAllItemsInGroup(Context context, long groupID) {
		int numberOfUpdatedRecords = -1;
		if (groupID > 0) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_GROUP_ID + " = ? AND " + COL_SELECTED + " = ?";
				String[] whereArgs = { String.valueOf(groupID), String.valueOf(SELECTED_TRUE) };

				ContentValues values = new ContentValues();
				values.put(COL_SELECTED, SELECTED_FALSE);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in DeselectAllItemsInGroup. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static void ToggleStrikeOut(Context context, long itemID) {
		Cursor cursor = getItem(context, itemID);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(COL_STRUCK_OUT);
			int strikeOutIntValue = cursor.getInt(columnIndex);
			boolean strikeOutValue = AListUtilities.intToBoolean(strikeOutIntValue);
			cursor.close();
			StrikeItem(context, itemID, !strikeOutValue);
		}
	}

	public static void ToggleSelection(Context context, long itemID) {
		Cursor cursor = getItem(context, itemID);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(COL_SELECTED);
			int selectedIntValue = cursor.getInt(columnIndex);
			boolean selectedValue = AListUtilities.intToBoolean(selectedIntValue);
			cursor.close();
			SelectItem(context, itemID, !selectedValue);
		}

	}

	public static void ToggleCheckBox(Context context, long itemID) {
		Cursor cursor = getItem(context, itemID);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(COL_CHECKED);
			int checkIntValue = cursor.getInt(columnIndex);
			boolean checkValue = AListUtilities.intToBoolean(checkIntValue);
			cursor.close();
			CheckItem(context, itemID, !checkValue);
		}
	}

	public static int StrikeItem(Context context, long itemID, boolean struckOut) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_ITEM_ID + " = ?";
				String[] whereArgs = { String.valueOf(itemID) };

				ContentValues values = new ContentValues();
				int struckOutValue = AListUtilities.boolToInt(struckOut);
				values.put(COL_STRUCK_OUT, struckOutValue);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in StrikeItem. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int UnStrikeAndDeselectAllStruckOutItems(Context context, long listID,
			boolean deleteNoteUponDeslectingItem) {
		int numberOfUpdatedRecords = -1;
		if (listID > 1) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_LIST_ID + " = ? AND " + COL_STRUCK_OUT + " = ?";
				String[] whereArgs = { String.valueOf(listID), String.valueOf(SELECTED_TRUE) };

				ContentValues values = new ContentValues();
				values.put(COL_STRUCK_OUT, STRUCKOUT_FALSE);
				values.put(COL_SELECTED, SELECTED_FALSE);
				if (deleteNoteUponDeslectingItem) {
					values.put(COL_ITEM_NOTE, "");
				}
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in UnStrikeAllItemsInList. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int UnStrikeAllItemsInGroup(Context context, long groupID) {
		int numberOfUpdatedRecords = -1;
		if (groupID > 0) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_GROUP_ID + " = ? AND " + COL_STRUCK_OUT + " = ?";
				String[] whereArgs = { String.valueOf(groupID), String.valueOf(SELECTED_TRUE) };

				ContentValues values = new ContentValues();
				values.put(COL_STRUCK_OUT, SELECTED_FALSE);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in UnStrikeAllItemsInGroup. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int CheckItem(Context context, long itemID, boolean checked) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_ITEM_ID + " = ?";
				String[] whereArgs = { String.valueOf(itemID) };

				ContentValues values = new ContentValues();
				int checkedValue = AListUtilities.boolToInt(checked);
				values.put(COL_CHECKED, checkedValue);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in ItemsTable: CheckItem. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int UnCheckAllItemsInList(Context context, long listID) {
		int numberOfUpdatedRecords = -1;
		if (listID > 1) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_LIST_ID + " = ? AND " + COL_CHECKED + " = ?";
				String[] whereArgs = { String.valueOf(listID), String.valueOf(CHECKED_TRUE) };

				ContentValues values = new ContentValues();
				values.put(COL_CHECKED, CHECKED_FALSE);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in UnCheckAllItemsInList. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int ApplyGroupToCheckedItems(Context context, long listID, long groupID) {
		int numberOfUpdatedRecords = -1;
		if (listID > 1) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_LIST_ID + " = ? AND " + COL_CHECKED + " = ?";
				String[] whereArgs = { String.valueOf(listID), String.valueOf(CHECKED_TRUE) };

				ContentValues values = new ContentValues();
				values.put(COL_GROUP_ID, groupID);
				values.put(COL_CHECKED, CHECKED_FALSE);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in ApplyGroupToCheckedItems. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int CheckItemsUnused(Context context, long listID, long numberOfDays) {
		int numberOfCheckedItems = -1;
		if (listID > 1) {

			long numberOfMilliSeconds = numberOfDays * milliSecondsPerDay;
			Calendar now = Calendar.getInstance();
			long dateTimeCutOff = now.getTimeInMillis() - numberOfMilliSeconds;

			ContentResolver cr = context.getContentResolver();
			Uri itemUri = CONTENT_URI;
			String selection = COL_DATE_TIME_LAST_USED + " < ?";
			String[] selectionArgs = { String.valueOf(dateTimeCutOff) };

			ContentValues values = new ContentValues();
			values.put(COL_CHECKED, CHECKED_TRUE);

			numberOfCheckedItems = cr.update(itemUri, values, selection, selectionArgs);
		}
		return numberOfCheckedItems;
	}

	/*
	 * public static void TrialUsedTimes(Context context, long listID) {
	 * Calendar now = Calendar.getInstance(); long minus91days =
	 * now.getTimeInMillis() - (91 * milliSecondsPerDay); long minus181days =
	 * now.getTimeInMillis() - (181 * milliSecondsPerDay); long minus366days =
	 * now.getTimeInMillis() - (366 * milliSecondsPerDay); long minus91days =
	 * Long.valueOf(123456); long minus181days = Long.valueOf(234567); long
	 * minus366days = Long.valueOf(345678);
	 * 
	 * //Cursor cursor = getItems(context, listID);
	 * 
	 * //if (cursor != null) { //cursor.moveToPosition(-1);
	 * 
	 * ContentValues values = new ContentValues();
	 * values.put(COL_DATE_TIME_LAST_USED, minus366days);
	 * UpdateItemFieldValues(context, 1, values);
	 * 
	 * values = new ContentValues(); values.put(COL_DATE_TIME_LAST_USED,
	 * minus366days); UpdateItemFieldValues(context, 2, values); values = new
	 * ContentValues(); values.put(COL_DATE_TIME_LAST_USED, minus366days);
	 * UpdateItemFieldValues(context, 3, values); values = new ContentValues();
	 * values.put(COL_DATE_TIME_LAST_USED, minus366days);
	 * UpdateItemFieldValues(context, 4, values);
	 * 
	 * values = new ContentValues(); values.put(COL_DATE_TIME_LAST_USED,
	 * minus181days); UpdateItemFieldValues(context, 5, values); values = new
	 * ContentValues(); values.put(COL_DATE_TIME_LAST_USED, minus181days);
	 * UpdateItemFieldValues(context, 6, values); values = new ContentValues();
	 * values.put(COL_DATE_TIME_LAST_USED, minus181days);
	 * UpdateItemFieldValues(context, 7, values); values = new ContentValues();
	 * values.put(COL_DATE_TIME_LAST_USED, minus181days);
	 * UpdateItemFieldValues(context, 8, values);
	 * 
	 * values = new ContentValues(); values.put(COL_DATE_TIME_LAST_USED,
	 * minus91days); UpdateItemFieldValues(context, 9, values); values = new
	 * ContentValues(); values.put(COL_DATE_TIME_LAST_USED, minus91days);
	 * UpdateItemFieldValues(context, 10, values); values = new ContentValues();
	 * values.put(COL_DATE_TIME_LAST_USED, minus91days);
	 * UpdateItemFieldValues(context, 11, values); values = new ContentValues();
	 * values.put(COL_DATE_TIME_LAST_USED, minus91days);
	 * UpdateItemFieldValues(context, 12, values);
	 * 
	 * for (int i = 0; i < 4; i++) { cursor.moveToNext(); long itemID =
	 * cursor.getLong(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
	 * UpdateItemFieldValues(context, itemID, values); }
	 * 
	 * values = new ContentValues(); values.put(COL_DATE_TIME_LAST_USED,
	 * minus181days); for (int i = 0; i < 4; i++) { cursor.moveToNext(); long
	 * itemID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
	 * UpdateItemFieldValues(context, itemID, values); }
	 * 
	 * values = new ContentValues(); values.put(COL_DATE_TIME_LAST_USED,
	 * minus91days); for (int i = 0; i < 4; i++) { cursor.moveToNext(); long
	 * itemID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
	 * UpdateItemFieldValues(context, itemID, values); } //cursor.close(); //}
	 * 
	 * }
	 */

	public static void SwapManualSortOrder(Context context, long mobileItemID, long switchItemID, long previousSwitchItemID) {
		int numberOfUpdatedRecords = -1;
		if (mobileItemID > 0 && switchItemID > 0) {
			try {
				Cursor mobileItemCursor = getItem(context, mobileItemID);
				Cursor switchItemCursor = getItem(context, switchItemID);

				mobileItemCursor.moveToFirst();
				switchItemCursor.moveToFirst();

				int mobileItemManualSortOrder = mobileItemCursor.getInt(mobileItemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_ORDER));
				int switchItemManualSortOrder = switchItemCursor.getInt(switchItemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_ORDER));

				// TODO remove strings names
				String mobileItemName = mobileItemCursor.getString(mobileItemCursor.getColumnIndexOrThrow(COL_ITEM_NAME));
				String switchItemName = switchItemCursor.getString(switchItemCursor.getColumnIndexOrThrow(COL_ITEM_NAME));

				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_ITEM_ID + " = ?";
				String[] whereArgsMobileItemCursor = { String.valueOf(mobileItemID) };
				ContentValues values = new ContentValues();
				values.put(COL_MANUAL_SORT_ORDER, switchItemManualSortOrder);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgsMobileItemCursor);

				String[] whereArgsSwitchItemCursor = { String.valueOf(switchItemID) };
				values = new ContentValues();
				values.put(COL_MANUAL_SORT_ORDER, mobileItemManualSortOrder);
				values.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_ITEM_SWITCHED);
				numberOfUpdatedRecords += cr.update(uri, values, where, whereArgsSwitchItemCursor);

				if (numberOfUpdatedRecords != 2) {
					MyLog.e("ItemsTable", "SwapManualSortOrder: Incorrect number of records updated.");
				}

				if (previousSwitchItemID > 0) {
					String[] whereArgsPreviousSwitchedItem = { String.valueOf(previousSwitchItemID) };
					values = new ContentValues();
					values.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_VISIBLE);
					numberOfUpdatedRecords += cr.update(uri, values, where, whereArgsPreviousSwitchedItem);
				}

				mobileItemCursor.close();
				switchItemCursor.close();
				MyLog.i("ItemsTable",
						"SwapManualSortOrder: mobileItem:"
								+ mobileItemName + "(" + mobileItemManualSortOrder + ")"
								+ " MANUAL_SORT_ORDER swapped with switchItem:"
								+ switchItemName + "(" + switchItemManualSortOrder + ")");

			} catch (Exception e) {
				MyLog.e("Exception error in ItemsTable: CheckItem. ", e.toString());
			}
		}
	}

	public static int MoveItem(Context context, long itemID, long newListID) {
		int numberOfUpdatedRecords = 0;
		String existingItemName;
		Cursor existingItemCursor = null;
		Cursor newListCursor = null;

		if (itemID > 0 && newListID > 1) {
			existingItemCursor = getItem(context, itemID);
			if (existingItemCursor != null) {
				if (existingItemCursor.getCount() > 0) {

					existingItemCursor.moveToFirst();
					existingItemName = existingItemCursor
							.getString(existingItemCursor.getColumnIndexOrThrow(COL_ITEM_NAME));

					// verify that the item does not already exist in the new
					// list
					newListCursor = getItem(context, newListID, existingItemName);
					if (newListCursor != null) {
						if (newListCursor.getCount() == 0) {
							// the item does not exists in the table ... so move
							// it
							// by changing the listID
							numberOfUpdatedRecords = ChangeListID(context, itemID, newListID);

						} else {
							// the item exists in the new list ... so move it
							// by deleting it from the new list and changing the
							// existing item's listID
							long newListItemID = newListCursor
									.getLong(newListCursor.getColumnIndexOrThrow(COL_ITEM_ID));
							DeleteItem(context, newListItemID);
							numberOfUpdatedRecords = ChangeListID(context, itemID, newListID);
						}
					}
				}
			}
		}

		if (existingItemCursor != null) {
			existingItemCursor.close();
		}
		if (newListCursor != null) {
			newListCursor.close();
		}

		return numberOfUpdatedRecords;
	}

	private static int ChangeListID(Context context, long itemID, long newListID) {
		int numberOfUpdatedRecords = -1;
		ContentResolver cr = context.getContentResolver();
		Uri uri = CONTENT_URI;
		String where = COL_ITEM_ID + " = ?";
		String[] whereArgs = { String.valueOf(itemID) };

		ContentValues values = new ContentValues();
		values.put(COL_LIST_ID, newListID);
		values.put(COL_GROUP_ID, 1); // default group
		values.put(COL_CHECKED, CHECKED_FALSE); // clear the checked flag
		numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);

		return numberOfUpdatedRecords;
	}

	public static int MoveAllCheckedItemsInList(Context context, long listID, long newListID) {
		int numberOfUpdatedRecords = -1;
		Cursor checkedItemsCursor = null;
		if (listID > 1 && newListID > 1) {

			checkedItemsCursor = getAllCheckedItemsInList(context, listID, true);
			if (checkedItemsCursor != null && checkedItemsCursor.getCount() > 0) {
				numberOfUpdatedRecords = 0;
				int numberOfItemsMoved = 0;
				long itemID;
				checkedItemsCursor.moveToPosition(-1);
				while (checkedItemsCursor.moveToNext()) {
					itemID = checkedItemsCursor.getLong(checkedItemsCursor.getColumnIndexOrThrow(COL_ITEM_ID));
					numberOfItemsMoved = MoveItem(context, itemID, newListID);
					numberOfUpdatedRecords += numberOfItemsMoved;
				}
				if (numberOfUpdatedRecords != checkedItemsCursor.getCount()) {
					StringBuilder sb = new StringBuilder();
					sb.append("Error in MoveAllCheckedItemsInList: ");
					sb.append(System.getProperty("line.separator"));
					sb.append("Number of items moved does not match the number of checked items in the list!");
					sb.append(System.getProperty("line.separator"));
					sb.append("Number of items moved = " + numberOfItemsMoved);
					sb.append(System.getProperty("line.separator"));
					sb.append("Number of checked items in the list = " + checkedItemsCursor.getCount());
					MyLog.e("ItemsTable", sb.toString());
				}
			}
		}
		if (checkedItemsCursor != null) {
			checkedItemsCursor.close();
		}
		return numberOfUpdatedRecords;
	}

	public static int setManualSortOrder(Context context, long itemID, int manualSortOrder) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri uri = CONTENT_URI;
				String where = COL_ITEM_ID + " = ?";
				String[] whereArgs = { String.valueOf(itemID) };

				ContentValues values = new ContentValues();
				values.put(COL_MANUAL_SORT_ORDER, manualSortOrder);
				numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
			} catch (Exception e) {
				MyLog.e("Exception error in setManualSortOrder. ", e.toString());
			}
		}
		return numberOfUpdatedRecords;
	}

	public static int getManualSortOrder(Context context, long itemID) {
		int manualSortOrder = -1;
		if (itemID > 0) {
			Cursor cursor = getItem(context, itemID);
			if (cursor != null) {
				cursor.moveToFirst();
				manualSortOrder = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANUAL_SORT_ORDER));
				cursor.close();
			}
		}
		return manualSortOrder;
	}

	public static int ResetGroupID(Context context, long groupID) {
		int numberOfUpdatedRecords = -1;
		if (groupID > 1) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = CONTENT_URI;
			String where = COL_GROUP_ID + " = ?";
			String[] whereArgs = { String.valueOf(groupID) };

			ContentValues values = new ContentValues();
			values.put(COL_GROUP_ID, 1); // groupID = 1 is the default groupID
			numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
		}
		return numberOfUpdatedRecords;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int DeleteItem(Context context, long itemID) {
		int numberOfDeletedRecords = -1;
		if (itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
			String where = null;
			String[] selectionArgs = null;
			cr.delete(uri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllItemsInList(Context context, long listID) {
		int numberOfDeletedRecords = -1;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String where = COL_LIST_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID) };
			ContentResolver cr = context.getContentResolver();
			numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllSelectedItemsInList(Context context, long listID) {
		int numberOfDeletedRecords = -1;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String where = COL_LIST_ID + " = ? AND " + COL_SELECTED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(SELECTED_TRUE) };
			ContentResolver cr = context.getContentResolver();
			numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllStruckOutItemsInList(Context context, long listID) {
		int numberOfDeletedRecords = -1;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String where = COL_LIST_ID + " = ? AND " + COL_STRUCK_OUT + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(STRUCKOUT_TRUE) };
			ContentResolver cr = context.getContentResolver();
			numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllCheckedItemsInList(Context context, long listID) {
		int numberOfDeletedRecords = -1;
		if (listID > 1) {
			Uri uri = CONTENT_URI;
			String where = COL_LIST_ID + " = ? AND " + COL_CHECKED + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(listID), String.valueOf(CHECKED_TRUE) };
			ContentResolver cr = context.getContentResolver();
			numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

}
