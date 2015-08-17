package com.virtualkarma.pushthedeals.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sirig on 7/14/15.
 */
public class PushTheDealsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "pushthedeals.db";



    public PushTheDealsDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DEALSITE_TABLE = "CREATE TABLE " + DealSiteDao.TABLE_NAME + " (" +
                DealSiteDao._ID + " INTEGER PRIMARY KEY, " +
                DealSiteDao.COLUMN_DEALSITE_NAME + " TEXT UNIQUE NOT NULL, " +
                DealSiteDao.COLUMN_DEALSITE_URL + " TEXT NOT NULL, " +
                DealSiteDao.COLUMN_SEND_NOTIFICATION + " BOOLEAN NOT NULL);";


        db.execSQL(SQL_CREATE_DEALSITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DealSiteDao.TABLE_NAME);
        onCreate(db);

    }

}
