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

package com.snt.bt.recon.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snt.bt.recon.containers.BluetoothClassicDeviceStore;

import java.util.List;


public class BcDeviceListAdapter extends ArrayAdapter<BluetoothClassicDeviceStore>{

    private Context context;
    private BluetoothAdapter bTAdapter;

    public BcDeviceListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View line = null;
        BluetoothClassicDeviceStore item = (BluetoothClassicDeviceStore)getItem(position);
        final String name = item.getDeviceName();
        TextView macAddress = null;
        TextView rssi = null;
        TextView btType = null;
        TextView btClass = null;
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(com.snt.bt.recon.R.layout.list_item_device_bc, null);
        holder = new ViewHolder();
        holder.titleText = (TextView)viewToUse.findViewById(com.snt.bt.recon.R.id.device_name);
        viewToUse.setTag(holder);

        macAddress = (TextView)viewToUse.findViewById(com.snt.bt.recon.R.id.device_address);
        rssi = (TextView)viewToUse.findViewById(com.snt.bt.recon.R.id.device_rssi);
        btType = (TextView)viewToUse.findViewById(com.snt.bt.recon.R.id.device_type);
        btClass = (TextView)viewToUse.findViewById(com.snt.bt.recon.R.id.device_class);

        //line = (View)viewToUse.findViewById(R.id.line);
        holder.titleText.setText(item.getDeviceName());
        macAddress.setText(item.getAddress());
        rssi.setText(item.getRssi());
        btType.setText(item.getBtType());
        btClass.setText(item.getBtClass());


        if (item.getDeviceName()==null  || item.getDeviceName().toString() == "No Devices"  ) {
            macAddress.setVisibility(View.INVISIBLE);
            rssi.setVisibility(View.INVISIBLE);
            btType.setVisibility(View.INVISIBLE);
            btClass.setVisibility(View.INVISIBLE);

            //line.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.titleText.setLayoutParams(params);
        }

        return viewToUse;
    }


}
