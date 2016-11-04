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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.snt.bt.recon.R;
import com.snt.bt.recon.database.Trip;
import com.snt.bt.recon.util.TransportMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyPreferencesActivity extends PreferenceActivity {

    //For Device ID
    static TelephonyManager telephonyManager;


    private static Activity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        mContext = this;


        //For Device ID
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


            Preference settings_change_transport = findPreference("preferred_transport");
            settings_change_transport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new TransportMode(mContext).modify();
                    return false;
                }
            });

            Preference settings_total_trips = findPreference("get_total_trips");
            settings_total_trips.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                    // Initialize a new instance of progress dialog
                    final ProgressDialog pd = new ProgressDialog(mContext);

                    // Set progress dialog style spinner
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    // Set the progress dialog title and message
                    pd.setTitle("Please wait");
                    pd.setMessage("Counting trips.........");

                    // Set the progress dialog background color
                    pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));

                    pd.setIndeterminate(false);

                    //Create AsycHttpClient object
                    final AsyncHttpClient client = new AsyncHttpClient();
                    //client.setThreadPool(Executors.newSingleThreadExecutor());

                    pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            client.cancelRequests(mContext, true);
                        }
                    });

                    pd.show();


                    RequestParams params = new RequestParams();


                    params.put("totalTripsJSON", "[{\"imei\":\"" + telephonyManager.getDeviceId() + "\"}]");

                    //Allow self-signed SSL certificates.
                    client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

                    //By adding context here (and onCancellistener to progressdialog) we make sure if dialog is dismissed the post is cancelled
                    client.post(mContext, "https://.../api/get_total_trips.php", params, new AsyncHttpResponseHandler() {//TODO
                        @Override
                        public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] response) {


                            try {

                                JSONArray json = new JSONArray(new String(response));
                                // Dismiss/hide the progress dialog
                                pd.dismiss();
								
								//TODO DO SOMETHING

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response, Throwable e) {
                            // Dismiss/hide the progress dialog
                            pd.dismiss();
							//TODO DO SOMETHING


                            if (statusCode == 404) {
                                //logDebug("DatabaseTest", postString + " : Requested resource not found");
                            } else if (statusCode == 500) {
                                //logDebug("DatabaseTest", postString + " : Something went wrong at server end");
                            } else {
                                //logDebug("DatabaseTest", postString + " : Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]");

                            }
                        }
                    });


                    return false;
                }
            });

        }
    }

}
