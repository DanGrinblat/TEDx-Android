package com.example.dan.ted.TED;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeakerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeakerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeakerFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int INDEX = 3;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private ImageAdapter imageAdapter;
    private TextView textNoConnection;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        //startService();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        intentFilter = new IntentFilter();
        intentFilter.addAction("img_list_off");
        intentFilter.addAction("img_list_new");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speaker, container, false);
    }

    public void startService() {
        Intent intent = new Intent(getActivity().getApplicationContext(), HttpUpdateService.class);
        intent.putExtra("intent", "Bios");
        intent.putExtra("bio_list", ImageListFragment.speakerBios);
        context.startService(intent);
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

            Picasso.with(context).setDebugging(true);
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
}
