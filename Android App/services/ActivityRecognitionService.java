package com.snt.bt.recon.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.snt.bt.recon.R;
import com.snt.bt.recon.activities.MainActivity;

import java.util.ArrayList;


public class ActivityRecognitionService extends IntentService {
    //LogCat
    private static final String TAG = ActivityRecognitionService.class.getSimpleName();

    //Notifications
    public static int NOTIFICATION_ACTIVITY_SERVICE = 101;

    public ActivityRecognitionService() {
        super("ActivityRecognitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        if(ActivityRecognitionResult.hasResult(intent)) {
            //show notification
            showNotification();

            //Extract the result from the Response
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
            DetectedActivity detectedActivity = result.getMostProbableActivity();



            //Get the Confidence and Name of Activity
            int confidence = detectedActivity.getConfidence();
            String mostProbableName = getActivityName(detectedActivity.getType());

            //Fire the intent with activity name & confidence
            Intent i = new Intent("ImActive");
            i.putExtra("activities", detectedActivities);
            i.putExtra("activity", detectedActivity.getType());
            i.putExtra("confidence", confidence);

            Log.d(TAG, "Most Probable Name : " + mostProbableName);

            Log.d(TAG, "Confidence : " + confidence);

            //Send Broadcast to be listen in MainActivity
            this.sendBroadcast(i);

        }else {
            Log.d(TAG, "Intent had no data returned");
        }
    }

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
    
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;

    private void showNotification() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//PendingIntent.FLAG_CANCEL_CURRENT //PendingIntent.FLAG_UPDATE_CURRENT

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_activity);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("SnT BlueScanner")
                        .setTicker("SnT BlueScanner")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Activity Recognition Service"))
                        .setContentText("Activity Recognition Service")
                        .setSmallIcon(R.drawable.ic_activity)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setOngoing(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}