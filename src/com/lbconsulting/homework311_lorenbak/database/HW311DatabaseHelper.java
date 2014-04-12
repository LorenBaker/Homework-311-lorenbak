package com.lbconsulting.homework311_lorenbak.database;

import com.lbconsulting.homework311_lorenbak.MyLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HW311DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "HW311.db";
	private static final int DATABASE_VERSION = 1;

	private static SQLiteDatabase dBase;

	public HW311DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		HW311DatabaseHelper.dBase = database;

		MyLog.i("HW311DatabaseHelper", "onCreate");
		ArticlesTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.i("HW311DatabaseHelper", "onUpgrade");
		ArticlesTable.onUpgrade(database, oldVersion, newVersion);
	}

	public static SQLiteDatabase getDatabase() {
		return dBase;
	}

}
