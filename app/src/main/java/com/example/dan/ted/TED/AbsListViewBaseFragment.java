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

import android.app.Fragment;
import android.content.Intent;
import android.widget.AbsListView;

import com.example.dan.ted.TED.common.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class AbsListViewBaseFragment extends Photo_Sharing {

	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";

	protected AbsListView listView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;

	@Override
	public void onResume() {
		super.onResume();
		applyScrollListener();
	}

	//This activates when you hit an image.
    //TODO: Replace this with internal fragment switch
	protected void startImagePagerActivity(int position) {
		Intent intent = new Intent(getActivity(), ImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	private void applyScrollListener() {
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
	}
}
