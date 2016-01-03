package com.example.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.datamodel.AnotherUser;
import com.example.datamodel.OfferRide;
import com.example.feeds.CustomListAdapter.ViewHolder;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.testapp.R;
import com.example.testapp.RideDetailsFragment;
import com.example.utils.CommonUtil;
import com.example.utils.TimeHelper;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.ParseException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
 
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    
    private Activity mActivity;
    private ExpandableListView listView;
 
    List<OfferRide> rides;
    private TreeSet mSeparatorsSet = new TreeSet();

    public CustomExpandableListAdapter(Activity activity, ExpandableListView listView, List<OfferRide> acceptedRides) {
        this.mActivity = activity;        
        this.rides = acceptedRides; 
        this.listView = listView;
    }
    
    public void addSeparatorItem(final int item) {
        mSeparatorsSet.add(item);
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
    	OfferRide ride = rides.get(groupPosition);
    	int acceptedUsersSize = ride.getAcceptedUsers().size(); 
    	if(childPosititon < acceptedUsersSize){
    		return ride.getAcceptedUsers().get(childPosititon);
    	} else {
    		return ride.getRequestedUsers().get(childPosititon - acceptedUsersSize);
    	}
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final AnotherUser child = (AnotherUser) getChild(groupPosition, childPosition);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.feed_child_row, null);
        }
 
        TextView txtListChild = (TextView) convertView.findViewById(R.id.title);
        final LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout);
        final ImageView accept = (ImageView) convertView.findViewById(R.id.accept);
        ImageView reject = (ImageView) convertView.findViewById(R.id.reject);
        
        String title = child.getContact_name();
        if(TextUtils.isEmpty(title)){
        	title = child.getPhone_number();
        }
        txtListChild.setText(title);
        
        if(rides.get(groupPosition).getAcceptedUsers().contains(child)){
        	accept.setVisibility(View.GONE);
    		layout.setBackgroundResource(R.drawable.list_child_accept);
        } else{
        	accept.setVisibility(View.VISIBLE);
    		layout.setBackgroundResource(R.drawable.list_child_bg);
        }
        
        accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OfferRide ride = rides.get(groupPosition);
				ride.setUserUserId(child.getId());
				handleAcceptDeclineClicked(groupPosition, childPosition, true);
			}
		});
        
        reject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OfferRide ride = rides.get(groupPosition);
				ride.setUserUserId(child.getId());
				handleAcceptDeclineClicked(groupPosition, childPosition, false);
			}
		});
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
//        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                .size();
    	
    	OfferRide ride = rides.get(groupPosition);
    	return ride.getRequestedUsers().size() + ride.getAcceptedUsers().size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
//        return this._listDataHeader.get(groupPosition);
    	return rides.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return rides.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    @Override
	public int getGroupType(int groupPosition) {
    	return mSeparatorsSet.contains(groupPosition) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

	@Override
	public int getGroupTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
//    public View getView(int position, View convertView, ViewGroup parent) {
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

    	int type = getGroupType(groupPosition);
        ViewHolder holder = null;
        final OfferRide ride = rides.get(groupPosition);
        
        
        if (convertView == null){
        	LayoutInflater inflater = (LayoutInflater) this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	holder = new ViewHolder();
        	switch (type) {
            case TYPE_ITEM:
            	convertView = inflater.inflate(R.layout.feed_row, null);
            	holder.title = (TextView) convertView.findViewById(R.id.title);
            	holder.time = (TextView) convertView.findViewById(R.id.rating);
            	holder.place = (TextView) convertView.findViewById(R.id.genre);
            	holder.year = (TextView) convertView.findViewById(R.id.releaseYear);
            	holder.option = (ImageView) convertView.findViewById(R.id.imageView1);
            	 String titleString = "Ride";
                 if(ride.isRecurring()){
                 	titleString += " starting from ";
                 } else {
                 	titleString += " is on ";
                 }
                 titleString += TimeHelper.DateToString(ride.getStartDate());
                 
                 String time= "time";
                 time = "Forward journey : " + TimeHelper.TimeToString(ride.getStartTime());
                 if(ride.isRoundTrip()){
                 	time += "\nReturn journey : " + TimeHelper.TimeToString(ride.getReturnTime());
                 }
                 
//                 Address sourceAddress = CommonUtil.getSharedInstance().getAddress(mActivity, ride.getSource()); 
//                 Address destinationAddress = CommonUtil.getSharedInstance().getAddress(mActivity, ride.getDestination());
                 
                 String placeString = "";
                 

//                 if(sourceAddress != null && destinationAddress != null){
//                 	placeString = sourceAddress.getLocality() + " .. " + destinationAddress.getLocality();
//                 }
                 String sourceAddress = ride.getSourceAddress();
                 String destinationAddress = ride.getDestinationAddress();
                 if(sourceAddress != null && destinationAddress != null){
	                 if(sourceAddress.contains(",")){
	                	 placeString = ride.getSourceAddress().split(",")[0];
	                 } else {
	                	 placeString = ride.getSourceAddress();
	                 }
	                 placeString = placeString + " .. ";
	                 if(destinationAddress.contains(",")){
	                	 placeString += destinationAddress.split(",")[0];
	                 } else {
	                	 placeString += destinationAddress;
	                 }
                 }
                 String text = "";
                 int acceptedSize = ride.getAcceptedUsers().size();
                 int requestedSize = ride.getRequestedUsers().size();
                 if(requestedSize > 0){
                	 text = "Request:1 ";
                 }
                 if(acceptedSize > 0){
                	 text = text + "Accepted:1";
                 }
                 holder.titleString = titleString;
                 holder.timeString = time;
                 holder.placeString = placeString;
                 holder.yearString = text;
                 break;
            case TYPE_SEPARATOR:
            	convertView = inflater.inflate(R.layout.separator_row, null);
            	holder.title = (TextView) convertView.findViewById(R.id.title);
            	holder.titleString = ride.getSourceAddress();
                break;
        	}
        	convertView.setTag(holder);
        } else {
        	holder = (ViewHolder)convertView.getTag();
        }
            
        switch (type) {
        case TYPE_ITEM:
        	 holder.title.setText(holder.titleString);
             holder.time.setText(holder.timeString);
             holder.place.setText(holder.placeString);
             holder.year.setText(holder.yearString);
             holder.option.setImageResource(R.drawable.ic_drawer);
             holder.option.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleOptionImageClicked(ride);
				}
			});
            break;
        case TYPE_SEPARATOR:
        	holder.title.setText(holder.titleString);
        	break;
    	}
        
//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
//        NetworkImageView thumbNail = (NetworkImageView) convertView
//                .findViewById(R.id.thumbnail);
        
 
//        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        
       
 
        return convertView;
    }
    
    class ViewHolder{
    	TextView title, time, place, year; 
        ImageView option;
        String titleString, timeString, placeString, yearString;
        
    }
    
    class ChildViewHolder{
    	TextView title, time, place, year; 
        ImageView accept, decline;
        LinearLayout layout;
        
    }
    
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    public void handleOptionImageClicked(final OfferRide ride){

    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(mActivity);
	        builderSingle.setTitle("Options");
	        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
	                mActivity,
	                android.R.layout.select_dialog_item);
	        
	 	        	arrayAdapter.add("Manage");
	 	        	arrayAdapter.add("Share");
	        builderSingle.setNegativeButton("cancel",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });

	        builderSingle.setAdapter(arrayAdapter,
	                new DialogInterface.OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        switch (which) {
							case 0:
								FragmentManager fragmentManager = ((FragmentActivity)mActivity).getSupportFragmentManager();
								fragmentManager
									.beginTransaction()
									.replace(R.id.container,
										RideDetailsFragment.newInstance(2, "Ride details", ride.getRideId(), ride, null))
									.addToBackStack(null)
									.commit();
								break;
							case 1:
								CommonUtil.getSharedInstance().shareMessage(ride.getRideId(), mActivity);
								break;
							case 2:
								break;
							case 3:
								break;
							}
	                        
	                    }
	                });
	        builderSingle.show();

    }
    
    
    public void handleAcceptDeclineClicked(final int groupPosition, final int childPosition, final boolean accept){
    	final ProgressDialog pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Updating...");
        pDialog.show();
        final OfferRide ride = rides.get(groupPosition);
        
        final AnotherUser child = (AnotherUser) getChild(groupPosition, childPosition);
        HttpDataListener dataListener = new HttpDataListener() {
			
			@Override
			public void onError(Exception e) {
				pDialog.dismiss();
				Toast.makeText(mActivity, mActivity.getString(R.string.server_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				pDialog.dismiss();
				try{
					JSONObject jsonResponse = new JSONObject(response);
					boolean success = jsonResponse.getBoolean("success");
					if(success){
						if(accept){
							ride.getRequestedUsers().remove(child);
							ride.getAcceptedUsers().add(child);
							notifyDataSetChanged();
							Toast.makeText(mActivity, mActivity.getString(R.string.accept_success), Toast.LENGTH_LONG).show();
//							layout.setBackgroundResource(R.drawable.list_child_accept);
//							acceptImageView.setVisibility(View.GONE);
						} else {
							ride.getRequestedUsers().remove(child);
							ride.getAcceptedUsers().remove(child);
							notifyDataSetChanged();
//							CustomExpandableListAdapter.this.listView.expandGroup(groupPosition);
							Toast.makeText(mActivity, mActivity.getString(R.string.reject_success), Toast.LENGTH_LONG).show();
//							layout.setBackgroundResource(R.drawable.list_child_bg);
						}
					} else {
						
						if(accept){
							Toast.makeText(mActivity, mActivity.getString(R.string.accept_error), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mActivity, mActivity.getString(R.string.reject_error), Toast.LENGTH_LONG).show();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		};
		if(accept){
			Httphandler.getSharedInstance().acceptRide(ride, dataListener);
		} else {
			Httphandler.getSharedInstance().declineRide(ride, dataListener);
		}
    }
}