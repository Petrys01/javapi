package com.ste0067.gpstracker;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.ste0067.gpstracker.DataHelper;
import com.ste0067.gpstracker.KMLCreator;
import com.ste0067.gpstracker.MyLocation;
import com.ste0067.gpstracker.R;
import com.ste0067.gpstracker.MyLocation.LocationResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    
    
	private static DataHelper dh;
	public static double lat;
	public static double lon;
	public static double alt;
	public static float acc;
	public static float spd;
	public static int sats;
	public String latitude, longitude;
	public static String provider;
	public static TextView t;
	public static MyLocation my_location;
	public static Timer myTimer;
	public static Timer satTimer;
	public static boolean timerRun = false;
	public static final String PREFS_NAME = "MyPreferences";
	public static int interval;
	public static boolean running = false;
	//public static NotificationManager mNotificationManager;
	//public static Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (timerRun) {
				Toast.makeText(this, "Aplikace stále bìží na pozadí...",
						Toast.LENGTH_SHORT).show();
				moveTaskToBack(true);
			} else {
				this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
    
    /*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}*/



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());     
        if (tab.getPosition() == 2) {
        	myTimer.cancel();
        	satTimer.cancel();
        	if(running) {
        		Toast.makeText(getApplicationContext(), "Logování bylo vypnuto!", Toast.LENGTH_SHORT).show();   	
        	}
        	running = false;
         }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	if (position == 2) {
        		return new SettingsActivity();			
            }
        	else {
        		return PlaceholderFragment.newInstance(position + 1);
        	}
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }



    
    public static class PlaceholderFragment extends Fragment {

    	static MapsActivity maps = new MapsActivity();
    	
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            switch(sectionNumber){
            case 2:
                 fragment = maps;//new MapsActivity();
                 break;
            /*case 3:
            	 //fragment = new SettingsActivity();
            	 break;*/

            }

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
    		final PowerManager.WakeLock wl = pm.newWakeLock(
    				PowerManager.PARTIAL_WAKE_LOCK, "GPS Tracker");

    		//String ns = Context.NOTIFICATION_SERVICE;
    		//mNotificationManager = (NotificationManager) getActivity().getSystemService(ns);

    		/*notification = new Notification(R.drawable.ic_launcher,
    				"Logování zapnuto", System.currentTimeMillis());
    		notification.setLatestEventInfo(getActivity(), "GPS Logger", "Logování zapnuto",
    				PendingIntent.getActivity(getActivity(), 0, new Intent()
    						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
    								| Intent.FLAG_ACTIVITY_SINGLE_TOP),
    						PendingIntent.FLAG_CANCEL_CURRENT));
    		notification.flags |= Notification.FLAG_ONGOING_EVENT;*/


    		myTimer = new Timer();
    		satTimer = new Timer();

    		dh = new DataHelper(getActivity());
    		dh.deleteAll();
            
            final ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.imageButton1);
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.start));
            
            ImageButton.OnClickListener listener = new ImageButton.OnClickListener()
            {

	            @SuppressLint("Wakelock") @Override
	            public void onClick(View arg0)
	            {
	            	Drawable drawable = imageButton.getDrawable();
	            	
	            	if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.start).getConstantState())){
	            		Toast.makeText(getActivity(), "Zapnuto", Toast.LENGTH_SHORT).show();
	            		imageButton.setImageDrawable(getResources().getDrawable(R.drawable.stop));
	            			
	            		running = true;
	            		
	            		wl.acquire();
						dh.deleteAll();
						timerRun = true;
						SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
						interval = settings.getInt("interval", 30);
						Toast.makeText(getActivity(), String.valueOf(interval), Toast.LENGTH_SHORT).show();

						myTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								timerMethod();
							}
						}, 0, 1000 * interval);// interval GPSky

						satTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								satTimerMethod();
							}
						}, 0, 1000 * 1);// interval Satelitù

						my_location = new MyLocation();
						my_location.start();

						t = (TextView) getActivity().findViewById(R.id.tv_fix);
						t.setText(String.valueOf("Probíhá hledání..."));

						//mNotificationManager.notify(1, notification);
	            	}
	            	else {
	            		Toast.makeText(getActivity(), "Vypnuto", Toast.LENGTH_SHORT).show();
	            		imageButton.setImageDrawable(getResources().getDrawable(R.drawable.start));
	            		
	            		running = false;
	            		
	            		wl.release();

						timerRun = false;
						myTimer.cancel();
						satTimer.cancel();
						myTimer = new Timer();
						satTimer = new Timer();
						my_location.cancel();

						t = (TextView) getActivity().findViewById(R.id.tv_fix);
						t.setText("GPS vypnuta");

						List<String> names = dh.selectAll();
						StringBuilder sb = new StringBuilder();
						for (String name : names) {
							sb.append(name + "\n");
						}
						String notsaved = sb.toString();

						if (notsaved != "") {
							KMLCreator kmlcreator = new KMLCreator();
							kmlcreator.write(getActivity());
						} else {
							Toast.makeText(getActivity(), "KML Neuloženo", Toast.LENGTH_LONG).show();
						}

						changeText();

						//mNotificationManager.cancel(1);
	            	}
	            }
	        };
            
            imageButton.setOnClickListener(listener);
    
            return rootView;
        }
        
        
        
        
        
        private void timerMethod() {
    		getActivity().runOnUiThread(getLoc);
    	}

    	private void satTimerMethod() {

    		getActivity().runOnUiThread(getSat);
    	}

    	private Runnable getSat = new Runnable() {
    		public void run() {

    			sats = my_location.getSat();

    			t = (TextView) getActivity().findViewById(R.id.tv_sat);
    			t.setText(String.valueOf(sats));
    		}

    	};

    	private Runnable getLoc = new Runnable() {
    		public void run() {

    			my_location.init(getActivity(), locationResult);

    		}
    	};

    	public LocationResult locationResult = new LocationResult() {
    		@Override
    		public void gotLocation(final Location location) {

    			provider = location.getProvider();
    			lat = location.getLatitude();
    			lon = location.getLongitude();
    			alt = location.getAltitude();
    			acc = location.getAccuracy();
    			spd = location.getSpeed();
    			Time currentTime = new Time();
    			currentTime.setToNow();
    			String time = currentTime.toString();
    			String hour = time.substring(9, 11);
    			String minute = time.substring(11, 13);
    			String second = time.substring(13, 15);
    			time = hour + ":" + minute + ":" + second;

    			String info = "Signál z: " + provider + " v èase: " + time;
    			t = (TextView) getActivity().findViewById(R.id.tv_fix);
    			t.setText(String.valueOf(info));
    			t = (TextView) getActivity().findViewById(R.id.tv_sirka);
    			t.setText(String.valueOf(lat));
    			t = (TextView) getActivity().findViewById(R.id.tv_delka);
    			t.setText(String.valueOf(lon));
    			t = (TextView) getActivity().findViewById(R.id.tv_vyska);
    			t.setText(String.valueOf(alt).substring(0, 5) + " metrù");
    			t = (TextView) getActivity().findViewById(R.id.tv_presnost);
    			t.setText(String.valueOf(acc) + " metrù");
    			t = (TextView) getActivity().findViewById(R.id.tv_rychlost);
    			t.setText(String.valueOf(spd) + " m/s");
    			
    			maps.SetPosition(lat, lon);
    			maps.AddPoint(lat, lon);

    			savetodatabase();

    		}
    	};

    	public void savetodatabase() {

    		dh.insert(String.valueOf(lat) + "," + String.valueOf(lon));

    	}

    	

    	/*@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    		switch (item.getItemId()) {
    		case R.id.googlemaps:
    			if (!timerRun) {
    				Intent intent = new Intent(GPSLoggerActivity.this,
    						GoogleMapsActivity.class);
    				startActivityForResult(intent, ACTIVITY_INTENT_RETURNTEXT);
    			} else {
    				Toast.makeText(GPSLoggerActivity.this,
    						"Mapy nejsou k dispozici, vypnìte logování",
    						Toast.LENGTH_LONG).show();
    			}
    			break;
    		case R.id.settings:
    			Intent intent1 = new Intent(GPSLoggerActivity.this,
    					PreferencesActivity.class);
    			startActivityForResult(intent1, ACTIVITY_INTENT_RETURNTEXT);
    			break;
    		case R.id.exit:
    			this.finish();
    			System.exit(0);
    			break;
    		}
    		return true;
    	}*/

    	

    	public void changeText() {

    		t = (TextView) getActivity().findViewById(R.id.tv_fix);
    		t.setText("GPS vypnuta");
    		t = (TextView) getActivity().findViewById(R.id.tv_delka);
    		t.setText("---");
    		t = (TextView) getActivity().findViewById(R.id.tv_sirka);
    		t.setText("---");
    		t = (TextView) getActivity().findViewById(R.id.tv_vyska);
    		t.setText("---");
    		t = (TextView) getActivity().findViewById(R.id.tv_presnost);
    		t.setText("---");
    		t = (TextView) getActivity().findViewById(R.id.tv_rychlost);
    		t.setText("---");
    		t = (TextView) getActivity().findViewById(R.id.tv_sat);
    		t.setText("---");

    		dh.deleteAll();

    	}
        
        
        
       
    }

}
