package com.babel.kliky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
	private final static String LOG_TAG = DatabaseHelper.class.getSimpleName();

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "KlikyDb";
	private static final String DATABASE_TABLE_NAME = "kliky";
	public static final String ID = "ID";
	public static final String COLUMN_DATE = "DATE";
	public static final String COLUMN_REPS = "REPS";
	public static final String COLUMN_SUM = "SUM";
	public static final String COLUMN_MAX = "MAX";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This runs once after the installation and creates a database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DATABASE_TABLE_NAME +
				" (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_DATE + " DATE, " +
				COLUMN_REPS + " TEXT, " +
				COLUMN_SUM + " INTEGER, " +
				COLUMN_MAX + " INTEGER )");

	}

	/**
	 * This would run after the user updates the app. This is in case you want
	 * to modify the database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	/**
	 * This method adds a record to the database. All we pass in is the todo
	 * text
	 */
	public long addRecord(Kliky kliky) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_DATE, convertDateToString(kliky.getDate()));
		cv.put(COLUMN_REPS, kliky.getReps());
		cv.put(COLUMN_SUM, kliky.getSum());
		cv.put(COLUMN_MAX, kliky.getMax());

		return db.insert(DATABASE_TABLE_NAME, null, cv);
	}

	/**
	 * //This method returns all notes from the database
	 */
	public ArrayList<Kliky> getAllNotes() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Kliky> listItems = new ArrayList<Kliky>();

		Cursor cursor = db.rawQuery("SELECT * from " + DATABASE_TABLE_NAME,
				new String[] {});

		if (cursor.moveToFirst()) {
			do {
				Kliky kliky = new Kliky();
				kliky.id = cursor.getInt(cursor.getColumnIndex(ID));
				kliky.reps = cursor.getString(cursor.getColumnIndex(COLUMN_REPS));
				kliky.date = convertStringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
				kliky.sum = cursor.getInt(cursor.getColumnIndex(COLUMN_SUM));
				kliky.max = cursor.getInt(cursor.getColumnIndex(COLUMN_MAX));

				listItems.add(kliky);
			} while (cursor.moveToNext());
		}

		cursor.close();

		return listItems;
	}

	/*
	 * //This method deletes a record from the database.
	 */
	public void deleteKliky(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DELETE FROM " + DATABASE_TABLE_NAME + " WHERE " + ID
				+ "=" + id + "");
	}


	public static String convertDateToString(Date date) {
		return sdf.format(date);
	}

	public static Date convertStringToDate(String dateAsString) {
		Date date = null;
		try {
			date = sdf.parse(dateAsString);
		} catch (ParseException e) {
			Log.d(LOG_TAG,"Cant convert : " + dateAsString + " to txtDate " + e);
		}
		return date;
	}
}