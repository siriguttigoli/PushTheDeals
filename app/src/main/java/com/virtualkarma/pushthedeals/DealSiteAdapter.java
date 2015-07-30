package com.virtualkarma.pushthedeals;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.virtualkarma.pushthedeals.dao.DealSiteDao;
import com.virtualkarma.pushthedeals.domain.DealSite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sirig on 6/17/15.
 */
public class DealSiteAdapter extends BaseAdapter {

    private static final String LOG_TAG = DealSiteAdapter.class.getSimpleName();
    private List<DealSite> dealSiteList;
    private Activity activity;

    public DealSiteAdapter(Activity activity) {
        this.activity = activity;
        this.dealSiteList = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return dealSiteList.size();
    }

    @Override
    public DealSite getItem(int position) {
        return dealSiteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public List<DealSite> getDealSiteList() {
        return dealSiteList;
    }

    public void setDealSiteList(List<DealSite> dealSiteList) {
        this.dealSiteList = dealSiteList;
    }

    private View.OnClickListener addClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            addToFavorites(position);
        }
    };

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.deal_site_list_item, null);
            viewHolder = new ViewHolder(position);
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.relativeLayout);
            viewHolder.dealSiteTextView = (TextView) viewHolder.linearLayout.findViewById(R.id
                    .dealsite_textview);
            viewHolder.addSiteImageView = (ImageView) viewHolder.linearLayout.findViewById(R.id.add_to_fav);
            viewHolder.addSiteImageView.setOnClickListener(addClickListener);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.dealSiteTextView.setText(dealSiteList.get(position).getName());
        viewHolder.addSiteImageView.setVisibility(View.VISIBLE);
        viewHolder.addSiteImageView.setTag(position);

        return view;
    }

    private void addToFavorites(final int position) {
        addToPreferences();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String dealSiteName = dealSiteList.get(position).getName();
                String dealSiteUrl = dealSiteList.get(position).getLink();
                try {
                    DealSiteDao dealSiteDao = new DealSiteDao(activity);
                    long row = dealSiteDao.insert(dealSiteName, dealSiteUrl);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (activity instanceof MainActivity) {
                                ((MainActivity) activity).switchContent(new FavoritesFragment());
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
        }).start();


    }

    private void addToPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean favAvailable = preferences.getBoolean(MainActivity.FAVORITES_AVAILABLE, false);
        if (!favAvailable) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(MainActivity.FAVORITES_AVAILABLE, true);
            editor.commit();
        }
    }


    static class ViewHolder {
        private LinearLayout linearLayout;
        private TextView dealSiteTextView;
        private ImageView addSiteImageView;
        private int pos;

        public ViewHolder(int position) {
            pos = position;

        }

        public int getPos() {
            return pos;
        }
    }
}
