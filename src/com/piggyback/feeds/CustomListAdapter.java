package com.example.feeds;

import java.util.List;
import java.util.TreeSet;

import com.example.datamodel.OfferRide;
import com.example.testapp.R;
import com.example.utils.CommonUtil;
import com.example.utils.TimeHelper;
 
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
  
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<OfferRide> rides;
    
    
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private TreeSet mSeparatorsSet = new TreeSet();
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public CustomListAdapter(Activity activity, List<OfferRide> acceptedRides) {
        this.activity = activity;
        
        this.rides = acceptedRides;
        
    }
 
    public void addSeparatorItem(final int item) {
        mSeparatorsSet.add(item);
    }
    
    @Override
    public int getCount() {
        return rides.size();
    }
 
    //return how many different view layouts
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    
    @Override
    public Object getItem(int location) {
        return rides.get(location);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.d("getitemtypeview","");
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int type = getItemViewType(position);
        ViewHolder holder = null;
        OfferRide ride = rides.get(position);
        
        
        if (convertView == null){
        	holder = new ViewHolder();
        	switch (type) {
            case TYPE_ITEM:
            	convertView = inflater.inflate(R.layout.feed_row, null);
            	holder.title = (TextView) convertView.findViewById(R.id.title);
            	holder.time = (TextView) convertView.findViewById(R.id.rating);
            	holder.place = (TextView) convertView.findViewById(R.id.genre);
            	holder.year = (TextView) convertView.findViewById(R.id.releaseYear);
            	
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
                 
                 Address sourceAddress = CommonUtil.getSharedInstance().getAddress(activity, ride.getSource()); 
                 Address destinationAddress = CommonUtil.getSharedInstance().getAddress(activity, ride.getDestination());
                 
                 String placeString = "";
                 
                 if(sourceAddress.getLocality() != null && destinationAddress != null){
                 	placeString = sourceAddress.getLocality() + " .. " + destinationAddress.getLocality();
                 }
                 
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
    	TextView title;
        TextView time;
        TextView place;
        TextView year; 
        
        String titleString, timeString, placeString, yearString;
        
    }
 
}