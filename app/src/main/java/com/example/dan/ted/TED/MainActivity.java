package com.example.dan.ted.TED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.SessionManager;
import com.example.dan.ted.TED.common.SlidingTabLayout;
import com.example.dan.ted.TED.common.UserRequest;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private boolean networkConnected;
    private String imageGridStatus = "img_grid_on";
    private String[] imgList;
    SessionManager session;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        networkConnected = getNetworkStatus();

        session = new SessionManager(MainActivity.this);
        String token = (String)session.getUserDetails().get("token");
        UserRequest.checkLogin(MainActivity.this, token);

        IntentFilter filter = new IntentFilter();
        filter.addAction("img_list_on");
        filter.addAction("img_list_off");
        filter.addAction("img_list_new");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch(action) {
                    case "img_list_on":
                        imageGridStatus = "img_list_on";
                        break;
                    case "img_list_off":
                        imageGridStatus = "img_list_off";
                        break;
                    case "img_list_new":
                        imageGridStatus = "img_list_new";
                        imgList = intent.getStringArrayExtra("img_list");
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.header2);

        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Give the SlidingTabLayout the ViewPager
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        if (id == R.id.action_logout) {
            session.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public void onFragmentInteraction(Uri uri) {
    }*/

    public boolean getNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
            networkConnected = false;
        else
            networkConnected = true;
        return networkConnected;
    }

    public void resetBroadcastUpdate(String fragment) {
        if (fragment.equals("img_grid")) {
            imageGridStatus = "img_grid_on";
        }
    }

    public String[] getImgList() {
        return imgList;
    }

    public String getBroadcastUpdate(String fragment) {
        if (fragment.equals("img_grid")) {
            Log.e("tag", imageGridStatus);
            return imageGridStatus;
        }
        else return "Nothing";
    }
/*
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action) {
                case "img_list_on":
                    imageGridStatus = "img_list_on";
                    break;
                case "img_list_off":
                    imageGridStatus = "img_list_off";
                    break;
                case "img_list_new":
                    Log.e("tag", "img_list_new set");
                    imageGridStatus = "img_list_new";
                    imgList = intent.getStringArrayExtra("img_list");
                    break;
            }
        }
    };
*/
    public void onNotificationsFragmentInteraction(String string){

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        // getItem is called to instantiate the fragment for the given page.
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    mProfile mProfile = new mProfile();
                    return mProfile;
                case 1:
                    Photo_Sharing photo_sharing = new ImageGridFragment();
                    return photo_sharing;
                case 2:
                    Event_Details event_details = new Event_Details();
                    return event_details;
                case 3:
                    Notifications notifications = new Notifications();
                    return notifications;
                case 4:
                    More_Info more_info = new More_Info();
                    return more_info;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
                case 3:
                    return getString(R.string.title_section4);
                case 4:
                    return getString(R.string.title_section5);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
