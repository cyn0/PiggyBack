package com.example.testapp;


import org.json.JSONException;
import org.json.JSONObject;

import com.example.datamodel.AnotherUser;
import com.example.datamodel.Message;
import com.example.datamodel.OfferRide;
import com.example.datamodel.User;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.CommonUtil;
import com.example.utils.Constants;
import com.example.utils.TimeHelper;
import com.example.datamodel.User.UserType;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class UserDetailFragment extends Fragment {
    final String TAG = "UserDetailFragment";
	// Store instance variables
    private String title, ride_id;
    private int page;
    static OfferRide mOfferRide = null;
    static AnotherUser mUser;
    private FragmentActivity mFragmentActivity;
    
    TextView sourceTextView, destinationTextView, userNametTextView, notificationTextView;
    Button negativeButton, positiveButton;
    LinearLayout rideDetailesView, userDetailsview;
    UserType userType;
    static Message gcmMessage;
    View mView;
    // newInstance constructor for creating fragment with arguments
    public static UserDetailFragment newInstance(int page, String title, Message gcm, AnotherUser anotherUser, OfferRide ride) {
    	UserDetailFragment fragmentFirst = new UserDetailFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
//        args.putParcelable(Constants.SOME_NAME_FOR_GCM_OBJECT, gcmMessage);
        fragmentFirst.setArguments(args);
        gcmMessage = gcm;
        mUser = anotherUser;
        mOfferRide = ride;
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
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        
        positiveButton = (Button) view.findViewById(R.id.share);
        rideDetailesView = (LinearLayout) view.findViewById(R.id.rideDetailsLayout);
        userDetailsview = (LinearLayout) view.findViewById(R.id.userDetailsLayout);
        negativeButton = (Button) view.findViewById(R.id.negative);
        sourceTextView = (TextView) view.findViewById(R.id.source);
        destinationTextView = (TextView) view.findViewById(R.id.destination);
        userNametTextView  = (TextView) view.findViewById(R.id.userName);
        notificationTextView  = (TextView) view.findViewById(R.id.notification);
        
        if(gcmMessage != null){
        	fetchRideData(gcmMessage.getRideId());
        	fetchUserData();
        	Log.d("GCM message", gcmMessage.toString());
        	notificationTextView.setText(gcmMessage.getMessage());
        } else {
        	notificationTextView.setVisibility(View.GONE);
	        if (mUser == null){
	        	fetchUserData();
	        } else {
	        	setUserViews();
	        }
	        if (mOfferRide == null){
	        	//fetchRideData(rideId);
	        } else {
	        	setRideViews();
	        }
        }
        mView = view;
        return view;
    }
    
    public void setRideViews(){
    	sourceTextView.setText(mOfferRide.getSourceAddress());
    	destinationTextView.setText(mOfferRide.getDestinationAddress());
    	
//    	if(TextUtils.isEmpty(mOfferRide.getRideId())){
//    		positiveButton.setVisibility(View.GONE);
//    	} else {
//    		positiveButton.setVisibility(View.VISIBLE);
//    	}
//    	switch(userType){
//    		case OFFERER:
//    			//have i posted??
//    			if(mOfferRide.getRideId() == null || mOfferRide.getRideId().equals("")){
//    				negativeButton.setVisibility(View.GONE);
//    			} else {
//    				negativeButton.setText("DELETE RIDE");
//    			}
//    			break;
//    		case REQUESTER:
//    			if(CommonUtil.getSharedInstance().HaveIRequested(mOfferRide, mUser)
//    					|| CommonUtil.getSharedInstance().HaveIAccepted(mOfferRide, mUser)){
//    				negativeButton.setText("DELETE REQUEST");
//    			} else {
//    				negativeButton.setVisibility(View.GONE);
//    			}
//    			break;
//    	}
//        negativeButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				switch(userType){
//	    		case OFFERER:
//	    			deleteRide();
//	    			break;
//	    		case REQUESTER:
//	    			cancelRequest();
//	    			break;
//	    	}
//				
//			}
//		});
//        
//        
//        positiveButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				CommonUtil.getSharedInstance().shareMessage(mOfferRide.getRideId(), mFragmentActivity);
//			}
//		});
//        
    	rideDetailesView.setVisibility(View.VISIBLE);
    	rideDetailesView.animate()
    		.translationY(0)
	        .alpha(1.0f)
	        .setDuration(300);
	        
    	
    }
    
    private void setUserViews(){
    	String phoneNumber = mUser.getPhone_number();
    	String contactName = CommonUtil.getSharedInstance().getContactName(phoneNumber);
    	if(TextUtils.isEmpty(contactName)){
    		userNametTextView.setText(phoneNumber);
    	} else {
    		userNametTextView.setText(contactName);
    	}
    	userDetailsview.setVisibility(View.VISIBLE);
    	userDetailsview.animate()
    		.translationY(0)
	        .alpha(1.0f)
	        .setDuration(300);
	    
    }
    public void fetchUserData(){
        HttpDataListener dataListener = new HttpDataListener(){
				
				@Override
				public void onError(Exception e) {
					Toast.makeText(mFragmentActivity, getString(R.string.feeds_error), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				
				@Override
				public void onDataAvailable(String response) {		
					mUser = AnotherUser.fromString(response);
					setUserViews();
				}
			};
		String userId = null;
		switch (gcmMessage.getMsgType()) {
			case TYPE_RIDE_ACCEPTED:
			case TYPE_RIDE_DELETED:
			case TYPE_RIDE_DECLINED:
				userId = gcmMessage.getOfferedUserId();
				break;
				
			case TYPE_RIDE_REQUESTED:
			case TYPE_RIDE_REQUEST_CANCELLED:
				userId = gcmMessage.getUserUserId();
				break;
	
			default:
				int i = 1/0;
				break;
		}
		Httphandler.getSharedInstance().getUser(userId, dataListener);

    }
    public void fetchRideData(String rideId){
    	HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {		
				Toast.makeText(mFragmentActivity, getString(R.string.feeds_error), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
			@Override
			public void onDataAvailable(String response) {
				mOfferRide = OfferRide.fromString(response);
				setRideViews();
			}
        };
        
		Httphandler.getSharedInstance().getRide(rideId, mDataListener);
    }
    
    public void deleteRide(){
    	final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
    	HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {		
				pDialog.dismiss();
				Toast.makeText(mFragmentActivity, getString(R.string.server_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				pDialog.dismiss();
				try{
					JSONObject jsonResponse = new JSONObject(response);
					boolean success = jsonResponse.getBoolean("success");
					if(success){
						Toast.makeText(mFragmentActivity, getString(R.string.delete_success), Toast.LENGTH_LONG).show();
						getFragmentManager().popBackStackImmediate();
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
    
    public void cancelRequest(){
    	final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
    	HttpDataListener mDataListener = new HttpDataListener() {				
			@Override
			public void onError(Exception e) {		
				pDialog.dismiss();
				Toast.makeText(mFragmentActivity, getString(R.string.server_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				pDialog.dismiss();
				try{
					JSONObject jsonResponse = new JSONObject(response);
					boolean success = jsonResponse.getBoolean("success");
					if(success){
						Toast.makeText(mFragmentActivity, getString(R.string.cancel_success), Toast.LENGTH_LONG).show();
						negativeButton.setVisibility(View.GONE);
						getFragmentManager().popBackStackImmediate();
					} else {
						Toast.makeText(mFragmentActivity, getString(R.string.cancel_error), Toast.LENGTH_LONG).show();
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
        };        
		Httphandler.getSharedInstance().cancelRequest(mOfferRide, mDataListener);
    }
    
    private void reloadFragment(){
    	getFragmentManager()
    		.beginTransaction()
    		.detach(this)
    		.attach(this)
    		.commit();
    }
}