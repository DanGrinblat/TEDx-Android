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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageListFragment extends AbsListViewBaseFragment {

	public static final int INDEX = 0;
	private static String[] list_images = new String[0];
	private static String[] speakerNames = new String[0];
	private static String[] speakerImages = new String[0];
	private static boolean speakerURLReady = false;
	private ImageAdapter imageAdapter;
	private IntentFilter intentFilter;
	private BroadcastReceiver broadcastReceiver;
	private static TextView textNoConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService();

		intentFilter = new IntentFilter();
		intentFilter.addAction("speaker_list_off");
		intentFilter.addAction("speaker_list_new");

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				switch(action) {
					case "speaker_list_off":
						if (speakerNames.length == 0) {
							speakerURLReady = false;
							updateUI(false);
						}
						break;
					case "speaker_list_new":
						speakerNames = intent.getStringArrayExtra("speaker_list");
						speakerImages = intent.getStringArrayExtra("speaker_image_list");
						speakerURLReady = true;
						updateUI(true);
						break;
				}
			}
		};
	}

	public void startService() {
		Intent intent = new Intent(getActivity().getApplicationContext(), HttpUpdateService.class);
		intent.putExtra("intent", "Speakers");
		intent.putExtra("speaker_list", speakerNames);
		context.startService(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (speakerNames.length == 0)
			startService();

		View rootView = inflater.inflate(R.layout.fr_image_list, container, false);
		textNoConnection = (TextView) rootView.findViewById(R.id.textViewNoSpeaker);
		listView = (ListView) rootView.findViewById(android.R.id.list);
		imageAdapter = new ImageAdapter(getActivity(), speakerNames, speakerImages);
		((ListView) listView).setAdapter(imageAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//startImagePagerActivity(position);
			}
		});
		if (speakerURLReady)
			textNoConnection.setVisibility(View.GONE);
		return rootView;
	}

	private static class ImageAdapter extends ArrayAdapter {
		private LayoutInflater inflater;
		private Context context;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		private String[] speakerNames = new String[0];
		private String[] speakerImages = new String[0];

		ImageAdapter(Context context, String[] speakerNames, String[] speakerImages) {
			super(context, R.layout.item_list_image, speakerImages);
			this.context = context;
			this.speakerNames = speakerNames;
			this.speakerImages = speakerImages;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return speakerImages.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(speakerNames[position]);

			Picasso.with(context).setDebugging(true);
			Picasso.with(context)
					.load(speakerImages[position])
					.fit()
					.centerCrop()
					.placeholder(R.drawable.ic_ted_loading)
					.into(holder.image);

			//ImageLoader.getInstance().displayImage(imgList[position], holder.image, options, animateFirstListener);

			return convertView;
		}
	}

	static class ViewHolder {
		TextView text;
		ImageView image;
	}

	public void updateUI(boolean hasImages) {
		if (hasImages) {
			if (textNoConnection.getVisibility() == View.VISIBLE)
				textNoConnection.setVisibility(View.GONE);
			imageAdapter = new ImageAdapter(getActivity(), speakerNames, speakerImages);
			((ListView) listView).setAdapter(imageAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//if (speakerURLReady)
						//startImagePagerActivity(position);
				}
			});
		}
		else
			textNoConnection.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		AnimateFirstDisplayListener.displayedImages.clear();
		getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
	}

		private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}