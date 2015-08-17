package com.virtualkarma.pushthedeals.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.virtualkarma.pushthedeals.MainActivity;
import com.virtualkarma.pushthedeals.domain.DealSite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sirig on 7/14/15.
 */
public class DealSiteDao {

    private static final String LOG_TAG = DealSiteDao.class.getSimpleName();


    public static final String TABLE_NAME = "deal_site_info";

    public static final String _ID = "id";

    public static final String COLUMN_DEALSITE_NAME = "dealsite_name";

    public static final String COLUMN_DEALSITE_URL = "dealsite_url";

    public static final String COLUMN_SEND_NOTIFICATION = "send_notification";

    private PushTheDealsDbHelper dbHelper;
    private Context context;

    public DealSiteDao(Context context) {

        this.context = context;
        dbHelper = new PushTheDealsDbHelper(context);
    }

    public long insert(String name, String url) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        contentValues.put(COLUMN_DEALSITE_NAME, name);
        contentValues.put(COLUMN_DEALSITE_URL, url);
        contentValues.put(COLUMN_SEND_NOTIFICATION, false);

        long newRowId = -1;
        try {
            newRowId = db.insert(TABLE_NAME, null, contentValues);
            Log.d(LOG_TAG, "Site inserted");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            db.close();
        }
        return newRowId;
    }

    public List<DealSite> lookup() {
        List<DealSite> siteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        String[] projection = {DealSiteDao.COLUMN_DEALSITE_NAME, DealSiteDao.COLUMN_DEALSITE_URL};

        try {
            cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_DEALSITE_NAME));
                String url = cursor.getString(cursor.getColumnIndex(COLUMN_DEALSITE_URL));
                DealSite ds = new DealSite();
                ds.setName(name);
                ds.setLink(url);
                siteList.add(ds);
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            db.close();
            if (cursor != null)
                cursor.close();
        }

        return siteList;
    }

    public void delete(DealSite dealSite) throws Exception {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "delete site " + dealSite.getName());
        db.delete(TABLE_NAME, COLUMN_DEALSITE_NAME + "=?", new String[]{dealSite.getName()});

        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cur != null) {
            cur.moveToFirst();                       // Always one row returned.
            if (cur.getInt(0) == 0) {               // Zero count means empty table.
                setSharedPreferences(false);
            }
        }
    }

    private void setSharedPreferences(boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainActivity.FAVORITES_AVAILABLE, value);
        editor.commit();
    }

}
