package com.example.feeds;

import java.util.List;

import com.example.datamodel.OfferRide;
import com.example.testapp.R;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
  
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<OfferRide> rides;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public CustomListAdapter(Activity activity, List<OfferRide> rides) {
        this.activity = activity;
        this.rides = rides;
    }
 
    @Override
    public int getCount() {
        return rides.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
 
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_row, null);
 
//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
//        NetworkImageView thumbNail = (NetworkImageView) convertView
//                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);
 
        OfferRide ride = rides.get(position);
 
//        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
         
        title.setText(ride.getSourceAddress());
         
        rating.setText("Rating");
         
        genre.setText("genreStr");
         
        // release year
        year.setText("year");
 
        return convertView;
    }
 
}