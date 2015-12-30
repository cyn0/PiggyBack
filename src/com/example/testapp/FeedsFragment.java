package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.autocomplete.MyAutoComplete;
import com.example.autocomplete.PlaceArrayAdapter;
import com.example.autocomplete.MyAutoComplete.AutoCompleteListener;
import com.example.autocomplete.PlaceArrayAdapter.PlaceAutocomplete;
import com.example.datamodel.GcmMessage;
import com.example.datamodel.OfferRide;
import com.example.datamodel.User;
import com.example.feeds.CustomExpandableListAdapter;
import com.example.feeds.CustomListAdapter;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.Constants;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;

public class FeedsFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    PlaceArrayAdapter mPlaceArrayAdapter;
    private String TAG = "FeedsFragment";
    Button chooseSource, chooseDestination, done;
    AutoCompleteTextView sourceTextView, destinationTextView;
    
    Place source, destination;
    OfferRide mOfferRide;
    Activity mActivity;
    
    ExpandableListView expListView;
    final ArrayList<OfferRide> listItems = new ArrayList<OfferRide>();
    CustomExpandableListAdapter adapter;
    FrameLayout barker;
    
    public static FeedsFragment newInstance(int page, String title) {
    	FeedsFragment fragmentFirst = new FeedsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        
        mOfferRide = new OfferRide();
        mActivity = getActivity();
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);

        adapter = new CustomExpandableListAdapter(mActivity, expListView, listItems);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        barker = (FrameLayout) view.findViewById(R.id.barker);
        
        expListView.setAdapter(adapter);
        barker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openNewRideFragment();
			}
		});
        ((ImageView) view.findViewById(R.id.next)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openNewRideFragment();
			}
		});
        
        ((Button) view.findViewById(R.id.skip)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openNewRideFragment();
			}
		});

        boolean registrationSucsess = User.getSharedInstance().getRegistrationStatus();
        if(!registrationSucsess){
        	return view;
        }
        fetchFeeds();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.registerReceiver(mMessageReceiver, new IntentFilter(Constants.UPDATE_LIST_VIEW));
    }

    //Must unregister onPause()
    @Override
	public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(mMessageReceiver);
    }
    
    public void fetchFeeds(){
    	final ProgressDialog pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Fetching feeds...");
        pDialog.show();
        HttpDataListener dataListener = new HttpDataListener(){
				
				@Override
				public void onError(Exception e) {
					pDialog.dismiss();
					e.printStackTrace();
					Toast.makeText(mActivity, getString(R.string.feeds_error), Toast.LENGTH_LONG).show();
				}
				
				@Override
				public void onDataAvailable(String response) {		
					User user = User.fromString(response);
					listItems.clear();
					int acceptedCount = user.getAcceptedRides().size();
					int offeredCount = user.getOfferedRides().size();
					int requestedCount = user.getRequestedRides().size();
					Log.d(TAG, "Accepted rides - "+ acceptedCount + " Offered rides " + offeredCount+ " requested rides " + requestedCount);
					
//					if(acceptedCount == 0 && offeredCount == 0){
//						barker.setVisibility(View.VISIBLE);
//					}
					
					if(acceptedCount > 0){
						adapter.addSeparatorItem(0);
						OfferRide tempRide = new OfferRide();
						tempRide.setSourceAddress("Accepted rides");
						listItems.add(tempRide);
						listItems.addAll(user.getAcceptedRides());
					}
					
					if(offeredCount > 0){
						adapter.addSeparatorItem(listItems.size());
						OfferRide tempRide1 = new OfferRide();
						tempRide1.setSourceAddress("Offered rides");
						listItems.add(tempRide1);
						listItems.addAll(user.getOfferedRides());
						
					}
					
					if(requestedCount > 0){
						adapter.addSeparatorItem(listItems.size());
						OfferRide tempRide1 = new OfferRide();
						tempRide1.setSourceAddress("Requested rides");
						listItems.add(tempRide1);
						listItems.addAll(user.getRequestedRides());
						
					}
					
					adapter.notifyDataSetChanged();
					pDialog.dismiss();
				}
			};
			
		Httphandler.getSharedInstance().getUser(User.getSharedInstance().getUserId(), dataListener);

    }
    
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            GcmMessage gcmMessage = intent.getExtras().getParcelable(Constants.GCM_MSG_OBJECT);
            Toast.makeText(context, gcmMessage.getTitle() + gcmMessage.getRideId(), Toast.LENGTH_LONG).show();
            fetchFeeds();
        }
    };
    
    public void openNewRideFragment(){
    	getFragmentManager()
			.beginTransaction()
			.replace(R.id.container,
				OfferRideFragment.newInstance(2, "Offer a ride"))
			.addToBackStack(null)
			.commit();
    }
}