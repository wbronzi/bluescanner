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
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snt.bt.bluetoothlelib.device.BluetoothLeDevice;
import com.snt.bt.bluetoothlelib.device.beacon.BeaconType;
import com.snt.bt.bluetoothlelib.device.beacon.BeaconUtils;
import com.snt.bt.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;
import com.snt.bt.recon.util.Constants;

import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;

spublic class LeDeviceListAdapter extends SimpleCursorAdapter {
    private final LayoutInflater mInflator;
    private final Activity mActivity;

    public LeDeviceListAdapter(final Activity activity, final EasyObjectCursor<BluetoothLeDevice> cursor) {
        super(activity, com.snt.bt.recon.R.layout.list_item_device, cursor, new String[0], new int[0], 0);
        mInflator = activity.getLayoutInflater();
        mActivity = activity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EasyObjectCursor<BluetoothLeDevice> getCursor() {
        return ((EasyObjectCursor<BluetoothLeDevice>) super.getCursor());
    }

    @Override
    public BluetoothLeDevice getItem(final int i) {
        return getCursor().getItem(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(com.snt.bt.recon.R.layout.list_item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(com.snt.bt.recon.R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(com.snt.bt.recon.R.id.device_name);
            viewHolder.deviceRssi = (TextView) view.findViewById(com.snt.bt.recon.R.id.device_rssi);
            viewHolder.deviceIcon = (ImageView) view.findViewById(com.snt.bt.recon.R.id.device_icon);
            viewHolder.deviceLastUpdated = (TextView) view.findViewById(com.snt.bt.recon.R.id.device_last_update);
            viewHolder.ibeaconMajor = (TextView) view.findViewById(com.snt.bt.recon.R.id.ibeacon_major);
            viewHolder.ibeaconMinor = (TextView) view.findViewById(com.snt.bt.recon.R.id.ibeacon_minor);
            viewHolder.ibeaconDistance = (TextView) view.findViewById(com.snt.bt.recon.R.id.ibeacon_distance);
            viewHolder.ibeaconUUID = (TextView) view.findViewById(com.snt.bt.recon.R.id.ibeacon_uuid);
            viewHolder.ibeaconTxPower = (TextView) view.findViewById(com.snt.bt.recon.R.id.ibeacon_tx_power);
            viewHolder.ibeaconSection = view.findViewById(com.snt.bt.recon.R.id.ibeacon_section);
            viewHolder.ibeaconDistanceDescriptor = (TextView) view.findViewById(com.snt.bt.recon.R.id.ibeacon_distance_descriptor);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final BluetoothLeDevice device = getCursor().getItem(i);
        final String deviceName = device.getName();
        final double rssi = device.getRssi();

        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        } else {
            viewHolder.deviceName.setText(com.snt.bt.recon.R.string.unknown_device);
        }

        if (BeaconUtils.getBeaconType(device) == BeaconType.IBEACON) {
            final IBeaconDevice iBeacon = new IBeaconDevice(device);
            final String accuracy = Constants.DOUBLE_TWO_DIGIT_ACCURACY.format(iBeacon.getAccuracy());

            viewHolder.deviceIcon.setImageResource(com.snt.bt.recon.R.drawable.ic_device_ibeacon);
            viewHolder.ibeaconSection.setVisibility(View.VISIBLE);
            viewHolder.ibeaconMajor.setText(String.valueOf(iBeacon.getMajor()));
            viewHolder.ibeaconMinor.setText(String.valueOf(iBeacon.getMinor()));
            viewHolder.ibeaconTxPower.setText(String.valueOf(iBeacon.getCalibratedTxPower()));
            viewHolder.ibeaconUUID.setText(iBeacon.getUUID());
            viewHolder.ibeaconDistance.setText(
                    mActivity.getString(com.snt.bt.recon.R.string.formatter_meters, accuracy));
            viewHolder.ibeaconDistanceDescriptor.setText(iBeacon.getDistanceDescriptor().toString());
        } else {
            viewHolder.deviceIcon.setImageResource(com.snt.bt.recon.R.drawable.ic_bluetooth);
            viewHolder.ibeaconSection.setVisibility(View.GONE);
        }

        final String rssiString =
                mActivity.getString(com.snt.bt.recon.R.string.formatter_db, String.valueOf(rssi));
        final String runningAverageRssiString =
                mActivity.getString(com.snt.bt.recon.R.string.formatter_db, String.valueOf(device.getRunningAverageRssi()));

        viewHolder.deviceLastUpdated.setText(
                android.text.format.DateFormat.format(
                        Constants.TIME_FORMAT, new java.util.Date(device.getTimestamp())));
        viewHolder.deviceAddress.setText(device.getAddress());
        //viewHolder.deviceRssi.setText(rssiString + " / " + runningAverageRssiString);
        viewHolder.deviceRssi.setText(rssiString);
        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        TextView ibeaconUUID;
        TextView ibeaconMajor;
        TextView ibeaconMinor;
        TextView ibeaconTxPower;
        TextView ibeaconDistance;
        TextView ibeaconDistanceDescriptor;
        TextView deviceLastUpdated;
        View ibeaconSection;
        ImageView deviceIcon;
    }

}