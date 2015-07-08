package com.virtualkarma.pushthedeals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
public class DealsFragment extends Fragment implements AdapterView.OnItemClickListener {


    private static final String LOG_TAG = DealSiteFragment.class.getSimpleName();
    protected static final String DEAL_SITE_URL = "deal_site_url";
    protected static final String DEAL_SITE_NAME = "deal_site_name";
    private ListView mDealsListView;
    private RSSFeed dealsFeed;
    private static DealsAdapter mDealsAdapter;
    private LoadRSSFeed loadRSSFeedTask;
    private String dealSiteUrl;
    private String dealSiteName;

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mDealsListView = (ListView) rootView.findViewById(R.id.deals_listview);
        mDealsAdapter = new DealsAdapter(getActivity());
        mDealsListView.setAdapter(mDealsAdapter);

        mDealsListView.setOnItemClickListener(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            dealSiteUrl = arguments.getString(DEAL_SITE_URL);
            dealSiteName = arguments.getString(DEAL_SITE_NAME);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(dealSiteName);
        loadRSSFeedTask = new LoadRSSFeed(getActivity(), dealSiteUrl);
        Log.d(LOG_TAG, "start fetch task");
        loadRSSFeedTask.execute();

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = ((RSSItem)mDealsAdapter.getItem(position)).getURL();
        Log.d(LOG_TAG, "Deal url - " + url );
        openWebPage(url);
    }

    public static void loadTaskCompleted(RSSFeed feed) {
        mDealsAdapter.setDealsFeed(feed);
        mDealsAdapter.notifyDataSetChanged();

    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
