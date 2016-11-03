package com.snt.bt.recon.util;



import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.snt.bt.recon.services.ActivityRecognitionService;


public class ActivityRecognitionUtil implements ConnectionCallbacks, OnConnectionFailedListener {
    private Context mContext;
    private GoogleApiClient mGApiClient;
    private static final String TAG = "ActivityRecognition";
    private static PendingIntent callbackIntent;

    public ActivityRecognitionUtil(Context context) {
        this.mContext = context;
    }

    public void startActivityRecognitionScan() {
        mGApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
        mGApiClient.connect();
        Log.d(TAG, "startActivityRecognitionScan");
    }

    public void stopActivityRecognitionScan() {
        //did not work on onConnectionSuspended
        if(mGApiClient.isConnected())
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGApiClient, callbackIntent);

        mGApiClient.disconnect();

        Log.d(TAG, "stopActivityRecognitionScan");
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Intent i = new Intent(mContext, ActivityRecognitionService.class);
        callbackIntent = PendingIntent.getService(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "connected to ActivityRecognition");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGApiClient, 10000, callbackIntent); //10s//0 fastest interval


    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Suspended to ActivityRecognition");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Not connected to ActivityRecognition");
    }

}