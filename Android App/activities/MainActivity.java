/*
 * ------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Walter Bronzi [wbronzi@gmail.com], [walter.bronzi@uni.lu]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ------------------------------------------------------------------------------
 */

package com.snt.bt.recon.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.snt.bt.bluetoothlelib.device.BluetoothLeDevice;
import com.snt.bt.bluetoothlelib.device.adrecord.AdRecord;
import com.snt.bt.recon.BuildConfig;
import com.snt.bt.recon.R;
import com.snt.bt.recon.containers.BluetoothClassicDeviceStore;
import com.snt.bt.recon.containers.BluetoothLeDeviceStore;
import com.snt.bt.recon.database.BluetoothClassicEntry;
import com.snt.bt.recon.database.BluetoothLowEnergyEntry;
import com.snt.bt.recon.database.DBHandler;
import com.snt.bt.recon.database.Trip;
import com.snt.bt.recon.database.GPSLocation;

import com.snt.bt.recon.services.BcScanService;
import com.snt.bt.recon.util.ActivityRecognitionUtil;
import com.snt.bt.recon.util.BluetoothLeScanner;
import com.snt.bt.recon.util.BluetoothUtils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.snt.bt.bluetoothlelib.util.ByteUtils;
import com.snt.bt.recon.adapters.LeDeviceListAdapter;
import com.snt.bt.recon.adapters.BcDeviceListAdapter;

import com.snt.bt.recon.util.AppEULA;
import com.snt.bt.recon.util.TransportMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    @Bind(com.snt.bt.recon.R.id.tvBluetoothLe)
    protected TextView mTvBluetoothLeStatus;
    @Bind(com.snt.bt.recon.R.id.tvBluetoothStatus)
    protected TextView mTvBluetoothStatus;
    @Bind(R.id.tvActivity)
    protected TextView mTvActivity;
    @Bind(R.id.tvScanning)
    protected TextView mTvScanning;

    @Bind(com.snt.bt.recon.R.id.tvItemCount)
    protected TextView mTvItemCount;
    @Bind(com.snt.bt.recon.R.id.list1)
    protected ListView mList1;
    @Bind(com.snt.bt.recon.R.id.list2)
    protected ListView mList2;
    @Bind(R.id.debug_text)
    protected TextView mDebugText;

    //wake lock
    private PowerManager.WakeLock wl;

    //session id
    private UUID sessionId;

    //location id
    private UUID locationId;

    //gps
    LocationManager locationManager;
    LocationListener locationListener;
    float currentLatitude = 0.0f;
    float currentLongtitude = 0.0f;
    float currentSpeed = 0;
    float currentBearing = 0;
    float currentAltitude = 0.0f;
    float currentAccuracy = 0;

    //menu object for server sync
    private Menu menu;

    //Device id
    TelephonyManager telephonyManager;

    //to check for connectivity
    ConnectivityManager cm;

    //Bt
    private BluetoothUtils mBluetoothUtils;
    //Ble
    private BluetoothLeScanner mLeScanner;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothLeDeviceStore mLeDeviceStore;
    //bc
    private ArrayAdapter<BluetoothClassicDeviceStore> mBcDeviceListAdapter;
    private ArrayList<BluetoothClassicDeviceStore> mBcDeviceList;



    //Activity recognition
    private ActivityRecognitionUtil mActivityRecognitionScan;

    private final ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            String advBytes = "Unknown";
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes(), System.currentTimeMillis());

            //Get advertisement data
            final Collection<AdRecord> adRecords = deviceLe.getAdRecordStore().getRecordsAsCollection();
            if (adRecords.size() > 0) {
                for (final AdRecord record : adRecords) {
                    if (record.getType() == 255) { //255 manufacturer specific data, 9 name,1 flags
                        advBytes = ByteUtils.byteArrayToHexString(record.getData()) + "";

                    }
                }
            }

           
            if(locationId != null) {

                    db.addBleEntry(new BluetoothLowEnergyEntry(sessionId, locationId,getDateTime(), device.getAddress(), result.getRssi(), device.getName(), advBytes, "no"));
            }


            mLeDeviceStore.addDevice(deviceLe);
            final EasyObjectCursor<BluetoothLeDevice> c = mLeDeviceStore.getDeviceCursor();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.swapCursor(c);
                }
            });
        }
    };

    //DATABASE
    DBHandler db = new DBHandler(this);

    public <T> void syncSQLiteMySQLDB(final Class<T> cl, final String postString, final String postURL) {
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();

        //Sync Trips
        try {
            List<T> list = db.getAll(cl);
            if (list.size() != 0) {
                if (db.dbSyncCount(cl) != 0) {
                    if(menu!=null)
                        menu.findItem(R.id.menu_refresh_server).setActionView(R.layout.actionbar_progress_indeterminate);

                   	params.put(postString, db.composeJSONfromSQLite(cl));

                    // using a socket factory that allows self-signed SSL certificates.
                    client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                    client.post(postURL, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] response) {
                            logDebug("DatabaseTest", "Server response " + postString + " : " + new String(response));

                            try {
                                JSONArray arr = new JSONArray(new String(response));
                                for (int x = 0; x < arr.length(); x++) {
                                    JSONObject obj = (JSONObject) arr.get(x);
                                    db.updateSyncStatus(cl,obj);
                                }
                                logDebug("DatabaseTest", postString + " : DB Sync completed!");


                                T inst=cl.newInstance();
                                if(sessionId!=null && inst instanceof Trip){
                                    logDebug("DatabaseTest", "Session in progress -> Deleting old trips..");
                                    db.deleteOldTrips(sessionId);
                                }

                                if(menu!=null)
                                    menu.findItem(R.id.menu_refresh_server).setActionView(null);

                            } catch (JSONException e) {
                                logDebug("DatabaseTest", postString + " : Error Occured [Server's JSON response might be invalid]!");
                                e.printStackTrace();
                            } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable e) {
                            if (statusCode == 404) {
                                logDebug("DatabaseTest", postString + " : Requested resource not found");
                            } else if (statusCode == 500) {
                                logDebug("DatabaseTest", postString + " : Something went wrong at server end");
                            } else {
                                logDebug("DatabaseTest", postString + " : Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]");

                            }
                            try {
                                if(menu!=null)
                                    menu.findItem(R.id.menu_refresh_server).setActionView(null);
                            } catch (NullPointerException error) {
                                error.printStackTrace();
                            }
                        }
                    });
                } else {
                    logDebug("DatabaseTest", postString + " : SQLite and Remote MySQL DBs are in Sync!");

                }
            } else {
                logDebug("DatabaseTest", postString + " : No data in DB, nothing to Sync");

            }

        } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.snt.bt.recon.R.layout.activity_main);
        ButterKnife.bind(this);
        logDebug("Activity", "######################## On Create ########################");
        //Lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //Leave screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //leave cpu on
        wl = ((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wlTag");
        wl.acquire();

        //For device id
        telephonyManager =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        //for connectivity
        cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        try {


            logDebug("DatabaseTest", "Reading all trips..");
            List<Trip> trips = db.getAll(Trip.class);
            for (Trip trip : trips) {
                String log = "session id: " + trip.getSessionId()+" imei: " + trip.getImei()+" transport: " + trip.getTransport()+" TS: " + trip.getTimestampStart()+" TE: " + trip.getTimestampEnd()+" upload status: " + trip.getUploadStatus();
                logDebug("DatabaseTest", log);
            }
            logDebug("DatabaseTest", "Reading all locations..");
            List<GPSLocation> locs = db.getAll(GPSLocation.class);
            for (GPSLocation loc : locs) {
                String log = "loc id: " + loc.getLocationId()+" sess id: "+loc.getSessionId()+" timestamp: "+loc.getTimestamp()+" upload status: " + loc.getUploadStatus();
                logDebug("DatabaseTest", log);
            }
            logDebug("DatabaseTest", "Reading all bc..");
            List<BluetoothClassicEntry> bcs = db.getAll(BluetoothClassicEntry.class);
            for (BluetoothClassicEntry bc : bcs) {
                String log = "sess id: " + bc.getSessionId()+" loc id: "+bc.getLocationId()+" upload status: " + bc.getUploadStatus();
                logDebug("DatabaseTest", log);
            }
            logDebug("DatabaseTest", "Reading all ble..");
            List<BluetoothLowEnergyEntry> bles = db.getAll(BluetoothLowEnergyEntry.class);
            for (BluetoothLowEnergyEntry ble : bles) {
                String log = "sess id: " + ble.getSessionId() + " loc id: " + ble.getLocationId() + " upload status: " + ble.getUploadStatus();
                logDebug("DatabaseTest", log);
            }
            } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


        //Show select transpot mode
        new TransportMode(this).show();


        // Show EULA
        new AppEULA(this).show();


        //displayFeedbackDialog();


        //avtivity recognition start
        mActivityRecognitionScan = new ActivityRecognitionUtil(this);
        mActivityRecognitionScan.startActivityRecognitionScan();
        //Filter the Intent and register broadcast receiver
        registerReceiver(ActivityRecognitionReceiver, new IntentFilter("ImActive"));

        //gps
        gpsStatusCheck();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3, locationListener);

        //bt
        mBluetoothUtils = new BluetoothUtils(this);

        //ble
        mLeDeviceStore = new BluetoothLeDeviceStore();
        mLeScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
        //bc
        mBcDeviceList = new ArrayList<>();


        final Handler h = new Handler();
        final int delay = 60000;




        h.postDelayed(new Runnable() {
            public void run() {
                syncServerDatabase();

                h.postDelayed(this, delay);
            }
        }, delay);


        //Clean BLE table in case device last timestamp is > 10 seconds
        final Handler h2 = new Handler();
        h2.postDelayed(new Runnable() {
            public void run() {
                //clear old ble devices
                for (BluetoothLeDevice device : mLeDeviceStore.getDeviceList()) {
                    long diff = System.currentTimeMillis() - device.getTimestamp();
                    if (diff > 10000) {
                        mLeDeviceStore.removeDevice(device);
                        //Refresh the listview
                        final EasyObjectCursor<BluetoothLeDevice> c = mLeDeviceStore.getDeviceCursor();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLeDeviceListAdapter.swapCursor(c);
                            }
                        });
                    }
                }

                h2.postDelayed(this, 1000);//1 sec
            }
        }, 1000);
    }

    public void syncServerDatabase() {
        //check for connectivity
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());



        //sync database
        if (isConnected) {
            boolean wifiOnly = sp.getBoolean("preferred_sync", false);
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            if (!isWiFi && wifiOnly) {
                logDebug("syncServerDatabase", "Phone isConnected - Not Synchronizing upon user request because connection is not WiFi");
            } else {
                logDebug("syncServerDatabase", "Phone isConnected - Synchronizing");

                syncSQLiteMySQLDB(Trip.class, "tripsJSON", "https://.../api/insert_trip.php");//TODO
                syncSQLiteMySQLDB(GPSLocation.class, "locationsJSON", "https://.../api/insert_locations.php");//TODO
                syncSQLiteMySQLDB(BluetoothClassicEntry.class, "bcJSON", "https://.../api/insert_bc.php");//TODO
                syncSQLiteMySQLDB(BluetoothLowEnergyEntry.class, "bleJSON", "https://.../api/insert_ble.php");//TODO
            }
        } else {
            logDebug("syncServerDatabase", "Phone isNotConnected");

        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;
        

        if (!mLeScanner.isScanning()) {
            mTvScanning.setText("Off");
            mTvScanning.setTextColor(Color.RED);

        } else {
            mTvScanning.setText("On");
            mTvScanning.setTextColor(Color.parseColor("#00B100")); //dark green
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            
            case R.id.menu_settings:
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
                break;

        }
        return true;
    }




    @Override
    protected void onPause() {
        super.onPause();

       

    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
    @Override
    public void onDestroy() {
        if (mLeScanner.isScanning())
            db.updateTripEnd(sessionId, getDateTime());

        stopScan();

        //stop activity recognizing
        mActivityRecognitionScan.stopActivityRecognitionScan();
        unregisterReceiver(ActivityRecognitionReceiver);
        cancelNotification(this,1);



        //WAKE LOCK
        if (wl != null && wl.isHeld()) {
            wl.release();
        }

        super.onDestroy();
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }



    @Override
    public void onResume() {
        super.onResume();


        //initial sync
        syncServerDatabase();

        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

        if (mIsBluetoothOn) {
            mTvBluetoothStatus.setText(com.snt.bt.recon.R.string.on);
        } else {
            mTvBluetoothStatus.setText(com.snt.bt.recon.R.string.off);
        }

        if (mIsBluetoothLePresent) {
            mTvBluetoothLeStatus.setText(com.snt.bt.recon.R.string.supported);
        } else {
            mTvBluetoothLeStatus.setText(com.snt.bt.recon.R.string.not_supported);
        }

        if (!mLeScanner.isScanning()) {
            mTvScanning.setText("Off");
            mTvScanning.setTextColor(Color.RED);

        } else {
            mTvScanning.setText("On");
            mTvScanning.setTextColor(Color.parseColor("#00B100"));
        }


        invalidateOptionsMenu();
    }


    private boolean startScan() {
        //Toast.makeText(getApplicationContext(), "Start scan", Toast.LENGTH_SHORT).show();
        logDebug("Activity", "######################## startScan ########################");


        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        //enable bt adapter
        mBluetoothUtils.getBluetoothAdapter().enable();
        //mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        registerReceiver(BluetoothAdapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


        //BLE
        mLeDeviceStore.clear();
        mLeDeviceListAdapter = new LeDeviceListAdapter(this, mLeDeviceStore.getDeviceCursor());
        mList1.setAdapter(mLeDeviceListAdapter);


        //BC
        mBcDeviceListAdapter = new BcDeviceListAdapter(this, mBcDeviceList); //BC Addon
        mList2.setAdapter(mBcDeviceListAdapter);
        mBcDeviceListAdapter.clear();


        if (mIsBluetoothOn && mIsBluetoothLePresent && gpsStatusCheck()) {


            //sessionid
            sessionId = UUID.randomUUID();

            //save trip to database
            db.addTrip(new Trip(sessionId, telephonyManager.getDeviceId(),new TransportMode(this).getTransportMode(),getDateTime(),"0", BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")","no"));



            //BLE
            mLeScanner.scanLeDevice(-1, true);

            //BC
            // Register receiver to get message from BtServiceReceiver
            registerReceiver(BtScanServiceReceiver, new IntentFilter(BcScanService.ACTION_START_SCAN));
            Intent newIntent = new Intent(this, BcScanService.class);
            newIntent.setAction(BcScanService.ACTION_START_SCAN);
            newIntent.putExtra(BcScanService.EXTRA_SID, sessionId.toString());
            startService(newIntent);
            BcScanService.IS_SERVICE_RUNNING = true;



            //reset location id in case old are still stored
            locationId = null;
            BcScanService.locationId = null;



            Toast.makeText(getApplicationContext(), "Starting BC Service...", Toast.LENGTH_SHORT).show();


            invalidateOptionsMenu();

            return true;
        }
        return false;

    }
    private void stopScan() {
        logDebug("Activity", "######################## stopScan ########################");


       
        syncServerDatabase();


        //stop BLE
        mLeScanner.scanLeDevice(-1, false);
        mList1.setAdapter(null);
        mLeDeviceStore.clear();

        //stop BC

        BcScanService.IS_SERVICE_RUNNING = false;
        stopService(new Intent(this, BcScanService.class));
        mBluetoothUtils.getBluetoothAdapter().cancelDiscovery();
        mList2.setAdapter(null);//mBtDeviceListAdapter.clear();
        try{
            //unregister Bt classic service receiver
            unregisterReceiver(BtScanServiceReceiver);
            //Unregister Bluetooth adapter receiver
            unregisterReceiver(BluetoothAdapterReceiver);
        } catch (IllegalArgumentException e){}




        invalidateOptionsMenu();

    }


    private BroadcastReceiver BtScanServiceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if (b != null) {
                if (b.getString(BcScanService.BT_ACTION, null).equals(BluetoothDevice.ACTION_FOUND)) {
                    // Create a new device item
                    //Log.e("TEST", b.getString(BtScanService.DEVICE_CLASS));
                    BluetoothClassicDeviceStore newDevice = new BluetoothClassicDeviceStore(b.getString(BcScanService.DEVICE_NAME),
                            b.getString(BcScanService.DEVICE_ADDR), b.getString(BcScanService.DEVICE_RSSI), b.getString(BcScanService.DEVICE_TYPE), b.getString(BcScanService.DEVICE_CLASS), "false");
                    

                    mBcDeviceListAdapter.add(newDevice);
                    mBcDeviceListAdapter.notifyDataSetChanged();
                } else if (b.getString(BcScanService.BT_ACTION, null).equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    mBcDeviceListAdapter.clear();
                }
            }
        }
    };
    private final BroadcastReceiver BluetoothAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // It means the user has changed his bluetooth state.
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (mBluetoothUtils.getBluetoothAdapter().getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    // The user bluetooth is turning off yet, but it is not disabled yet.
                    if (mLeScanner.isScanning()) {
                        Toast.makeText(getApplicationContext(), "Looks like the Bluetooth was disabled, recording will stop now!", Toast.LENGTH_SHORT).show();
                        //update Trip end timestamp
                        db.updateTripEnd(sessionId, getDateTime());
                        stopScan();
                    }
                    return;
                }
                if (mBluetoothUtils.getBluetoothAdapter().getState() == BluetoothAdapter.STATE_OFF) {
                    // The user bluetooth is already disabled.

                    return;
                }

            }
        }
    };

    private final static int INTERVAL = 1000 * 60 * 3; //3 minutes
    private final static int MONITORED_ACTIVITY = 0; //IN VEHCILE
    int mostProbableActivity;
    Handler mHandler = new Handler();
    private volatile List<Integer> activitiesList = new ArrayList<>();
    private volatile HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
    private BroadcastReceiver ActivityRecognitionReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mostProbableActivity = intent.getExtras().getInt("activity");
            int mostProbableActivityConfidence = intent.getExtras().getInt("confidence");
            mTvActivity.setText(getActivityName(mostProbableActivity) + " " + mostProbableActivityConfidence + "%");

            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra("activities");




            for (DetectedActivity activity : detectedActivities) {
                detectedActivitiesMap.put(activity.getType(), activity.getConfidence());
                activitiesList.add(activity.getType());
            }

            logDebug("ActivityRecognition", "activitiesList "+TextUtils.join(", ", activitiesList));
            logDebug("ActivityRecognition", "detectedActivities "+TextUtils.join(", ", detectedActivities));
            
             //0 = IN VEHICLE
            int vehicleConfidence = detectedActivitiesMap.containsKey(MONITORED_ACTIVITY) ? detectedActivitiesMap.get(MONITORED_ACTIVITY) : 0;
            if ((vehicleConfidence > 90) && !mLeScanner.isScanning() && !BcScanService.IS_SERVICE_RUNNING && new TransportMode((Activity)context).getTransportMode() != null && gpsStatusCheck()){
                //start scanning - if scanning started then also start logging activities
                logDebug("ActivityRecognition", "In Vehicle > 90%");
                if(startScan()){
                    logDebug("ActivityRecognition", "startScan successfully called");
                    activitiesList.clear();
                    activitiesList.add(MONITORED_ACTIVITY);// add first one just o make sure it does not stop immediately
                    mHandlerTask.run();
                 }
            }
            detectedActivitiesMap.clear();

            if (!mLeScanner.isScanning() && !BcScanService.IS_SERVICE_RUNNING)
                activitiesList.clear();
        }
    };

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            try {
                logDebug("ActivityRecognition", "List " + TextUtils.join(", ", activitiesList));


                int retval = Double.compare((double)Collections.frequency(activitiesList, MONITORED_ACTIVITY)/activitiesList.size(), 0.2); //if occurencies <20%

                if(retval < 0 && mostProbableActivity != MONITORED_ACTIVITY){
                    logDebug("ActivityRecognition", "no longer in vehicle - stopping");
                    //update Trip end timestamp
                    db.updateTripEnd(sessionId,getDateTime());
                    stopScan();
                    activitiesList.clear();
                    //stop task
                    mHandler.removeCallbacks(mHandlerTask);
                } else {
                    logDebug("ActivityRecognition", "still in vehicle - continuing");
                    //Delete all aside from last one
                    activitiesList.subList(0,activitiesList.size() - 1).clear();
                    mHandler.postDelayed(mHandlerTask, INTERVAL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Get the activity name
    private String getActivityName(int type) {
        switch (type)
        {
            case DetectedActivity.IN_VEHICLE:
                return "In Vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "On Bicycle";
            case DetectedActivity.ON_FOOT:
                return "On Foot";
            case DetectedActivity.WALKING:
                return "Walking";
            case DetectedActivity.STILL:
                return "Still";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.RUNNING:
                return "Running";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
        }
        return "N/A";
    }


 

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //save location to database


            currentLatitude = (float) loc.getLatitude();
            currentLongtitude = (float) loc.getLongitude();
            currentSpeed = loc.getSpeed();
            currentBearing = loc.getBearing();
            currentAltitude = (float) loc.getAltitude();
            currentAccuracy = loc.getAccuracy();

            locationId = UUID.randomUUID();
            if (mLeScanner.isScanning())
                db.addLocation(new GPSLocation(locationId, sessionId,getDateTime(), currentLatitude, currentLongtitude, currentSpeed, currentBearing, currentAltitude, currentAccuracy, "no"));

            //Send updated data to BC service
            if (BcScanService.IS_SERVICE_RUNNING) {
                BcScanService.locationId = locationId;
            }
            mTvItemCount.setText("Last Location: " + currentLatitude + "," + currentLongtitude + " Speed: " + String.format("%.2f", currentSpeed));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //int: OUT_OF_SERVICE if the provider is out of service, and this is not expected to change in the near future; TEMPORARILY_UNAVAILABLE
            //if the provider is temporarily unavailable but is expected to be available shortly; and AVAILABLE if the provider is currently available.
            if (status == LocationProvider.OUT_OF_SERVICE){
                locationId = null;
                //Send updated data to BC service
                if (BcScanService.IS_SERVICE_RUNNING) {
                    BcScanService.locationId = null;
                }

            }
            logDebug("LocationDebug", "On status changed " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            //logDebug("LocationDebug", "On provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //logDebug("LocationDebug", "On provider disabled");

            if (mLeScanner.isScanning() && BcScanService.IS_SERVICE_RUNNING) {
                locationId = null;

                //Send updated data to BC service
                BcScanService.locationId = null;

                Toast.makeText(getApplicationContext(), "Looks like the GPS was disabled, the App will now quit!", Toast.LENGTH_SHORT).show();
                //ouhgou
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishAffinity();
                    }
                }, 3000);
            }

        }
    }

    public boolean gpsStatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        } else
            return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS is currently disabled, do you want to manually enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        //close app
                        finishAffinity();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void logDebug (String tag, String content){
        Log.d(tag, content);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    //Method to trim textview in case its too long!
    private final static int MAX_LINE = 50;
    public void writeTextFile(String data) {
        mDebugText.append(data);
        // Erase excessive lines
        int excessLineNumber = mDebugText.getLineCount() - MAX_LINE;
        if (excessLineNumber > 0) {
            int eolIndex = -1;
            CharSequence charSequence = mDebugText.getText();
            for(int i=0; i<excessLineNumber; i++) {
                do {
                    eolIndex++;
                } while(eolIndex < charSequence.length() && charSequence.charAt(eolIndex) != '\n');
            }
            if (eolIndex < charSequence.length()) {
                mDebugText.getEditableText().delete(0, eolIndex+1);
            }
            else {
                mDebugText.setText("");
            }
        }
    }




}
