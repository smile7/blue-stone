package com.example.misho.bluestonetest3;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

public class MainActivity extends AppCompatActivity {
	private ProximityManager proximityManager;
	int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
	TextView FoundedBeacons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		KontaktSDK.initialize ("YOUR_API_KEY");
		final int mID=3;
		final Button ScanBeacon = (Button)findViewById (R.id.ScanButton);

		ScanBeacon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				proximityManager.restartScanning ();


				NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
								.setVibrate (new long[] { 1000, 1000, 1000, 1000, 1000 })
								.setSmallIcon (R.mipmap.icon)
								.setContentTitle ("My notification")
								.setContentText ("hello world!");

				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify (mID,mBuilder.build ());
			}
		});
		if (ContextCompat.checkSelfPermission (this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_READ_CONTACTS);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.

		}
		proximityManager = ProximityManagerFactory.create (this);
		proximityManager.setIBeaconListener (createIBeaconListener ());
		proximityManager.setEddystoneListener (createEddystoneListener ());

	}

	@Override
	protected void onStart() {
		super.onStart();
		startScanning();
	}

	@Override
	protected void onStop() {
		proximityManager.stopScanning();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		proximityManager.disconnect();
		proximityManager = null;
		super.onDestroy();
	}

	private void startScanning() {
		proximityManager.connect(new OnServiceReadyListener () {
			@Override
			public void onServiceReady() {
				proximityManager.startScanning();
			}
		});
	}

	private IBeaconListener createIBeaconListener() {
		return new SimpleIBeaconListener () {
			@Override
			public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
				Log.i("Sample", "IBeacon discovered: " + ibeacon.toString());

			}
		};
	}

	private EddystoneListener createEddystoneListener() {
		return new SimpleEddystoneListener () {
			@Override
			public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
				Log.i ("Sample", "Eddystone discovered: " + eddystone.toString ());
//				Context context = getApplicationContext();
//				CharSequence text = "Eddystone discovered:" + eddystone.toString ();
//				int duration = Toast.LENGTH_LONG;
//				Toast toast = Toast.makeText (context, text, duration);
//				toast.show ();
				TextView FoundedBeacons = (TextView) findViewById (R.id.FoundedBeacons);
				FoundedBeacons.setText ("Beacons Found: ");
				FoundedBeacons.setText (FoundedBeacons.getText () + "\n" + eddystone.toString ());

				if (eddystone.getUniqueId ().equals ("nokE")){
					Context context = getApplicationContext();
					CharSequence text = "Found NokE";
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText (context, text, duration);
					toast.show ();
				}

			}
		};
	}



}