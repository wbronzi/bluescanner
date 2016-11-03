package com.snt.bt.recon.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BluetoothLeScanner {
    private final Handler mHandler;
    private final ScanCallback mLeScanCallback;
    private final BluetoothUtils mBluetoothUtils;
    private boolean mScanning;

    public BluetoothLeScanner(final ScanCallback leScanCallback, final BluetoothUtils bluetoothUtils) {
        mHandler = new Handler();
        mLeScanCallback = leScanCallback;
        mBluetoothUtils = bluetoothUtils;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void scanLeDevice(final int duration, final boolean enable) {
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        if (enable) {
            if (mScanning) {
                return;
            }
            Log.d("TAG", "~ Starting Scan");
            // Stops scanning after a pre-defined scan period.
            if (duration > 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "~ Stopping Scan (timeout)");
                        mScanning = false;
                        mBluetoothUtils.getBluetoothAdapter().getBluetoothLeScanner().stopScan(mLeScanCallback);

                    }
                }, duration);
            }
            mScanning = true;
            mBluetoothUtils.getBluetoothAdapter().getBluetoothLeScanner().startScan(null,settings,mLeScanCallback);
        } else {
            Log.d("TAG", "~ Stopping Scan");
            mScanning = false;
            try {
                mBluetoothUtils.getBluetoothAdapter().getBluetoothLeScanner().stopScan(mLeScanCallback);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}
