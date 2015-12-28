package com.example.testapp;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.datamodel.OfferRide;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.TimeHelper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RideDetailsFragment extends Fragment {
    // Store instance variables
    private String title, ride_id;
    private int page;
    private OfferRide mOfferRide;
    private FragmentActivity mFragmentActivity;
    
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
        
        mFragmentActivity = getActivity();
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_details, container, false);
        
        Button done = (Button) view.findViewById(R.id.next);
        Button negativeButton = (Button) view.findViewById(R.id.negative);
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
        
        negativeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteRide();
			}
		});
        fetchData();
		
        return view;
    }
    
    public void setViews(){
    	sourceTextView.setText(mOfferRide.getSourceAddress());
    	destinationTextView.setText(mOfferRide.getDestinationAddress());
    	isRecurringTextView.setText("Recurring : " + mOfferRide.isRecurring());
    	isRoundTripTextView.setText("Round Trip : " + mOfferRide.isRoundTrip());
    	
    	long millis;
    	millis = mOfferRide.getStartTime();
    	String dateString;
    	
    	dateString = TimeHelper.TimeToString(millis);
    	forwardStartTimeTextView.setText("Forward journey start time : " + dateString);
    	
    	if(mOfferRide.isRoundTrip()){
    		millis = mOfferRide.getReturnTime();
	    	dateString = TimeHelper.TimeToString(millis);
	    	returnStartTimeTextView.setText("Return journey start time : " + dateString);
    	}else {
    		returnStartTimeTextView.setVisibility(View.GONE);
    	}
    	
    	millis = mOfferRide.getStartDate();
    	dateString = TimeHelper.DateToString(millis);
    	tripStartDateTextView.setText("Trip start date : " + dateString);
    	
    	boolean isPriced = mOfferRide.isPriced();
    	if(isPriced){
    		priceTextView.setText("Price : " + mOfferRide.getPrice());
    	}else{
    		priceTextView.setText("Price : Free of charge");
    	}
    }
    
    public void fetchData(){
    	final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
    	HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {		
				pDialog.dismiss();
				Toast.makeText(mFragmentActivity, getString(R.string.feeds_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				mOfferRide = OfferRide.fromString(response);
				setViews();
				pDialog.dismiss();
			}
        };
        
		Httphandler.getSharedInstance().getRide(ride_id, mDataListener);
    }
    
    public void deleteRide(){
    	final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
    	HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {		
				pDialog.dismiss();
				Toast.makeText(mFragmentActivity, getString(R.string.feeds_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				pDialog.dismiss();
				try{
					JSONObject jsonResponse = new JSONObject(response);
					boolean success = jsonResponse.getBoolean("success");
					if(success){
						Toast.makeText(mFragmentActivity, getString(R.string.delete_success), Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mFragmentActivity, getString(R.string.delete_error), Toast.LENGTH_LONG).show();
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
        };        
		Httphandler.getSharedInstance().deleteRide(mOfferRide, mDataListener);
    }
}