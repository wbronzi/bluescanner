package com.snt.bt.recon.util;

/**
 * This file provides simple End User License Agreement
 * It shows a simple dialog with the license text, and two buttons.
 * If user clicks on 'cancel' button, app closes and user will not be granted access to app.
 * If user clicks on 'accept' button, app access is allowed and this choice is saved in preferences
 * so next time this will not show, until next upgrade.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.snt.bt.recon.BuildConfig;

import java.util.Arrays;

public class TransportMode {

    private String TRANSPORT_MODE = "preferred_transport"+ BuildConfig.VERSION_CODE;
    private Activity mContext;
    final CharSequence myList[] = {"Car", "Mixed", "Bus"};

    public TransportMode(Activity context) {
        mContext = context;
    }


    public void show() {


        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        String preferrendTransport = prefs.getString(TRANSPORT_MODE, null);
        if (preferrendTransport==null) {


            // Disable orientation changes, to prevent parent activity
            // reinitialization
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle("Please select your preferred mode of transportation:")
                .setSingleChoiceItems(myList, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(TRANSPORT_MODE, myList[arg1].toString());
                        editor.commit();
                        arg0.dismiss();
                        Toast.makeText(mContext, "Thank you! In the future you can modify you selection in the Settings menu.", Toast.LENGTH_SHORT).show();
                        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                    }
                });
            builder.create().show();
        }
    }

    public void modify() {


        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        String preferrendTransport = prefs.getString(TRANSPORT_MODE, null);
        if (preferrendTransport==null) {


            // Disable orientation changes, to prevent parent activity
            // reinitialization
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setTitle("Please select your preferred mode of transportation:")
                    .setSingleChoiceItems(myList, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(TRANSPORT_MODE, myList[arg1].toString());
                                    editor.commit();
                            arg0.dismiss();
                            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                        }
                    });
            builder.create().show();
        } else {

            // Disable orientation changes, to prevent parent activity
            // reinitialization
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("Please select your preferred mode of transportation:")
                    .setSingleChoiceItems(myList, Arrays.asList(myList).indexOf(preferrendTransport), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(TRANSPORT_MODE, myList[arg1].toString());
                            editor.commit();
                            arg0.dismiss();
                            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                        }
                    });
            builder.create().show();

        }
    }

    public String getTransportMode() {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return prefs.getString(TRANSPORT_MODE, null);
    }
}