package com.virtualkarma.pushthedeals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.virtualkarma.pushthedeals.util.Utils;


public class DealsActivity extends AppCompatActivity {

    private String dealSiteUrl;
    private String dealSiteName;
    private DealsFragment dealsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        Intent intent = getIntent();
        dealSiteUrl = intent.getStringExtra(DealsFragment.DEAL_SITE_URL);
        dealSiteName = intent.getStringExtra(DealsFragment.DEAL_SITE_NAME);


        dealsFragment = DealsFragment.newInstance(dealSiteUrl, dealSiteName);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, dealsFragment, "deals")
                    .commit();
        }
        setProgressBarIndeterminate(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deals, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Utils.startSettingsActivity(this);
        }

        return super.onOptionsItemSelected(item);
    }



}
