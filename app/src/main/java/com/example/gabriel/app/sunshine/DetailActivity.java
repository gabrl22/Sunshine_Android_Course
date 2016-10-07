package com.example.gabriel.app.sunshine;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private RelativeLayout parent;
    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        parent = (RelativeLayout)findViewById(R.id.activity_detail);

        TextView textView = new TextView(this);
        parent.addView(textView);

        Intent intent = getIntent();
        mForecastStr = intent.getStringExtra("FORECAST");
        Log.i("HOLA", mForecastStr);


        if(intent != null && intent.hasExtra("FORECAST")){
            String forecast = intent.getStringExtra("FORECAST");

            textView.setText(forecast);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr + "#forecast");

        return shareIntent;
    }
}
