package com.example.http;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.HttpConnection;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.datamodel.OfferRide;
import com.example.testapp.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.io.OutputStream;

/**
 * Created by paln on 28/11/2015.
 */
public class Httphandler {
	Context context;
	private String SERVER_BASE_URL = "http://192.168.1.4:8080";
	private final String GET_RIDE = "/ride";
    private final String POST_OFFERED_RIDE = "/ride";
    
	private String TAG = "Http error";
	private HttpDataListener mHttpDataListener;
	
	public interface HttpDataListener{
		public void onDataAvailable(String response);
		
		public void onError(Exception e);
	}
	
	public Httphandler(Context context, HttpDataListener dataListener){
		this.context = context;
		mHttpDataListener = dataListener;
	}

	public void getRide(String ride_id){
		final String url = SERVER_BASE_URL + GET_RIDE + "/" + ride_id;
		new AsyncHttpTask().execute(url, GET_RIDE, "GET");
	}

    
    public void postNewRide(OfferRide mOfferRide){
        final String url = SERVER_BASE_URL + POST_OFFERED_RIDE;
        
        new AsyncHttpTask().execute(url, POST_OFFERED_RIDE, "POST", mOfferRide.toJSON().toString());
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
				String type = params[1];
				String httpMethod = params[2];
				urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
				urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
				urlConnection.setRequestProperty("Accept", "application/json");

				urlConnection.setRequestMethod(httpMethod);
                

				if(httpMethod.equals("POST")){
	                byte[] outputInBytes = params[3].getBytes("UTF-8");
	                OutputStream os = urlConnection.getOutputStream();
	                os.write( outputInBytes );
	                os.close();
				}
                
                int statusCode = urlConnection.getResponseCode();
                /* 201 represents HTTP CREATED */
                if (statusCode >= 200 && statusCode < 300) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = convertInputStreamToString(inputStream);
                    Log.d("response", response);
                    result = 1; // Successful
                } else {
                    result = 0;
                }
                
			} catch (Exception e) {
				mHttpDataListener.onError(e);
				Log.d(TAG, e.getLocalizedMessage());
			}
			return result; //"Failed to fetch data!";
		}

		@Override
		protected void onPostExecute(Integer result) {
            /* Download complete. Lets update UI */
			if(result == 1){
				mHttpDataListener.onDataAvailable(response);
			}else{
				Log.e(TAG, "Failed to fetch data!");
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
