package com.example.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.datamodel.AcceptRide;
import com.example.datamodel.OfferRide;
import com.example.datamodel.Ride;
import com.example.datamodel.User;
import com.example.utils.CommonUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

/**
 * Created by paln on 28/11/2015.
 */
public class Httphandler {
	//Context context;
	private String SERVER_BASE_URL = "http://ec2-54-149-149-26.us-west-2.compute.amazonaws.com:5050";
	private final String GET_RIDE = "/ride/";
	private final String GET_USER = "/user/";
    private final String POST_OFFERED_RIDE = "/ride";
    private final String POST_ACCEPT_RIDE = "/ride/accept";
    private final String POST_REQUEST_RIDE = "/ride/request";
    private final String POST_DECLINE_RIDE = "/ride/decline";
    private final String POST_REGISTER = "/register";
    private final String POST_CANCEL_REQUEST = "/ride/revertRequest";
    
	private String TAG = "Http error";
	private HttpDataListener mHttpDataListener;
	private static Httphandler mInstance;
	
	public interface HttpDataListener{
		public void onDataAvailable(String response);
		
		public void onError(Exception e);
	}
	
	public static Httphandler getSharedInstance(){
		return mInstance;
	}
	
	public static void setSharedInstance(Httphandler httphandler){
		mInstance = httphandler;
	}

	public void getRide(String ride_id, HttpDataListener dataListener){
		final String url = SERVER_BASE_URL + GET_RIDE + ride_id;
		this.mHttpDataListener = dataListener;
		new AsyncHttpTask().execute(url, "GET");
	}

    
    public void postNewRide(OfferRide mOfferRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + POST_OFFERED_RIDE;
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "POST", mOfferRide.toJSON().toString());
    }
    
    public void deleteRide(OfferRide mOfferRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + GET_USER + mOfferRide.getOfferedUserId()  + GET_RIDE  + mOfferRide.getRideId();
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "DELETE");
    }
    
    public void updateRide(OfferRide mOfferRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + GET_RIDE + mOfferRide.getRideId()  + "/update";
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "POST",  mOfferRide.toJSON().toString());
    }
    
    public void requestRide(OfferRide mRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + POST_REQUEST_RIDE;
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "POST", mRide.toJSON().toString());
    }
    
    public void cancelRequest(OfferRide mRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + POST_CANCEL_REQUEST;
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "POST", mRide.toJSON().toString());
    }
    
    public void declineRide(OfferRide mRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + POST_DECLINE_RIDE;
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "POST", mRide.toJSON().toString());
    }
    
    public void acceptRide(OfferRide mRide, HttpDataListener dataListener){
        final String url = SERVER_BASE_URL + POST_ACCEPT_RIDE;
        this.mHttpDataListener = dataListener;
        new AsyncHttpTask().execute(url, "POST", mRide.toJSON().toString());
    }
    
    public void register(HttpDataListener dataListener){
    	final String url = SERVER_BASE_URL + POST_REGISTER;
    	this.mHttpDataListener = dataListener;
    	new AsyncHttpTask().execute(url, "POST", User.getSharedInstance().toString());
    }

    public void getUser(String user_id, HttpDataListener dataListener){
		final String url = SERVER_BASE_URL + GET_USER + user_id;
		this.mHttpDataListener = dataListener;
		new AsyncHttpTask().execute(url, "GET");
	}
    
	public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

		String response;

		@Override
		protected Integer doInBackground(String... params) {
			InputStream inputStream = null;
			HttpURLConnection urlConnection = null;
			Integer result = 0;
			try {
                /* forming th java.net.URL object */
				URL url = new URL(params[0]);
				String httpMethod = params[1];
				urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
				urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
				urlConnection.setRequestProperty("Accept", "application/json");

				urlConnection.setRequestMethod(httpMethod);
                
				if(httpMethod.equals("POST")){
	                byte[] outputInBytes = params[2].getBytes("UTF-8");
	                OutputStream os = urlConnection.getOutputStream();
	                os.write( outputInBytes );
	                os.close();
				}
				
				Log.d("Request url", url.toString());
                int statusCode = urlConnection.getResponseCode();

                if (statusCode >= 200 && statusCode < 300) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = convertInputStreamToString(inputStream);
                    Log.d("response", response);
                    
                    result = 1; 
                }
                
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == 1){
				mHttpDataListener.onDataAvailable(response);
			} else {
				mHttpDataListener.onError(new Exception("Status code not OK."));
			}
		}
	}

	private String convertInputStreamToString(InputStream inputStream) throws IOException {

		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));

		String line = "";
		String result = "";

		while((line = bufferedReader.readLine()) != null){
			result += line;
		}

            /* Close Stream */
		if(null!=inputStream){
			inputStream.close();
		}

		return result;
	}

}
