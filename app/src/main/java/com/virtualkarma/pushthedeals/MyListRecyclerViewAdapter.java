package com.virtualkarma.pushthedeals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualkarma.pushthedeals.dao.DealSiteDao;
import com.virtualkarma.pushthedeals.domain.DealSite;
import com.virtualkarma.pushthedeals.util.ItemTouchHelperAdapter;
import com.virtualkarma.pushthedeals.util.ItemTouchHelperViewHolder;

import java.util.List;

/**
 * Created by sirig on 7/14/15.
 */
public class MyListRecyclerViewAdapter extends RecyclerView.Adapter<MyListRecyclerViewAdapter
        .MyListHolder> implements ItemTouchHelperAdapter {


    private static final String LOG_TAG = MyListRecyclerViewAdapter.class.getSimpleName();
    private static final int REQUEST_NUM_DEALS = 1;
    private List<DealSite> myDealSitesList;
    static private Activity activity;
    private BackgroundContainer backgroundContainer;


    public MyListRecyclerViewAdapter(List<DealSite> dealSiteList, Activity activity) {
        myDealSitesList = dealSiteList;
        this.activity = activity;

    }

    @Override
    public MyListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .deal_site_list_item, viewGroup, false);

        MyListHolder holder = new MyListHolder(view, new MyListHolder.RecyclerViewClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(activity, DealsActivity.class);
                intent.putExtra(DealsFragment.DEAL_SITE_URL, myDealSitesList.get(position).getLink());
                intent.putExtra(DealsFragment.DEAL_SITE_NAME, myDealSitesList.get(position).getName
                        ());
                activity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyListHolder holder, int i) {
        holder.dealSiteNameText.setText(myDealSitesList.get(i).getName());
        holder.addImage.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return myDealSitesList.size();
    }

    public List<DealSite> getMyDealSitesList() {
        return myDealSitesList;
    }

    public static class MyListHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener, ItemTouchHelperViewHolder {

        private TextView dealSiteNameText, numDealsText;
        private ImageView addImage;
        private RecyclerViewClickListener recyclerViewClickListener;


        public MyListHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            recyclerViewClickListener = listener;
            dealSiteNameText = (TextView) itemView.findViewById(R.id.dealsite_textview);
            FrameLayout frameLayout = (FrameLayout) itemView.findViewById(R.id
                    .add_num_deals_framelayout);
            addImage = (ImageView) frameLayout.findViewById(R.id.add_to_fav);
            numDealsText = (TextView) frameLayout.findViewById(R.id.num_deals_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onItemClick(getLayoutPosition(), v);
        }

        public static interface RecyclerViewClickListener {
            public void onItemClick(int position, View v);
        }

        @Override
        public void onItemSelected() {

        itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onItemClear() {

            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        deleteSite(myDealSitesList.get(position));
        myDealSitesList.remove(position);
        notifyItemRemoved(position);
    }

    private void deleteSite(final DealSite dealSite) {
        new Thread() {
            public void run() {
                DealSiteDao dealSiteDao = new DealSiteDao(activity);
                try {
                    dealSiteDao.delete(dealSite);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "A problem occured...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
}
