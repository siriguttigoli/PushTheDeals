package com.virtualkarma.pushthedeals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.virtualkarma.pushthedeals.domain.DealSite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sirig on 6/17/15.
 */
public class DealSitesAdapter extends BaseAdapter {

    private static final String LOG_TAG = DealSitesAdapter.class.getSimpleName();
    private Context context;
    private List<DealSite> dealSiteList;

    public DealSitesAdapter(Context context){
        this.context = context;
        this.dealSiteList = new ArrayList<DealSite>();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.deal_site_list_item, null);
            viewHolder = new ViewHolder(view);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.dealSiteTextView.setText(dealSiteList.get(position).getName());

        return view;
    }

    public class ViewHolder{
        private TextView dealSiteTextView;

        public ViewHolder(View view){
            dealSiteTextView = (TextView) view.findViewById(R.id.dealsite_textview);
        }
    }
}
