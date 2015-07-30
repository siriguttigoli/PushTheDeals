package com.virtualkarma.pushthedeals;

/**
 * Created by sirig on 6/16/15.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.virtualkarma.pushthedeals.domain.DealSite;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the list deals sites.
 */
public class DealSiteFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = DealSiteFragment.class.getSimpleName();
    protected static final String FIRST_TIME_DEALSITE = "first_time";
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

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Deal Sites");
        fetchDealsSitesTask = new FetchDealsSitesTask(getActivity());
        Log.d(LOG_TAG, "start fetch task");
        fetchDealsSitesTask.execute();

    }

    private boolean isFirstTime() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean firstTime = preferences.getBoolean(FIRST_TIME_DEALSITE, true);
        return firstTime;

    }

    private void setFirstTime(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_TIME_DEALSITE, false);
        editor.commit();
    }

    private void showAddDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a site");
        builder.setMessage("Please add a site to your list to checkout the deals");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), DealsActivity.class);
        intent.putExtra(DealsFragment.DEAL_SITE_URL, dealSitesAdapter.getItem(position).getLink());
        intent.putExtra(DealsFragment.DEAL_SITE_NAME, dealSitesAdapter.getItem(position).getName
                ());
        startActivity(intent);

    }

    public void onFetchTaskCompleted(List<DealSite> dealSitesList) {
        dealSitesAdapter.getDealSiteList().clear();
        dealSitesAdapter.getDealSiteList().addAll(dealSitesList);
        dealSitesAdapter.notifyDataSetChanged();
        if (isFirstTime()) {
            showAddDialog();
            setFirstTime();
        }
    }
}
