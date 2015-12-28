package com.example.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.example.datamodel.AnotherUser;
import com.example.datamodel.OfferRide;
import com.example.feeds.CustomListAdapter.ViewHolder;
import com.example.testapp.R;
import com.example.utils.CommonUtil;
import com.example.utils.TimeHelper;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
 
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    
    private Activity mActivity;
//    private List<String> _listDataHeader; // header titles
//    private HashMap<String, List<String>> _listDataChild;
 
    List<OfferRide> rides;
    private TreeSet mSeparatorsSet = new TreeSet();
//    public CustomExpandableListAdapter(Context context, List<String> listDataHeader,
//            HashMap<String, List<String>> listChildData) {
//        this._context = context;
////        this._listDataHeader = listDataHeader;
////        this._listDataChild = listChildData;
//    }

    public CustomExpandableListAdapter(Activity activity, List<OfferRide> acceptedRides) {
        this.mActivity = activity;        
        this.rides = acceptedRides;      
    }
    
    public void addSeparatorItem(final int item) {
        mSeparatorsSet.add(item);
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
//        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                .get(childPosititon);
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
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final AnotherUser child = (AnotherUser) getChild(groupPosition, childPosition);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.feed_child_row, null);
        }
 
        TextView txtListChild = (TextView) convertView.findViewById(R.id.title);
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);
        txtListChild.setText(child.getContact_name());
        switch(child.getType()){
        	case ACCEPT:
        		layout.setBackgroundResource(R.drawable.list_child_accept);
        		break;
        	case REQUEST:
        		layout.setBackgroundResource(R.drawable.list_child_bg);
        		break;
        }
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
 
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded,
//            View convertView, ViewGroup parent) {
//        String headerTitle = (String) getGroup(groupPosition);
//        if (convertView == null) {
//            LayoutInflater infalInflater = (LayoutInflater) this._context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.feed_row, null);
//        }
// 
//        TextView lblListHeader = (TextView) convertView
//                .findViewById(R.id.title);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
//        lblListHeader.setText(headerTitle);
// 
//        return convertView;
//    }
 
    
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
        OfferRide ride = rides.get(groupPosition);
        
        
        if (convertView == null){
        	LayoutInflater inflater = (LayoutInflater) this.mActivity
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                 	titleString += " is on";
                 }
                 titleString += TimeHelper.DateToString(ride.getStartDate());
                 
                 String time= "time";
                 time = "Forward journey : " + TimeHelper.TimeToString(ride.getStartTime());
                 if(ride.isRoundTrip()){
                 	time += "\nReturn journey : " + TimeHelper.TimeToString(ride.getReturnTime());
                 }
                 
//                 Address sourceAddress = CommonUtil.getSharedInstance().getAddress(mActivity, ride.getSource()); 
//                 Address destinationAddress = CommonUtil.getSharedInstance().getAddress(mActivity, ride.getDestination());
                 
                 String placeString = "Chennai .. Madurai";
                 
//                 if(sourceAddress != null && destinationAddress != null){
//                 	placeString = sourceAddress.getLocality() + " .. " + destinationAddress.getLocality();
//                 }
                 
                 holder.titleString = titleString;
                 holder.timeString = time;
                 holder.placeString = placeString;
                 holder.yearString = "year";
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
					handleOptionImageClicked();
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
    
    
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    public void handleOptionImageClicked(){
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
								break;
							case 1:
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
}