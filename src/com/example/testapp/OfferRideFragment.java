package com.example.testapp;

import com.example.autocomplete.MyAutoComplete;
import com.example.autocomplete.PlaceArrayAdapter;
import com.example.autocomplete.MyAutoComplete.AutoCompleteListener;
import com.example.autocomplete.PlaceArrayAdapter.PlaceAutocomplete;
import com.example.datamodel.OfferRide;
import com.example.datamodel.User;
import com.example.utils.Constants;
//import com.example.dm.DummyCurrentRide;
//import com.example.dm.DummyRides;
//import com.example.dm.IMapMarker;
//import com.example.dm.Ride;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OfferRideFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    PlaceArrayAdapter mPlaceArrayAdapter;
    
    ImageButton chooseSource;
	ImageButton chooseDestination;
    AutoCompleteTextView sourceTextView, destinationTextView;
    
    Place source, destination;
    
    OfferRide mOfferRide;
    FragmentActivity mActivity;
    
    public static OfferRideFragment newInstance(int page, String title) {
    	OfferRideFragment fragmentFirst = new OfferRideFragment();
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
        mActivity = getActivity();
        
        mOfferRide = (OfferRide)getArguments().getParcelable(Constants.OFFER_RIDE_OBJECT);
        if(mOfferRide == null){
        	mOfferRide = new OfferRide();
        }
        Log.d("rideid orf", mOfferRide.getRideId());
        mOfferRide.setOfferedUserId(User.getSharedInstance().getUserId());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        
        sourceTextView = (AutoCompleteTextView)view.findViewById(R.id.sourceTextView);
        destinationTextView = (AutoCompleteTextView)view.findViewById(R.id.destinationTextView);
        final Spinner tripType = (Spinner)view.findViewById(R.id.trip_type_spinner);
        CheckBox roundTripCheckBox = (CheckBox) view.findViewById(R.id.roundTripCheckbox);
        
        sourceTextView.setText(mOfferRide.getSourceAddress());
        destinationTextView.setText(mOfferRide.getDestinationAddress());
        
        sourceTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (sourceTextView.getRight() - sourceTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                     openPlacePicker(1);
                     Toast.makeText(mActivity, "Drop the pin on a place to select it", Toast.LENGTH_LONG).show();
                     return true;
                    }
                }
                return false;
            }

			
        });
        
        destinationTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (sourceTextView.getRight() - sourceTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                     openPlacePicker(2);
                     Toast.makeText(mActivity, "Drop the pin on a place to select it", Toast.LENGTH_LONG).show();
                     return true;
                    }
                }
                return false;
            }

			
        });
        
        new MyAutoComplete(mActivity, sourceTextView, new AutoCompleteListener() {			
			@Override
			public void onItemSelected(PlaceAutocomplete selectedplace) {}

			@Override
			public void onPlaceAvailable(PlaceBuffer places) {
				final Place place = places.get(0);
			    mOfferRide.setSource(place.getLatLng());
			    mOfferRide.setSourceId(place.getId());
			    mOfferRide.setSourceAddress(place.getAddress().toString());
			}
		});
        
        new MyAutoComplete(mActivity, destinationTextView, new AutoCompleteListener() {			
			@Override
			public void onItemSelected(PlaceAutocomplete selectedplace) {}

			@Override
			public void onPlaceAvailable(PlaceBuffer places) {
				final Place place = places.get(0);
				mOfferRide.setDestination(place.getLatLng());
				mOfferRide.setDestinationId(place.getId());
				mOfferRide.setDestinationAddress(place.getAddress().toString());
				
			}
		});

        
        ArrayAdapter<CharSequence> tripTypeAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.trip_types, android.R.layout.simple_spinner_item);
        tripTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripType.setAdapter(tripTypeAdapter);
        if(mOfferRide.isRecurring()){
        	tripType.setSelection(0);
        } else {
        	tripType.setSelection(1);
        }
        
        roundTripCheckBox.setChecked(mOfferRide.isRoundTrip());
        roundTripCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mOfferRide.setRoundTrip(true);
				}else{
					mOfferRide.setRoundTrip(false);
				}
			}
       	});
        
        ((Button) view.findViewById(R.id.next)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mOfferRide.getSource() == null) {
					Toast.makeText(mActivity, "Source is not recognisable", Toast.LENGTH_LONG).show();
					return;
				} else if( mOfferRide.getDestination() == null) {
					Toast.makeText(mActivity, "Destination is not recognisable", Toast.LENGTH_LONG).show();
					return;
				}  
				
				int selectedPosition = tripType.getSelectedItemPosition();
				
				if(selectedPosition == 0){
					mOfferRide.setRecurring(true);
				}else{
					mOfferRide.setRecurring(false);
				}
			 	Fragment fragment = DateTimePickerFragment.newInstance(2, "title");
			 	
			 	Bundle bundle = new Bundle();
			 	bundle.putParcelable(Constants.OFFER_RIDE_OBJECT, mOfferRide);
			 	fragment.setArguments(bundle);
		        
			 	FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		        fragmentTransaction.replace(R.id.container, fragment);
		        fragmentTransaction.addToBackStack(null);
		        fragmentTransaction.commit();
		        
			}
		});
        /*LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
        .addApi(Places.GEO_DATA_API)
        .build();
        AutoCompleteTextView mAutocompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        
        AdapterView.OnItemClickListener mAutocompleteClickListener
        = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);
        
        //PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
        //placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        
    }
}; 
        dateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DialogFragment newFragment = new DatePickerFragment();
			    //newFragment.show(getFragmentManager(), "datePicker");
				LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
			            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
//				try {
//		            PlacePicker.IntentBuilder intentBuilder =
//		                    new PlacePicker.IntentBuilder();
//		            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
//		            Intent intent = intentBuilder.build(getActivity());
//		            startActivityForResult(intent, 1);
//		
//		        } catch (GooglePlayServicesRepairableException e) {
//		            e.printStackTrace();
//		        } catch (GooglePlayServicesNotAvailableException e) {
//		            e.printStackTrace();
//		        }
				
				
				try {			
				int PLACE_PICKER_REQUEST = 1;
				PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

				Context context = getActivity();
				startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
			} catch (GooglePlayServicesRepairableException e) {
	            e.printStackTrace();
	        } catch (GooglePlayServicesNotAvailableException e) {
	            e.printStackTrace();
	        }
			}
		});*/
        return view;
    }
    @Override
	public void onActivityResult(final int requestCode,
                                    int resultCode, Intent data) {
 
    	if(resultCode == Activity.RESULT_OK){
    		final Place place = PlacePicker.getPlace(data, getActivity());
            String attributions = PlacePicker.getAttributions(data);
            String address = place.getAddress().toString();
            String placeId = place.getId();
            
            //TO-DO : make sures that it is not possible to get address here
            if(TextUtils.isEmpty(address) || address == null){
            	MyAutoComplete autoComplete = new MyAutoComplete(getActivity(), null, new AutoCompleteListener() {
					
					@Override
					public void onPlaceAvailable(PlaceBuffer places) {
						Place place = places.get(0);
						
						if(requestCode == 1){
							String address1 = place.getAddress().toString();
		                    if(TextUtils.isEmpty(address1) || address1 == null){
		                    		address1 = place.getLatLng().toString();
		                    }
							mOfferRide.setSourceAddress(place.getAddress().toString());
			            	sourceTextView.setText(address1);
			            	
						} else if(requestCode == 2){
							String address1 = place.getAddress().toString();
		                    if(TextUtils.isEmpty(address1) || address1 == null){
		                    		address1 = place.getLatLng().toString();
		                    }
		                    mOfferRide.setDestinationAddress(place.getAddress().toString());
		                    destinationTextView.setText(address1);
						}
					}
					
					@Override
					public void onItemSelected(PlaceAutocomplete selectedplace) {}
				});
            	
            	autoComplete.getPlaceById(placeId);
            } else {
            	if(requestCode == 1){
					mOfferRide.setSourceAddress(address);
	            	sourceTextView.setText(address);
	            	
				} else if(requestCode == 2){
                    mOfferRide.setDestinationAddress(address);
                    destinationTextView.setText(address);
				}
            }
            
            if(requestCode == 1){
            	mOfferRide.setSource(place.getLatLng());
            	mOfferRide.setSourceId(place.getId());
            } else if(requestCode == 2){
            	mOfferRide.setDestination(place.getLatLng());
            	mOfferRide.setDestinationId(place.getId());
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            
    	} else {
            super.onActivityResult(requestCode, resultCode, data);
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