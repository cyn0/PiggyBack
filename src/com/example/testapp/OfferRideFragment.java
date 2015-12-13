package com.example.testapp;

import com.example.autocomplete.MyAutoComplete;
import com.example.autocomplete.PlaceArrayAdapter;
import com.example.autocomplete.MyAutoComplete.AutoCompleteListener;
import com.example.autocomplete.PlaceArrayAdapter.PlaceAutocomplete;
import com.example.datamodel.OfferRide;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class OfferRideFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    PlaceArrayAdapter mPlaceArrayAdapter;
    
    Button chooseSource, chooseDestination, done;
    AutoCompleteTextView sourceTextView, destinationTextView;
    
    Place source, destination;
    
    OfferRide mOfferRide;
    
    // newInstance constructor for creating fragment with arguments
    public static OfferRideFragment newInstance(int page, String title) {
    	OfferRideFragment fragmentFirst = new OfferRideFragment();
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
        
        mOfferRide = new OfferRide();
    }
    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        
        chooseSource = (Button) view.findViewById(R.id.button1);
        sourceTextView = (AutoCompleteTextView)view.findViewById(R.id.sourceTextView);
        chooseDestination = (Button) view.findViewById(R.id.button2);
        destinationTextView = (AutoCompleteTextView)view.findViewById(R.id.destinationTextView);
        final Spinner tripType = (Spinner)view.findViewById(R.id.trip_type_spinner);
        
        done = (Button) view.findViewById(R.id.next);
        
        
        chooseSource.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPlacePicker(1);
				Toast.makeText(getActivity(), "Drop the pin on a place to select it", Toast.LENGTH_LONG).show();
			}
		});

        chooseDestination.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPlacePicker(2);
				Toast.makeText(getActivity(), "Drop the pin on a place to select it", Toast.LENGTH_LONG).show();
			}
		});
        
        
        
        new MyAutoComplete(getActivity(), sourceTextView, new AutoCompleteListener() {			
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
        
        new MyAutoComplete(getActivity(), destinationTextView, new AutoCompleteListener() {			
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
        
        done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mOfferRide.getSource() == null) {
					Toast.makeText(getActivity(), "Source is not recognisable", Toast.LENGTH_LONG).show();
					return;
				} else if( mOfferRide.getDestination() == null) {
					Toast.makeText(getActivity(), "Destination is not recognisable", Toast.LENGTH_LONG).show();
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
		        
//				IMapMarker tempSource = new IMapMarker(source.getLatLng(), "Friend name", source.getAddress().toString());
//				IMapMarker tempDestination = new IMapMarker(destination.getLatLng(), "Friend name", destination.getAddress().toString());
//				Ride r = new Ride(tempSource, tempDestination, true);
//			
//				Intent myIntent = new Intent(getActivity(), MapActivity.class);
//				myIntent.putExtra("type", Ride.TYPE_OFFER);
//				//myIntent.putExtra("ride", r);
//				//To-do find better way to pass these params
////				myIntent.putExtra("source_latlng", source.getLatLng()); 
////				myIntent.putExtra("source_name", source.getName()); 
////				myIntent.putExtra("source_address", source.getAddress()); 
////				myIntent.putExtra("destination_latlng", destination.getLatLng()); 
////				myIntent.putExtra("destination_name", destination.getName());
////				myIntent.putExtra("destination_address", destination.getAddress());
//				
//				DummyCurrentRide.currentRide = r;
//				DummyRides.addRide(r);
//				startActivity(myIntent);
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
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView.setThreshold(3);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        Button dateButton = (Button) view.findViewById(R.id.button1);
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