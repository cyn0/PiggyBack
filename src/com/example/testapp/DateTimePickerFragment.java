package com.example.testapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.autocomplete.PlaceArrayAdapter;
import com.example.datamodel.OfferRide;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.TimeHelper;
//import com.example.dm.DummyCurrentRide;
//import com.example.dm.DummyRides;
//import com.example.dm.IMapMarker;
//import com.example.dm.Ride;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class DateTimePickerFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    PlaceArrayAdapter mPlaceArrayAdapter;
    
    Button  done;
    Button setStartTimeEditText;
	Button setStartDateEditText;
	Button setReturnTimeEditText;
	CheckBox roundTripCheckBox, chargeCheckBox;
	EditText chargeEditText;
	
    Place source, destination;
    
    OfferRide mOfferRide;
    
    // newInstance constructor for creating fragment with arguments
    public static DateTimePickerFragment newInstance(int page, String title) {
    	DateTimePickerFragment fragmentFirst = new DateTimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        
        mOfferRide = (OfferRide)getArguments().getParcelable(Constants.OFFER_RIDE_OBJECT);
        
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recurring_trip_date, container, false);
        
        setStartTimeEditText = (Button) view.findViewById(R.id.setStartTime);
        setReturnTimeEditText = (Button) view.findViewById(R.id.setReturnTime);
        setStartDateEditText = (Button) view.findViewById(R.id.setStartDate);
        done = (Button) view.findViewById(R.id.next);
        chargeCheckBox = (CheckBox) view.findViewById(R.id.chargeCheckbox);
        roundTripCheckBox = (CheckBox) view.findViewById(R.id.roundTripCheckbox);
        chargeEditText = (EditText) view.findViewById(R.id.chargeEditText);
        
        if(mOfferRide.isPriced()){
			chargeEditText.setVisibility(View.VISIBLE);
		}else{
			chargeEditText.setVisibility(View.INVISIBLE);
		}
        
        if(mOfferRide.isRoundTrip()){
        	setReturnTimeEditText.setVisibility(View.VISIBLE);
		}else{
			setReturnTimeEditText.setVisibility(View.INVISIBLE);
		}
        
        setStartTimeEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    	Date d = new Date(0,0,0,selectedHour, selectedMinute);
                    	mOfferRide.setStartTime(d);
                    	
                    	String time = TimeHelper.TimeToString(d);
                    	setStartTimeEditText.setText( time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
				
			}
		});
        
        setReturnTimeEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    	Date d = new Date(0,0,0,selectedHour, selectedMinute);
                    	mOfferRide.setReturnTime(d);
                    	
                    	String time = TimeHelper.TimeToString(d);
                    	setReturnTimeEditText.setText(time);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
				
			}
		});
        setStartDateEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment();
			    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");				
			}
		});
        
        chargeCheckBox.setChecked(!mOfferRide.isPriced());
        chargeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					chargeEditText.setVisibility(View.INVISIBLE);
					mOfferRide.setPrice(0);
					mOfferRide.setPriced(false);
				}else{
					chargeEditText.setVisibility(View.VISIBLE);
					mOfferRide.setPriced(true);
				}
			}
       	});
        
        roundTripCheckBox.setChecked(mOfferRide.isRoundTrip());
        roundTripCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					setReturnTimeEditText.setVisibility(View.VISIBLE);
					mOfferRide.setRoundTrip(true);
				}else{
					setReturnTimeEditText.setVisibility(View.INVISIBLE);
					mOfferRide.setRoundTrip(false);
				}
			}
       	});
        
        done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

//				if(mOfferRide.getStartDate() == null){
//	        		Toast.makeText(getActivity(), "Please set trip start date", Toast.LENGTH_LONG).show();
//	        		return;
//	        	} else if(mOfferRide.getStartTime() == null){
//	        		Toast.makeText(getActivity(), "Please set start time for forward journey", Toast.LENGTH_LONG).show();
//	        		return;
//	        	} else if(mOfferRide.isRoundTrip() && mOfferRide.getReturnTime() == null){
//	        		Toast.makeText(getActivity(), "Please set start time for return journey", Toast.LENGTH_LONG).show();
//	        		return;
//	        	} else if(mOfferRide.isPriced()){
//	        		String charge = chargeEditText.getText().toString();
//			    	if(TextUtils.isEmpty(charge)){
//			    		Toast.makeText(getActivity(), "Please set price for the trip", Toast.LENGTH_LONG).show();
//		        		return;
//			    	}
//			    	try{
//			    		double charge1 = Double.parseDouble(charge);
//			    		if(charge1 <=0){
//			    			throw new Exception("price less than zero");
//			    		} else {
//			    			mOfferRide.setPrice(charge1);
//			    		}
//			    	}catch(Exception e){
//			    		Log.e("exception", e.getLocalizedMessage());
//			    		Toast.makeText(getActivity(), "Please set valid price for the trip", Toast.LENGTH_LONG).show();
//			    		return;
//			    	}
//			    	
//	        	} 
//	        	
//				HttpDataListener mDataListener = new HttpDataListener() {				
//					@Override
//					public void onError(Exception e) {			
//						Toast.makeText(getActivity(), "Something went wrong. Please try after some time", Toast.LENGTH_LONG).show();
//					}
//					
//					@Override
//					public void onDataAvailable(String response) {
//						try{
//								JSONObject jsonResponse = new JSONObject(response);
//								boolean success = jsonResponse.getBoolean("success");
//								if(success){
//									String ride_id = jsonResponse.getString("ride_id");
//									String share_text = getString(R.string.share_text);
//									Intent sendIntent = new Intent();
//									sendIntent.setAction(Intent.ACTION_SEND);
//									
//									sendIntent.putExtra(Intent.EXTRA_TEXT, share_text +" http://ShareDrive.com/" + ride_id);
//									sendIntent.setType("text/plain");
//									startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_with)));
//									
//								}
//						}catch (JSONException e){
//							e.printStackTrace();
//						}
//					}
//				};
//				
//				Httphandler httphandler = new Httphandler(getActivity(), mDataListener);
//				httphandler.postNewRide(mOfferRide);
				
				Intent myIntent = new Intent(getActivity(), MapActivity.class);
				startActivity(myIntent);
			}
		});
        return view;
    }
    
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			
			//TO-DO : research on storing date
			Date d = new Date(year-1900, month, day);
			
			String  date = TimeHelper.DateToString(d);
			setStartDateEditText.setText(date);
			mOfferRide.setStartDate(d);
		}
	}
    
    
    
    public void openPlacePicker(int type){
    	try {			
			int PLACE_PICKER_REQUEST = type;
			PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

			Context context = getActivity();
			startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
		} catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    
}