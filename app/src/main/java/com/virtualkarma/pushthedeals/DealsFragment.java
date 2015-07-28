package com.virtualkarma.pushthedeals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.virtualkarma.pushthedeals.parser.RSSFeed;
import com.virtualkarma.pushthedeals.parser.RSSItem;
import com.virtualkarma.pushthedeals.util.LoadRSSFeed;

/**
 * Created by sirig on 7/7/15.
 */
public class DealsFragment extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {


    private static final String LOG_TAG = DealSiteFragment.class.getSimpleName();
    protected static final String DEAL_SITE_URL = "deal_site_url";
    protected static final String DEAL_SITE_NAME = "deal_site_name";
    private ListView dealsListView;
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
        View rootView = inflater.inflate(R.layout.fragment_deals, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.deals_swipe_refresh_layout);
        dealsListView = (ListView) rootView.findViewById(R.id.deals_listview);
        dealsAdapter = new DealsAdapter(getActivity());
        dealsListView.setAdapter(dealsAdapter);

        dealsListView.setOnItemClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);


        Bundle arguments = getArguments();
        if (arguments != null) {
            dealSiteUrl = arguments.getString(DEAL_SITE_URL);
            dealSiteName = arguments.getString(DEAL_SITE_NAME);
        }
        Log.d(LOG_TAG, "onCreateView");
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
    public void onRefresh() {
        refreshContent();
    }

    public void refreshContent() {
        loadRSSFeedTask = new LoadRSSFeed(getActivity(), dealSiteUrl);
        Log.d(LOG_TAG, "start fetch task");
        loadRSSFeedTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = ((RSSItem) dealsAdapter.getItem(position)).getURL();
        Log.d(LOG_TAG, "Deal url - " + url);
        openWebPage(url);
    }

    public void loadTaskCompleted(RSSFeed feed) {
        dealsAdapter.setDealsFeed(feed);
        dealsAdapter.notifyDataSetChanged();
        progressBarItem.setVisible(false);
        swipeRefreshLayout.setRefreshing(false);

    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
