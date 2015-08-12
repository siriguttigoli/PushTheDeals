package com.virtualkarma.pushthedeals;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.virtualkarma.pushthedeals.util.Utils;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView abtTextView = (TextView) findViewById(R.id.about_textview);
        abtTextView.setOnClickListener(this);

        TextView rateTextView = (TextView) findViewById(R.id.rate_this_textview);
        rateTextView.setOnClickListener(this);

        TextView tellTextView = (TextView) findViewById(R.id.tell_friend_textview);
        tellTextView.setOnClickListener(this);

        setTitle(R.string.title_activity_settings);


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.about_textview:
                break;
            case R.id.rate_this_textview:
                rateThisApp();
                break;
            case R.id.tell_friend_textview:
                Intent intent = Utils.shareWithAFriend(getResources().getString(R.string
                                .tell_friend_email_subject),
                        getResources().getString(R.string.tell_friend_email_content),
                        getResources().getString(R.string.tell_friend_chooser_txt), getApplicationContext());
                startActivity(intent);
                break;
        }

    }

    private void rateThisApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google" +
                    ".com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

}



