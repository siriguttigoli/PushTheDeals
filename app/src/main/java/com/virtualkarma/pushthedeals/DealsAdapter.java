package com.virtualkarma.pushthedeals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.virtualkarma.pushthedeals.parser.RSSFeed;
import com.virtualkarma.pushthedeals.parser.RSSItem;
import com.virtualkarma.pushthedeals.util.Utils;

/**
 * Created by sirig on 7/7/15.
 */
public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {

    private static final String LOG_TAG = DealsAdapter.class.getSimpleName();
    private Activity activity;
    private RSSFeed dealsFeed = new RSSFeed();
    private String dealSiteName;


    public DealsAdapter(Activity activity, String dealSiteName) {

        this.activity = activity;
        this.dealSiteName = dealSiteName;
    }

    @Override
    public int getItemCount() {
        return dealsFeed.getItemCount();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal_list_item,
                parent, false);
        viewHolder = new ViewHolder(view, new ViewHolder.DealsViewHolderClicks() {
            @Override
            public void onShareClicked(int position, TextView shareView) {
                shareClicked(position);
            }

            @Override
            public void onViewItemClick(int position, View viewItem) {
                showDealDetails(position);
            }
        });

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (dealSiteName.contains("RetailMeNot") || dealSiteName.contains("Ebates")) {
            holder.dealTextView.setText(dealsFeed.getItem(position).getDescription());
        } else {
            holder.dealTextView.setText(dealsFeed.getItem(position).getTitle());
        }

        holder.dealDateTextView.setText(dealsFeed.getItem(position).getDate());
        String url = dealsFeed.getItem(position).getImgUrl();
        Log.d(LOG_TAG, "Image Url - " + url);
        if (url == null || url.equals("") || dealsFeed.getItem(position).getImgHeight() == 1 ||
                dealsFeed.getItem(position).getImgWidth() == 1) {
            holder.dealImage.setVisibility(View.GONE);
        } else {
            Glide.with(activity).load(url).fitCenter().crossFade().placeholder(R.drawable.image)
                    .error(R.drawable.image).diskCacheStrategy(DiskCacheStrategy.ALL).into
                    (holder.dealImage);
        }

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView dealTextView;
        private TextView dealDateTextView;
        private TextView dealShareTextView;
        private ImageView dealImage;
        private DealsViewHolderClicks listener;

        public ViewHolder(View view, DealsViewHolderClicks listener) {
            super(view);
            this.listener = listener;
            dealTextView = (TextView) view.findViewById(R.id.list_item_deal_textview);
            dealDateTextView = (TextView) view.findViewById(R.id.list_item_date_textview);
            dealImage = (ImageView) view.findViewById(R.id.list_item_deal_imageview);
            dealShareTextView = (TextView) view.findViewById(R.id.deal_share_textview);
            dealShareTextView.setOnClickListener(this);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                listener.onShareClicked(getLayoutPosition(), (TextView) v);
            } else {
                listener.onViewItemClick(getLayoutPosition(), v);
            }

        }

        public interface DealsViewHolderClicks {
            public void onViewItemClick(int position, View viewItem);

            public void onShareClicked(int position, TextView shareView);

        }
    }

    public RSSFeed getDealsFeed() {
        return dealsFeed;
    }

    public void setDealsFeed(RSSFeed dealsFeed) {
        this.dealsFeed = dealsFeed;
    }

    private void shareClicked(int position) {
        Intent intent = Utils.shareWithAFriend(activity.getResources().getString(R.string
                .share_friend_msg_subject), activity.getResources().getString(R.string
                .share_friend_msg_content) + dealsFeed.getItem(position).getURL(), activity
                .getResources().getString(R.string.tell_friend_chooser_txt), activity);
        activity.startActivity(intent);
    }

    private void showDealDetails(int position) {
        String url = ((RSSItem) dealsFeed.getItem(position)).getURL();
        Log.d(LOG_TAG, "Deal url - " + url);
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }


}
