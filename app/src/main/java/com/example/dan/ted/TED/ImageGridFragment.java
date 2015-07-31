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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.example.dan.ted.TED.common.SessionManager;
import com.example.dan.ted.TED.common.UserRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageGridFragment extends AbsListViewBaseFragment {
    View rootView;

	public static final int INDEX = 1;
    Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		session = new SessionManager(context);
		Intent intent = new Intent(context, HttpUpdateService.class);
        intent.putExtra("img_list", images);
        //intent.putExtra("img_ready", imageURLReady);
	    //TODO: We need to use broadcastreceiver to get imgready status from httpupdate service and set the Photo_Sharing variable imgready accordingly
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_photo_sharing, container, false);
		listView = (GridView) rootView.findViewById(R.id.grid);
        //If the image URLs are in place, then populate the grid with images
		if (imageURLReady) {
    		listView.setAdapter(new ImageAdapter(getActivity()));
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startImagePagerActivity(position);
                }
            });
        }
		return rootView;
	}

    @Override
    public void onStart() {
        super.onStart();
        if (!((MainActivity)getActivity()).getNetworkStatus()) {
            listView.setVisibility(View.GONE);
            rootView.findViewById(R.id.grid_no_connection).setVisibility(View.VISIBLE);
        }
        else if (listView.getVisibility() == View.GONE) {
            listView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.grid_no_connection).setVisibility(View.GONE);
        }
		String imageGridStatus = ((MainActivity)getActivity()).getBroadcastUpdate("img_grid"); //This process recreates the fragment's view if there is a new image detected
		if (imageGridStatus.equals("img_list_new")) {
			Log.e("tag", "reached new image");
            images = (((MainActivity) getActivity()).getImgList()); //TODO: Find out why this isn't displaying
            imageURLReady = true;
            listView.setAdapter(new ImageAdapter(getActivity()));
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startImagePagerActivity(position);
                }
            });
            ((MainActivity)getActivity()).resetBroadcastUpdate("img_grid");
		}

        //TODO: Check for new images

        //TODO: Optional - Find a way to have this check if the server connection failed. Probably broadcastreceiver from HttpUpdateService to MainActivity boolean, then this checks that boolean.
    }

	private static class ImageAdapter extends BaseAdapter {

		private static final String[] IMAGE_URLS = images;

		private LayoutInflater inflater;

		private DisplayImageOptions options;

		ImageAdapter(Context context) {
			inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_ted_loading)
					.showImageForEmptyUri(R.drawable.ic_ted_loading)
					.showImageOnFail(R.drawable.ic_ted_loading)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
		}

		@Override
		public int getCount() {
			return IMAGE_URLS.length;
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
				view = inflater.inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			ImageLoader.getInstance()
					.displayImage(IMAGE_URLS[position], holder.imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.progressBar.setProgress(0);
							holder.progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							holder.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view, int current, int total) {
							holder.progressBar.setProgress(Math.round(100.0f * current / total));
						}
					});
			return view;
		}
	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
	}
}