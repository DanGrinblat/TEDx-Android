package com.example.dan.ted.TED;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;

/**
 * Created by Dan on 7/13/2015.
 */
public class ImageActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This starts a new activity to display any clicked image in Image_Sharing
        //TODO: Ideally, make this a fragment that is slided to a la. TED app
        int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        Fragment fr;
        String tag;
        int titleRes;
        switch (frIndex) {
            default:
            case ImagePagerFragment.INDEX:
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                titleRes = R.string.ac_name_image_pager;
                break;
        }
        setTitle(titleRes);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }
}
