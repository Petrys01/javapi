package com.ste0067.gpstracker;

import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

class MyLocation {

	private static final int min_gps_sat_count = 5;
	private static final int iteration_timeout_step = 500;

	private Location bestLocation = null;
	private Handler handler = new Handler();
	private LocationManager myLocationManager;
	public Context context;
	public LocationResult locationResult;

	@SuppressWarnings("unused")
	private boolean gps_enabled = false;
	private static boolean stop = false;
	private boolean destroy = false;
	private boolean no_signal = false;

	public static long mLastLocationMillis;
	private int counts = 0;
	private int sat_count = 0;
	public static final String PREFS_NAME = "MyPreferences";

	public Runnable showTime = new Runnable() {

		public void run() {
			counts++;
			no_signal = false;

			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
			boolean last_location = settings.getBoolean("last_location", false);

			if (counts > 100) {
				stop = true;
				gps_enabled = false;
				if (!last_location) {
					no_signal = true;
				}
			}

			bestLocation = getLocation(context);

			if (bestLocation == null) {
				
				handler.postDelayed(this, iteration_timeout_step);
			} 
			else {
				if (stop == false) {
					handler.postDelayed(this, iteration_timeout_step);
				} 
				else {

					myLocationManager.removeUpdates(gpsLocationListener);
					myLocationManager.removeUpdates(networkLocationListener);
					myLocationManager.removeGpsStatusListener(gpsStatusListener);

					stop = false;

					if (!destroy) {
						if (no_signal) {
							Toast.makeText(context, "Není signál", Toast.LENGTH_LONG).show();
						} 
						else {
							locationResult.gotLocation(bestLocation);
						}
					} 
					else {
					}
				}
			}
		}
	};

	
	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}

	
	public void init(Context ctx, LocationResult result) {

		context = ctx;
		locationResult = result;

		myLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		gps_enabled = (Boolean) myLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		bestLocation = null;
		counts = 0;

		myLocationManager.requestLocationUpdates("network", 0, 0,
				networkLocationListener);
		myLocationManager.requestLocationUpdates("gps", 0, 0,
				gpsLocationListener);
		myLocationManager.addGpsStatusListener(gpsStatusListener);

		handler.postDelayed(showTime, iteration_timeout_step);
	}

	
	public final GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {

			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				try {
					GpsStatus status = myLocationManager.getGpsStatus(null);
					Iterable<GpsSatellite> satellites = status.getSatellites();

					sat_count = 0;

					Iterator<GpsSatellite> satI = satellites.iterator();
					while (satI.hasNext()) {
						@SuppressWarnings("unused")
						GpsSatellite satellite = satI.next();
						sat_count++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					sat_count = min_gps_sat_count + 1;
				}
				break;

			case GpsStatus.GPS_EVENT_FIRST_FIX:
				stop = true;
				break;

			}
		}
	};

	
	public final LocationListener gpsLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	
	public final LocationListener networkLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	
	public static Location getLocation(Context context) {

		try {
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			String strLocationProvider = lm.getBestProvider(criteria, true);

			mLastLocationMillis = SystemClock.elapsedRealtime();

			Location location = lm.getLastKnownLocation(strLocationProvider);
			if (location != null) {
				return location;
			}
			return null;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void start() {
		stop = false;
	}

	public void cancel() {
		stop = true;
		destroy = true;
		gps_enabled = false;
		bestLocation = null;
		sat_count = 0;
	}

	public int getSat() {
		return sat_count;
	}
}