package com.virtualkarma.pushthedeals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.virtualkarma.pushthedeals.parser.RSSFeed;
import com.virtualkarma.pushthedeals.util.LoadRSSFeed;

/**
 * Created by sirig on 7/7/15.
 */
public class DealsFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {


    private static final String LOG_TAG = DealSiteFragment.class.getSimpleName();
    protected static final String DEAL_SITE_URL = "deal_site_url";
    protected static final String DEAL_SITE_NAME = "deal_site_name";
    private RecyclerView dealsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RSSFeed dealsFeed;
    private DealsAdapter dealsAdapter;
    private LoadRSSFeed loadRSSFeedTask;
    private String dealSiteUrl;
    private String dealSiteName;
    private Activity activity;
    private MenuItem progressBarItem;

    public DealsFragment() {
    }

    public static DealsFragment newInstance(String url, String name) {
        DealsFragment dealsFragment = new DealsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DEAL_SITE_URL, url);
        bundle.putString(DEAL_SITE_NAME, name);
        dealsFragment.setArguments(bundle);

        return dealsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            dealSiteUrl = arguments.getString(DEAL_SITE_URL);
            dealSiteName = arguments.getString(DEAL_SITE_NAME);
        }
        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_deals, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.deals_swipe_refresh_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        dealsRecyclerView = (RecyclerView) rootView.findViewById(R.id.deals_recycler_view);
        dealsRecyclerView.setLayoutManager(layoutManager);
        dealsAdapter = new DealsAdapter(getActivity(), dealSiteName);
        dealsRecyclerView.setAdapter(dealsAdapter);

//        dealsListView.setOnItemClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.deal_fragment, menu);
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        progressBarItem = menu.findItem(R.id.action_progress).setVisible(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
//            case R.id.action_add_favorites:
//                Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
//                addToFavorites();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated");
        activity = getActivity();
        activity.setTitle(dealSiteName);
        refreshContent();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (loadRSSFeedTask != null && loadRSSFeedTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d(LOG_TAG, "loadRSSFeedTask cancelled");
            loadRSSFeedTask.cancel(true);
        }

    }

    @Override
    public void onRefresh() {
        refreshContent();
    }

    public void refreshContent() {
        loadRSSFeedTask = new LoadRSSFeed(getActivity(), dealSiteUrl);
        Log.d(LOG_TAG, "start fetch task");
        loadRSSFeedTask.execute();
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String url = ((RSSItem) dealsAdapter.getItem(position)).getURL();
//        Log.d(LOG_TAG, "Deal url - " + url);
//        openWebPage(url);
//    }

    public void loadTaskCompleted(RSSFeed feed) {
        if (feed.getItemCount() > 0) {
            dealsAdapter.setDealsFeed(feed);
            dealsAdapter.notifyDataSetChanged();
        } else {
            showErrorDialog();
        }
        if (progressBarItem != null)
            progressBarItem.setVisible(false);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_dialog_title);
        builder.setMessage(R.string.error_dialog_msg);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
