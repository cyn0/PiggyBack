package com.example.testapp;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.datamodel.OfferRide;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.TimeHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RideDetailsFragment extends Fragment {
    // Store instance variables
    private String title, ride_id;
    private int page;
    private OfferRide mOfferRide;
    
    Button done;
    TextView sourceTextView, destinationTextView, forwardStartTimeTextView, returnStartTimeTextView;
    TextView tripStartDateTextView, isRecurringTextView, isRoundTripTextView, priceTextView;
    
    // newInstance constructor for creating fragment with arguments
    public static RideDetailsFragment newInstance(int page, String title, String ride_id) {
    	RideDetailsFragment fragmentFirst = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putString("ride_id", ride_id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        ride_id = getArguments().getString("ride_id");
        
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_details, container, false);
        
        done = (Button) view.findViewById(R.id.next);
        
        sourceTextView = (TextView) view.findViewById(R.id.source);
        destinationTextView = (TextView) view.findViewById(R.id.destination);
        forwardStartTimeTextView = (TextView) view.findViewById(R.id.forwardStartTime);
        returnStartTimeTextView = (TextView) view.findViewById(R.id.returnStartTime);
        isRecurringTextView  = (TextView) view.findViewById(R.id.isRecurring);
        isRoundTripTextView  = (TextView) view.findViewById(R.id.isRoundTrip);
        tripStartDateTextView  = (TextView) view.findViewById(R.id.tripStartDate);
        priceTextView = (TextView) view.findViewById(R.id.price);
        
        
        done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
        
        HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {			
			}
			
			@Override
			public void onDataAvailable(String response) {
				mOfferRide = OfferRide.fromString(response);
				setViews();
			}
        };
        Httphandler httphandler = new Httphandler(getActivity(), mDataListener);
		httphandler.getRide(ride_id);
		
		
        return view;
    }
    
    public void setViews(){
    	sourceTextView.setText(mOfferRide.getSourceAddress());
    	destinationTextView.setText(mOfferRide.getDestinationAddress());
    	isRecurringTextView.setText("Recurring : " + mOfferRide.isRecurring());
    	isRoundTripTextView.setText("Round Trip : " + mOfferRide.isRoundTrip());
    	
    	Date date;
    	date = mOfferRide.getStartTime();
    	String dateString;
    	
    	dateString = TimeHelper.TimeToString(date);
    	forwardStartTimeTextView.setText("Forward journey start time : " + dateString);
    	
    	if(mOfferRide.isRoundTrip()){
	    	date = mOfferRide.getReturnTime();
	    	dateString = TimeHelper.TimeToString(date);
	    	returnStartTimeTextView.setText("Return journey start time : " + dateString);
    	}else {
    		returnStartTimeTextView.setVisibility(View.GONE);
    	}
    	
    	date = mOfferRide.getStartDate();
    	dateString = TimeHelper.DateToString(date);
    	tripStartDateTextView.setText("Trip start date : " + dateString);
    	
    	boolean isPriced = mOfferRide.isPriced();
    	if(isPriced){
    		priceTextView.setText("Price : " + mOfferRide.getPrice());
    	}else{
    		priceTextView.setText("Price : Free of charge");
    	}
    }
}