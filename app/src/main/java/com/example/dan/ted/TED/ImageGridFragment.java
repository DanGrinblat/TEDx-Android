/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.dan.ted.TED;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.FragmentChangeInterface;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.example.dan.ted.TED.common.SessionManager;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageGridFragment extends AbsListViewBaseFragment implements FragmentChangeInterface{
    View rootView;

    public static final int INDEX = 1;
    static Context context;
    public BroadcastReceiver broadcastReceiver;
    private static String imageGridStatus;
    private IntentFilter intentFilter;
    private ImageAdapter imageAdapter;

    @Override
    public void fragmentBecameVisible() {
        System.out.println("TestFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);

        intentFilter = new IntentFilter();
        intentFilter.addAction("img_list_on");
        intentFilter.addAction("img_list_off");
        intentFilter.addAction("img_list_new");

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
                        if (images.length == 0) {
                            imageURLReady = false;
                            updateUI(false);
                        }
                        break;
                    case "img_list_new":
                        imageGridStatus = "img_list_new";
                        images = intent.getStringArrayExtra("img_list");
                        Log.e("tag", Integer.toString(images.length));
                        imageURLReady = true;
                        updateUI(true);
                        break;
                }
                Log.e("tag", "Broadcast received");
            }
        };
        //intent.putExtra("img_ready", imageURLReady);
        //TODO: We need to use broadcastreceiver to get imgready status from httpupdate service and set the Photo_Sharing variable imgready accordingly
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_photo_sharing, container, false);
        listView = (GridView) rootView.findViewById(R.id.grid);
        imageAdapter = new ImageAdapter(getActivity(), images);
            listView.setAdapter(imageAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (imageURLReady)
                        startImagePagerActivity(position);
                }
            });
        return rootView;
    }

    private static class ImageAdapter extends ArrayAdapter {
        private LayoutInflater inflater;
        private Context context;

        private String[] imgList = new String[0];

        ImageAdapter(Context context, String[] imgList) {
            super(context, R.layout.item_grid_image, imgList);
            this.context = context;
            this.imgList = imgList;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return imgList.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            ImageView view;

            //View view = convertView;
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_pager_image, parent, false);
                //view = new ImageView(context);
            Log.e("PICASSO", "Length: " + getCount());

            Picasso.with(context).setDebugging(true);
            Picasso.with(context)
                    .load(imgList[position])
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_ted_loading)
                    .into((ImageView) convertView);
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    public void updateUI(boolean hasImages) {
        Log.e("tag", "Fragment reached updateUI");

        if (hasImages)
            imageAdapter = new ImageAdapter(getActivity(), images);
        else {
            String[] imageConnectionLost = new String[1];
            imageConnectionLost[0] = ResourceToUri(context, R.drawable.header1).toString();
            imageAdapter = new ImageAdapter(getActivity(), imageConnectionLost);
        }

        listView.setAdapter(imageAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageURLReady)
                    startImagePagerActivity(position);
            }
        });

        imageGridStatus = "img_grid_on"; //resets status

        //TODO: Optional - Find a way to have this check if the server connection failed. Probably broadcastreceiver from HttpUpdateService to MainActivity boolean, then this checks that boolean.
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
    }

    public static Uri ResourceToUri (Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }




}