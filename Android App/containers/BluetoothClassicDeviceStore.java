package com.snt.bt.recon.containers;

/**
 * Created by Matt on 5/12/2015.
 */
public class BluetoothClassicDeviceStore {

    private String deviceName;
    private String address;
    private String rssi;
    private String btType;
    private String btClass;

    private boolean connected;

    public String getDeviceName() {
        return deviceName;
    }

    public String getRssi() {
        return rssi;
    }
    public String getBtType() {
        return btType;
    }
    public String getBtClass() {
        return btClass;
    }


    public boolean getConnected() {
        return connected;
    }

    public String getAddress() {
        return address;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public BluetoothClassicDeviceStore(String name, String address,String rssi, String btType,String btClass, String connected){
        this.deviceName = name;
        this.address = address;
        this.rssi = rssi;
        this.btType = btType;
        this.btClass = btClass;

        if (connected == "true") {
            this.connected = true;
        }
        else {
            this.connected = false;
        }
    }
}
