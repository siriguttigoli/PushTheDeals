package com.virtualkarma.pushthedeals;

/**
 * Created by sirig on 6/16/15.
 */

import android.content.Intent;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the list deals sites.
 */
public class DealSiteFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = DealSiteFragment.class.getSimpleName();
    private ListView mDealSiteListView;
    private List<DealSite> dealSitesList = new ArrayList<>();
    private DealSiteAdapter mDealSitesAdapter;
    private FetchDealsSitesTask fetchDealsSitesTask;


    public DealSiteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mDealSiteListView = (ListView) rootView.findViewById(R.id.deals_listview);
        mDealSitesAdapter = new DealSiteAdapter(getActivity());
        mDealSiteListView.setAdapter(mDealSitesAdapter);

        mDealSiteListView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDealsSitesTask = new FetchDealsSitesTask();
        Log.d(LOG_TAG, "start fetch task");
        fetchDealsSitesTask.execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), DealsActivity.class);
        intent.putExtra(DealsFragment.DEAL_SITE_URL, mDealSitesAdapter.getItem(position).getLink());
        intent.putExtra(DealsFragment.DEAL_SITE_NAME, mDealSitesAdapter.getItem(position).getName
                ());
        startActivity(intent);

    }

    public class FetchDealsSitesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {

                final String SITE_LIST_URL = "http://www.virtualkarma.com/deals.txt";

                URL url = new URL(SITE_LIST_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                List<String> stringList = new ArrayList<>();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringList.add(line);
                    Log.d(LOG_TAG, "" + line + "\n");
                }

                getDealSites(stringList);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private void getDealSites(List<String> stringList) {
            int length = stringList.size();
            for (int i = 0; i < length; i++) {
                DealSite dealSite = new DealSite();
                dealSite.setName(stringList.get(i++));
                dealSite.setLink(stringList.get(i));
                dealSite.setEnableNotification(false);
                dealSite.setNumOfDeals(0);
                Log.d(LOG_TAG, "" + dealSite);
                dealSitesList.add(dealSite);
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDealSitesAdapter.getDealSiteList().clear();
            mDealSitesAdapter.getDealSiteList().addAll(dealSitesList);
            mDealSitesAdapter.notifyDataSetChanged();
        }
    }
}
