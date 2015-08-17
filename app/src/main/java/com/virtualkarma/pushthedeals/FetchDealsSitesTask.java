package com.virtualkarma.pushthedeals;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.virtualkarma.pushthedeals.dao.DealSiteDao;
import com.virtualkarma.pushthedeals.domain.DealSite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FetchDealsSitesTask extends AsyncTask<Void, Void, List<DealSite>> {

    private static final String LOG_TAG = FetchDealsSitesTask.class.getSimpleName();
    private static final String TIME = "time";
    private static final String FILENAME = "dealsites";
    private static final String LAST_DOWNLOADED = "last_downloaded";
    private MainActivity activity;

    public FetchDealsSitesTask(Activity activity) {
        this.activity = (MainActivity) activity;
    }

    @Override
    protected List<DealSite> doInBackground(Void... params) {

        List<DealSite> dealSiteList = new ArrayList<>();
        boolean downloadFromNetwork = shouldDownloadFromNetwork();
        try {
            if (downloadFromNetwork) {
                dealSiteList = lookupDealSitesFromNetwork();
            } else {
                dealSiteList = lookupDealSites();
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Json Error" + e.getMessage());
        }


        return dealSiteList;
    }

    private boolean shouldDownloadFromNetwork() {

        SharedPreferences preferences = activity.getSharedPreferences
                ("last_downloaded", Context.MODE_PRIVATE);


        long lastDownloadedTime = preferences.getLong(TIME, 0);

        if (lastDownloadedTime > 0) {
            Date date = new Date();
            long timeNow = date.getTime();

            long diff = timeNow - lastDownloadedTime;
            long diffHours = diff / (60 * 60 * 1000) % 24;

            if (diffHours >= 24) {
                Log.d(LOG_TAG, "Download from network");
                return true;
            } else {
                Log.d(LOG_TAG, "Download from file");
                return false;
            }
        }
        return true;

    }

    private List<DealSite> lookupDealSitesFromNetwork() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        FileOutputStream fileOutputStream = null;
        String dealSitesJsonStr;
        List<DealSite> dealSiteList = new ArrayList<>();

        try {

            final String SITE_LIST_URL = "http://www.virtualkarma.com/ptd/v4//dealslist.php?order=standard&app_version=37";

            URL url = new URL(SITE_LIST_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
//                List<String> stringList = new ArrayList<>();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
                Log.d(LOG_TAG, line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            dealSitesJsonStr = buffer.toString();

            fileOutputStream = activity.openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            fileOutputStream.write(dealSitesJsonStr.getBytes());

            SharedPreferences preferences = activity.getSharedPreferences
                    (LAST_DOWNLOADED, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            long time = new Date().getTime();
            editor.putLong(TIME, time);
            editor.commit();

            Log.d(LOG_TAG, "Current time " + time);

            dealSiteList = lookupDealSites();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);
            }

        }
        return dealSiteList;
    }

    private List<DealSite> lookupDealSites() throws JSONException {

        String dealSiteJsonStr = "";
        FileInputStream fileInputStream = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        List<DealSite> dealSiteList = new ArrayList<>();

        try {
            fileInputStream = activity.openFileInput(FILENAME);
            reader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d(LOG_TAG, line + " ");
            }
            dealSiteJsonStr = buffer.toString();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

        }

        dealSiteList = getDealSiteInfoFromJson(dealSiteJsonStr);
        List<DealSite> withoutFavList = getListWithoutFavorites(dealSiteList);
        if (withoutFavList.isEmpty())
            return dealSiteList;

        return withoutFavList;
    }


    private List<DealSite> getDealSiteInfoFromJson(String dealSiteJsonStr) throws JSONException {

        List<DealSite> dealSitesList = new ArrayList<>();
        JSONObject dealSiteListJsonObject = new JSONObject(dealSiteJsonStr);
        Iterator<String> keys = dealSiteListJsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONArray dealSiteJsonArray = dealSiteListJsonObject.getJSONArray(key);
            for (int i = 0; i < dealSiteJsonArray.length(); i++) {
                JSONObject dealSiteJsonObject = dealSiteJsonArray.getJSONObject(i);
                DealSite dealSite = new DealSite(dealSiteJsonObject);
                dealSitesList.add(dealSite);
            }
        }


        return dealSitesList;
    }

    private List<DealSite> getListWithoutFavorites(List<DealSite> allSiteList) {
        List<DealSite> withoutFavList = new ArrayList<>();
        List<DealSite> favList = new ArrayList<>();
        DealSiteDao dealSiteDao = new DealSiteDao(activity);
        try {
            favList = dealSiteDao.lookup();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        if (favList != null) {
            for (DealSite site : allSiteList) {
                if (!favList.contains(site))
                    withoutFavList.add(site);
            }
        }
        return withoutFavList;
    }

    @Override
    protected void onPostExecute(List<DealSite> dealSitesList) {
        super.onPostExecute(dealSitesList);
        DealSiteFragment dealSiteFragment = (DealSiteFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("deal_site");

        dealSiteFragment.onFetchTaskCompleted(dealSitesList);
    }
}
