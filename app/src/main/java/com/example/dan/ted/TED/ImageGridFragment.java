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
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.FragmentChangeInterface;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.example.dan.ted.TED.common.SessionManager;
import com.squareup.picasso.Picasso;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */

//Photo Gallery
public class ImageGridFragment extends AbsListViewBaseFragment implements FragmentChangeInterface{
    View rootView;

    public static final int INDEX = 1;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private ImageAdapter imageAdapter;
    private TextView textNoConnection;

    @Override
    public void fragmentBecameVisible() {
        System.out.println("TestFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBroadcastReceiver();
    }

    public void startService() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), HttpUpdateService.class);
                intent.putExtra("intent", "Photos");
                getActivity().startService(intent);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 10 * DateUtils.SECOND_IN_MILLIS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_photo_sharing, container, false);
        listView = (GridView) rootView.findViewById(R.id.grid);
        imageAdapter = new ImageAdapter(getActivity(), images);
        textNoConnection = (TextView) rootView.findViewById(R.id.textViewNoPhoto);

        ((GridView) listView).setAdapter(imageAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageURLReady)
                    startImagePagerActivity(position, ImagePagerFragment.INDEX);
            }
            });

        if (images.length != 0)
            textNoConnection.setVisibility(View.GONE);



        return rootView;
    }

    private void setBroadcastReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("img_list_off");
        intentFilter.addAction("img_list_new");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch(action) {
                    case "img_list_off":
                        if (images.length == 0) {
                            imageURLReady = false;
                            updateUI(false);
                            startService();
                        }
                        break;
                    case "img_list_new":
                        images = intent.getStringArrayExtra("img_list");
                        imageURLReady = true;
                        updateUI(true);
                        break;
                }
            }
        };
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
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_pager_image, parent, false);
                //view = new ImageView(context);
                holder = new ViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                view.setTag(holder);
            }
            else
                holder = (ViewHolder) view.getTag();

            Picasso.with(context)
                    .load(imgList[position])
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_ted_loading)
                    .into((ImageView) view);
            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
    }

    public void updateUI(boolean hasImages) {
        if (hasImages) {
            textNoConnection.setVisibility(View.GONE);
            imageAdapter = new ImageAdapter(getActivity(), images);
            ((GridView) listView).setAdapter(imageAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (imageURLReady)
                        startImagePagerActivity(position, ImagePagerFragment.INDEX);
                }
            });
        }
        else
            textNoConnection.setVisibility(View.VISIBLE);
            //String[] imageConnectionLost = new String[1];
            //imageConnectionLost[0] = ResourceToUri(context, R.drawable.header1).toString();
            //imageAdapter = new ImageAdapter(getActivity(), imageConnectionLost);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getActivity().getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
        } catch (IllegalArgumentException e) {
            //
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            //
        }
    }
}