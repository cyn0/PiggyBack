package com.example.testapp;

import java.util.List;

import com.example.datamodel.User;
import com.example.http.Httphandler;
import com.example.utils.Constants;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String TAG = "MainActivity";
	public static Context mContext;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = getApplicationContext();
		startApplicationComponents();
		checkGCMRegistrationId();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		final Intent intent = getIntent();
	    final Bundle extras = intent.getExtras();

	    if(extras != null){
		    String screenName = (String) extras.get(Constants.PAGE);
		    if(screenName != null && screenName.equals(OfferRideFragment.class.getName())){
		    	FragmentManager fragmentManager = getSupportFragmentManager();
	            fragmentManager
	            	.beginTransaction()
	            	.replace(R.id.container,
	            			OfferRideFragment.newInstance(2, "Offer a ride")).commit();
		    }
	    }
//	    if (Intent.ACTION_VIEW.equals(action)) {
//	    	//You will probably want to use intent.getDataString() rather than getData() if you care about the full URL including the querystring.
//	        final List<String> segments = intent.getData().getPathSegments();
//	        if (segments.size() > 0) {
//	        	String ride_id = segments.get(0);
//	            FragmentManager fragmentManager = getSupportFragmentManager();
//	            fragmentManager
//	            	.beginTransaction()
//	            	.replace(R.id.container,
//	            			RideDetailsFragment.newInstance(2, "Offer a ride", ride_id)).commit();
//	        }
//	    }
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch(position){
			case 0:
				fragmentManager
					.beginTransaction()
					.replace(R.id.container,
							FeedsFragment.newInstance(1, "My rides")).commit();
				break;
			
			case 1:
				fragmentManager
					.beginTransaction()
					.replace(R.id.container,
							OfferRideFragment.newInstance(2, "Offer a ride")).commit();
				mTitle = getString(R.string.offer_ride);
				break;
			case 2:
				mTitle = getString(R.string.offered_rides);
				
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.my_rides);
			break;
		case 2:
			mTitle = getString(R.string.offer_ride);
			break;
		case 3:
			mTitle = getString(R.string.offered_rides);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
	
	public void startApplicationComponents(){
		Httphandler.setSharedInstance(new Httphandler());
	}
	
	private void checkGCMRegistrationId(){
		boolean registrationSucsess = User.getSharedInstance().getRegistrationStatus();
		if (!registrationSucsess) {
			Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
			startActivity(intent);
			Log.d(TAG, "Registration not done");
			
		}
//		int registeredVersion = getSharedPreferences(Constants.APP_SETTINGS, MODE_PRIVATE).getInt(Constants.APP_VERSION, Integer.MIN_VALUE);
//		int currentVersion = getAppVersion(getApplicationContext());
//		if (registeredVersion != currentVersion) {
//			Log.i(TAG, "App version changed.");
//			return "";
//		}
		
//		try {
//			PackageInfo packageInfo = context.getPackageManager()
//					.getPackageInfo(context.getPackageName(), 0);
//			return packageInfo.versionCode;
//		} catch (NameNotFoundException e) {
//			Log.d("RegisterActivity",
//					"I never expected this! Going down, going down!" + e);
//			throw new RuntimeException(e);
//		}
	}
}
