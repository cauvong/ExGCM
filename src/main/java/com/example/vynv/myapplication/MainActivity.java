package com.example.vynv.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import static com.example.vynv.myapplication.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.vynv.myapplication.CommonUtilities.EXTRA_MESSAGE;
import static com.example.vynv.myapplication.CommonUtilities.EXTRA_MESSAGE_;
import static com.example.vynv.myapplication.CommonUtilities.SENDER_ID;

public class MainActivity extends Activity {
	// label to display gcm messages
	TextView lblMessage;
	EditText edMessage;
    Button btnSend;
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	AsyncTask<Void, Void, Void> mSendMessageTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Connection detector
	ConnectionDetector cd;
	
	public static String name;
	public  String numberPhone;
	public static String email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        // Getting name, email from intent
        Intent i = getIntent();
        numberPhone=i.getStringExtra("number_phone");
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        Log.d("xxx",""+numberPhone);
        final String regId_ = GCMRegistrar.getRegistrationId(this);
		edMessage=(EditText)findViewById(R.id.edMessage);
        btnSend=(Button)findViewById(R.id.btnSend);
		cd = new ConnectionDetector(getApplicationContext());
        final String deviceId_= "android-device-"+android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendMessageTask=new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ServerUtilities.sendMessage(getApplicationContext(), regId_, edMessage.getText().toString(), deviceId_);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mSendMessageTask=null;
                    }
                };
                mSendMessageTask.execute(null,null,null);
                edMessage.setText("");
            }
        });
		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);
        final String deviceId= "android-device-"+android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        // Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, name, email, regId,deviceId);
                        Log.d("xxx",""+ android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}		

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
            if(intent.getExtras().getString(EXTRA_MESSAGE)==null || intent.getExtras().getString(EXTRA_MESSAGE_)==null){
                return;
            }
            else {
                Log.d("xxxReceiver", intent.getExtras().getString(EXTRA_MESSAGE));
                Log.d("xxxReceiver_____", intent.getExtras().getString(EXTRA_MESSAGE_));
                String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
                // Waking up mobile if it is sleeping
                WakeLocker.acquire(getApplicationContext());

                /**
                 * Take appropriate action on this message
                 * depending upon your app requirement
                 * For now i am just displaying it on the screen
                 * */

                // Showing received message
                lblMessage.append(newMessage + "\n");
                Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

                // Releasing wake lock
                WakeLocker.release();
            }
		}
	};
	
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
