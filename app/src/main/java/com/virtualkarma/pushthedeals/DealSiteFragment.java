package com.virtualkarma.pushthedeals;

/**
 * Created by sirig on 6/16/15.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.virtualkarma.pushthedeals.domain.DealSite;

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
import java.util.List;

/**
 * Shows the list deals sites.
 */
public class DealSiteFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = DealSiteFragment.class.getSimpleName();
    private ListView dealSiteListView;
    private List<DealSite> dealSitesList = new ArrayList<>();
    private DealSiteAdapter dealSitesAdapter;
    private FetchDealsSitesTask fetchDealsSitesTask;


    public DealSiteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        dealSiteListView = (ListView) rootView.findViewById(R.id.deals_site_listview);
        dealSitesAdapter = new DealSiteAdapter(getActivity());
        dealSiteListView.setAdapter(dealSitesAdapter);

        dealSiteListView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Deal Sites");
        fetchDealsSitesTask = new FetchDealsSitesTask();
        Log.d(LOG_TAG, "start fetch task");
        fetchDealsSitesTask.execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), DealsActivity.class);
        intent.putExtra(DealsFragment.DEAL_SITE_URL, dealSitesAdapter.getItem(position).getLink());
        intent.putExtra(DealsFragment.DEAL_SITE_NAME, dealSitesAdapter.getItem(position).getName
                ());
        startActivity(intent);

    }

    public class FetchDealsSitesTask extends AsyncTask<Void, Void, Void> {

        protected static final String TIME = "time";
        protected static final String FILENAME = "dealsites";
        protected static final String LAST_DOWNLOADED = "last_downloaded";

        @Override
        protected Void doInBackground(Void... params) {

            boolean downloadFromNetwork = shouldDownloadFromNetwork();
            if (downloadFromNetwork) {
                lookupDealSitesFromNetwork();
            } else {
                lookupDealSites();
            }

            return null;
        }

        private boolean shouldDownloadFromNetwork() {

            SharedPreferences preferences = getActivity().getSharedPreferences
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

        private boolean lookupDealSitesFromNetwork() {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            FileOutputStream fileOutputStream = null;

            try {

                final String SITE_LIST_URL = "http://www.virtualkarma.com/deals.txt";

                URL url = new URL(SITE_LIST_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
//                List<String> stringList = new ArrayList<>();
                if (inputStream == null) {
                    // Nothing to do.
                    return true;
                }

                fileOutputStream = getActivity().openFileOutput(FILENAME,
                        Context.MODE_PRIVATE);

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    fileOutputStream.write(line.getBytes());
                    fileOutputStream.write(System.getProperty("line.separator").getBytes());
                }


                SharedPreferences preferences = getActivity().getSharedPreferences
                        (LAST_DOWNLOADED, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                long time = new Date().getTime();
                editor.putLong(TIME, time);
                editor.commit();

                Log.d(LOG_TAG, "Current time " + time);

                lookupDealSites();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                try {
                    if (reader != null)
                        reader.close();
                    if (fileOutputStream != null)
                        fileOutputStream.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }

            }
            return false;
        }

        private void lookupDealSites() {

            List<String> stringList = new ArrayList<>();
            FileInputStream fileInputStream = null;
            BufferedReader reader = null;

            try {
                fileInputStream = getActivity().openFileInput(FILENAME);
                reader = new BufferedReader(new InputStreamReader(fileInputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringList.add(line);
                    Log.d(LOG_TAG, line + " ");
                }
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

            int length = stringList.size();
            for (int i = 0; i < length; i++) {
                DealSite dealSite = new DealSite();
                dealSite.setName(stringList.get(i++));
                dealSite.setLink(stringList.get(i));
                dealSite.setEnableNotification(false);
                dealSite.setNumOfDeals(0);
                dealSitesList.add(dealSite);
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dealSitesAdapter.getDealSiteList().clear();
            dealSitesAdapter.getDealSiteList().addAll(dealSitesList);
            dealSitesAdapter.notifyDataSetChanged();
        }
    }
}
