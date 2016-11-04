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


package com.snt.bt.recon.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.os.IBinder;
import android.widget.Toast;

import com.snt.bt.recon.R;
import com.snt.bt.recon.activities.MainActivity;
import com.snt.bt.recon.database.BluetoothClassicEntry;
import com.snt.bt.recon.database.DBHandler;


public class BcScanService extends Service {
    public static final String ACTION_START_SCAN = "com.snt.bt.recon.services.action.START_SCAN";
    public static final String ACTION_STOP_SCAN = "com.snt.bt.recon.services.action.STOP_SCAN";

    // parameters in
    public static final String EXTRA_SID = "com.snt.bt.recon.services.extra.SESSIONID";

    // parameters out

    public static final String BT_ACTION = "com.snt.bt.recon.services.extra.BT_ACTION";
    public static final String DEVICE_NAME = "com.snt.bt.recon.services.extra.DEVICE_NAME";
    public static final String DEVICE_ADDR = "com.snt.bt.recon.services.extra.DEVICE_ADDRESS";
    public static final String DEVICE_TYPE = "com.snt.bt.recon.services.extra.DEVICE_TYPE";
    public static final String DEVICE_RSSI = "com.snt.bt.recon.services.extra.DEVICE_RSSI";
    public static final String DEVICE_CLASS = "com.snt.bt.recon.services.extra.DEVICE_CLASS";


    //wake lock
    private PowerManager.WakeLock wl;


    public static boolean IS_SERVICE_RUNNING = false;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    //session id
    private UUID sessionId;

    //Scanner id
    public static UUID locationId;

    //DATABASE
    DBHandler db = new DBHandler(this);

    //handler thread
    Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //leave cpu on
        wl = ((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wlTag2");
        wl.acquire();

        HandlerThread handlerThread = new HandlerThread("ht");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        handler = new Handler(looper);

        //show notification
        showNotification();

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_SCAN.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_LAT);
                sessionId = UUID.fromString(intent.getStringExtra(EXTRA_SID));
                handleActionStartScan();

            }
        }
        return START_STICKY;
    }



    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStartScan() {


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReciever, filter, null, handler);

        BluetoothAdapter.getDefaultAdapter().startDiscovery();

    }

   


    @Override
    public void onDestroy() {
        //locationId = null;

        unregisterReceiver(bReciever);

        //remove notification
        mNotificationManager.cancel(2);


        //WAKE LOCK
        if (wl != null && wl.isHeld()) {
            wl.release();
        }



        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);

                //DEVICE_TYPE_CLASSIC 1
                //DEVICE_TYPE_DUAL 3
                //DEVICE_TYPE_LE 2
                //DEVICE_TYPE_UNKNOWN 0

                //if latitude is different than 0.0
                if(locationId != null) {
                    //TODO INSERTING INTO DB BC
                    db.addBcEntry(new BluetoothClassicEntry(sessionId,locationId,getDateTime(), device.getAddress(),device.getType(), rssi, device.getName(),
                            device.getBluetoothClass().toString(), "no"));

                }
                // Send the device back to the MainActivity
                Intent backIntent=new Intent(BcScanService.ACTION_START_SCAN);
                backIntent.putExtra(BcScanService.BT_ACTION, action);
                if(device.getName()==null)
                    backIntent.putExtra(BcScanService.DEVICE_NAME, "Null");
                else
                    backIntent.putExtra(BcScanService.DEVICE_NAME, device.getName());
                backIntent.putExtra(BcScanService.DEVICE_ADDR, device.getAddress());

                int deviceType = device.getType();
                if(deviceType == BluetoothDevice.DEVICE_TYPE_CLASSIC)
                    backIntent.putExtra(BcScanService.DEVICE_TYPE, "BC Only");
                else if(deviceType == BluetoothDevice.DEVICE_TYPE_LE)
                    backIntent.putExtra(BcScanService.DEVICE_TYPE, "BLE Only");
                else if(deviceType == BluetoothDevice.DEVICE_TYPE_DUAL)
                    backIntent.putExtra(BcScanService.DEVICE_TYPE, "BC/BLE");
                else if(deviceType == BluetoothDevice.DEVICE_TYPE_UNKNOWN)
                    backIntent.putExtra(BcScanService.DEVICE_TYPE, "Unknown");

                backIntent.putExtra(BcScanService.DEVICE_RSSI, String.valueOf(rssi));
                backIntent.putExtra(BcScanService.DEVICE_CLASS, device.getBluetoothClass().toString());


                sendBroadcast(backIntent);

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Log.d("DEVICELIST", "Discovery started\n");
                Intent backIntent=new Intent(BcScanService.ACTION_START_SCAN);
                backIntent.putExtra(BcScanService.BT_ACTION, action);
                sendBroadcast(backIntent);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Log.d("DEVICELIST", "Discovery finished\n");
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
                Intent backIntent=new Intent(BcScanService.ACTION_START_SCAN);
                backIntent.putExtra(BcScanService.BT_ACTION, action);
                sendBroadcast(backIntent);
            }
        }
    };

    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 2;

    private void showNotification() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//PendingIntent.FLAG_CANCEL_CURRENT //PendingIntent.FLAG_UPDATE_CURRENT

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_bc);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("SnT BlueScanner")
                        .setTicker("SnT BlueScanner")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Recording trip..."))
                        .setContentText("Recording trip...")
                        .setSmallIcon(R.drawable.ic_bc)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setOngoing(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}