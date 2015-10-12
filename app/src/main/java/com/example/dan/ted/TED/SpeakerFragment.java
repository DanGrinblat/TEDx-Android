package com.example.dan.ted.TED;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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
    IntentFilter intentFilter;
    TextView textViewSpeakerName;
    private TextView textViewSpeakerBio;
    private ImageView imageView;
    private String[] bioList;
    private String[] bioNameList;
    private HashMap<String, String> speakerBios1;

    private boolean bioListReady;
    private int position;

    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        speakerBios1 = new HashMap<String, String>(ImageListFragment.speakerNames.length);
        startService();

        intentFilter = new IntentFilter();
        intentFilter.addAction("bio_list_off");
        intentFilter.addAction("bio_list_new");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch(action) {
                    case "bio_list_off":
                        if (images.length == 0) {
                            bioListReady = false;
                            updateUI(false);
                            //startService();
                        }
                        break;
                    case "bio_list_new":
                        //bioList = intent.getStringArrayExtra("bio_list");
                        bioNameList = intent.getStringArrayExtra("bio_name_list");
                        speakerBios1 = (HashMap<String, String>)intent.getSerializableExtra("bio_list");
                        bioListReady = true;
                        updateUI(true);
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_speaker, container, false);
        position = getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0);

        textViewSpeakerName = (TextView)rootView.findViewById(R.id.textViewName);
        textViewSpeakerName.setText(ImageListFragment.speakerNames[position]);


        textViewSpeakerBio = (TextView)rootView.findViewById(R.id.textViewBio);
        textViewSpeakerBio.setMovementMethod(new ScrollingMovementMethod());

        imageView = (ImageView)rootView.findViewById(R.id.image);
        setImage(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0), imageView);
        return rootView;
    }

    public void startService() {
        Intent intent = new Intent(getActivity().getApplicationContext(), HttpUpdateService.class);
        intent.putExtra("intent", "Bios");
        intent.putExtra("bio_list", bioList);
        context.startService(intent);
    }

    private void setImage(int position, ImageView imageView) {
        Picasso.with(context)
                .load(ImageListFragment.speakerImages[position])
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                        //.fit()
                        //.centerCrop()
                .resize(800, 800)
                .centerInside()
                .placeholder(R.drawable.ic_ted_loading)
                .into(imageView);
    }

    public void updateUI(boolean hasBios) {
        if (hasBios) {
            position = getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0);
            textViewSpeakerBio.setText(speakerBios1.get(bioNameList[position]));
            //setImage(position, imageView);
        }
        else {
            if (TextUtils.isEmpty(textViewSpeakerBio.getText()))
                textViewSpeakerBio.setText(R.string.connection_error);
        }
    }
    static class ViewHolder {
        ImageView imageView;
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
