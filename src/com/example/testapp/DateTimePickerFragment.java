package com.example.testapp;

import java.util.Calendar;
import java.util.Date;

import com.example.autocomplete.PlaceArrayAdapter;
import com.example.datamodel.OfferRide;
import com.example.utils.Constants;
import com.example.utils.TimeHelper;
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
import android.os.Parcelable;
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
import android.widget.LinearLayout;
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
	CheckBox chargeCheckBox;
	EditText chargeEditText;
	
    Place source, destination;
    
    OfferRide mOfferRide;
    Activity mActivity;
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
        mActivity = getActivity();
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recurring_trip_date, container, false);
        
        LinearLayout returnTimeLayout = (LinearLayout) view.findViewById(R.id.returnTimeLayout);
        setStartTimeEditText = (Button) view.findViewById(R.id.setStartTime);
        setReturnTimeEditText = (Button) view.findViewById(R.id.setReturnTime);
        setStartDateEditText = (Button) view.findViewById(R.id.setStartDate);
        done = (Button) view.findViewById(R.id.next);
        chargeCheckBox = (CheckBox) view.findViewById(R.id.chargeCheckbox);
        chargeEditText = (EditText) view.findViewById(R.id.chargeEditText);
        Log.d("rideid dtp", mOfferRide.getRideId());
        
        long time = System.currentTimeMillis();
        if(mOfferRide.getStartDate() == 0){
        	mOfferRide.setStartDate(time);
        }
        if(mOfferRide.getStartTime() == 0){
        	mOfferRide.setStartTime(time);
        }
        if(mOfferRide.getReturnTime() == 0){
        	mOfferRide.setReturnTime(time);
        }
        
        setStartDateEditText.setText(TimeHelper.DateToString(mOfferRide.getStartDate()));
        setStartTimeEditText.setText(TimeHelper.TimeToString(mOfferRide.getStartTime()));
        setReturnTimeEditText.setText(TimeHelper.TimeToString(mOfferRide.getReturnTime()));
        
        if(mOfferRide.isPriced()){
			chargeEditText.setVisibility(View.VISIBLE);
			chargeEditText.setText(mOfferRide.getPrice() +"");
		}else{
			chargeEditText.setVisibility(View.INVISIBLE);
		}
        
        if(mOfferRide.isRoundTrip()){
        	returnTimeLayout.setVisibility(View.VISIBLE);
		}else{
			returnTimeLayout.setVisibility(View.GONE);
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
                    	mOfferRide.setStartTime(d.getTime());
                    	
                    	String time = TimeHelper.TimeToString(d.getTime());
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
                    	mOfferRide.setReturnTime(d.getTime());
                    	
                    	String time = TimeHelper.TimeToString(d.getTime());
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
				} else {
					chargeEditText.setVisibility(View.VISIBLE);
					mOfferRide.setPriced(true);
					chargeEditText.setText(mOfferRide.getPrice()+"");
				}
			}
       	});
        
        done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOfferRide.isPriced()){
	        		String charge = chargeEditText.getText().toString();
			    	if(TextUtils.isEmpty(charge)){
			    		Toast.makeText(getActivity(), "Please set price for the trip", Toast.LENGTH_LONG).show();
		        		return;
			    	}
			    	try{
			    		double charge1 = Double.parseDouble(charge);
			    		if(charge1 <=0){
			    			throw new Exception("price less than zero");
			    		} else {
			    			mOfferRide.setPrice(charge1);
			    		}
			    	}catch(Exception e){
			    		Log.e("exception", e.getLocalizedMessage());
			    		Toast.makeText(getActivity(), "Please set valid price for the trip", Toast.LENGTH_LONG).show();
			    		return;
			    	}
			    	
	        	} 
	        	
				
				
				Intent myIntent = new Intent(getActivity(), MapActivity.class);
				myIntent.putExtra(Constants.OFFER_RIDE_OBJECT, (Parcelable) mOfferRide);
				Log.d("rideid dtp", mOfferRide.getRideId());
				startActivity(myIntent);
				if(mActivity instanceof MapActivity){
					mActivity.finish();
				}
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
			
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			String  date = TimeHelper.DateToString(c.getTimeInMillis());
			setStartDateEditText.setText(date);
			mOfferRide.setStartDate(c.getTimeInMillis());
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