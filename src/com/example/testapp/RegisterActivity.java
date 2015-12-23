package com.example.testapp;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.datamodel.User;
import com.example.http.Httphandler;
import com.example.http.Httphandler.HttpDataListener;
import com.example.utils.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterActivity extends Activity {

	Button btnGCMRegister;
	GoogleCloudMessaging gcm;
	Context context;

	public static final String REG_ID = "regId";
	private Boolean registerationSuccess = false;
	static final String TAG = "Register Activity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		context = getApplicationContext();
	
		if (isRegistrationSuccess()) {
			directToMainActivity();
		}
		btnGCMRegister = (Button) findViewById(R.id.btnGCMRegister);
		btnGCMRegister.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				/*if (TextUtils.isEmpty(User.getSharedInstance().getPhoneNumber(context))) {
					//verifyPhone()
				} else */if (TextUtils.isEmpty(User.getSharedInstance().getGCMRegistrationId())) {
					registerGCM();
				} else if(!isRegistrationSuccess()){
					postRegistrationDataToServer();
				} else {
					directToMainActivity();
				}
				
			}
		});		
}
	
	public void registerGCM() {
		gcm = GoogleCloudMessaging.getInstance(this);
		registerInBackground();
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (isRegistrationSuccess()) {
			directToMainActivity();
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			String regId = "";
			@Override
			protected String doInBackground(Void... params) {
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Constants.GOOGLE_PROJECT_ID);
					
					Log.d("RegisterActivity", "registerInBackground - regId: "	+ regId);
					
					registerationSuccess = true;
				} catch (IOException ex) {
					Log.d("RegisterActivity", "Error: " + ex.getLocalizedMessage());
				}
				
				return "";
			}

			@Override
			protected void onPostExecute(String msg) {
				if(registerationSuccess){
					User.getSharedInstance().setGCMRegistrationId(regId);
					Log.d("GCM id set", User.getSharedInstance().getGCMRegistrationId());
					postRegistrationDataToServer();
				}
				else
					Toast.makeText(getApplicationContext(), getString(R.string.registration_error), Toast.LENGTH_LONG).show();
			}

		}.execute(null, null, null);
	}
	
	

	
	@Override
	protected void onRestart() {
		super.onRestart();
		if (isRegistrationSuccess()) {
			directToMainActivity();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isRegistrationSuccess()) {
			directToMainActivity();
		}
	}

	public void postRegistrationDataToServer(){
		Httphandler.getSharedInstance().register(new HttpDataListener() {
			
			@Override
			public void onError(Exception e) {
				Toast.makeText(getApplicationContext(), getString(R.string.registration_error), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onDataAvailable(String response) {
				try{
					JSONObject jsonResponse = new JSONObject(response);
					boolean success = jsonResponse.getBoolean("success");
					if(success){
						Toast.makeText(context, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
						User.getSharedInstance().setRegistrationStatus(true);
						String userId = jsonResponse.getString("user_id");
						User.getSharedInstance().setUserId(userId);
						Log.d("User id", userId);
						directToMainActivity();
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
					}
				} catch(Exception e){
					Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
	}
	private void directToMainActivity(){
		Intent i = new Intent(context, MainActivity.class);
		startActivity(i);
		finish();
	}
	
	private boolean isRegistrationSuccess(){
		return User.getSharedInstance().getRegistrationStatus();
	}
	
}
