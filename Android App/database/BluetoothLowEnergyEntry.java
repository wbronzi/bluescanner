package com.snt.bt.recon.database;

import java.util.UUID;


public class BluetoothLowEnergyEntry {

    int id;
    private UUID session_id;
    private UUID location_id;

    private String timestamp;
    private String mac;
    private int rssi;
    private String device_name;
    private String ble_adv_data;
    private String upload_status;


    public BluetoothLowEnergyEntry() {
    }

    public BluetoothLowEnergyEntry( UUID session_id, UUID location_id, String timestamp, String mac, int rssi, String device_name, String ble_adv_data, String upload_status) {
        this.session_id = session_id;
        this.location_id = location_id;

        this.timestamp = timestamp;
        this.mac = mac;
        this.rssi = rssi;
        this.device_name = device_name;
        this.ble_adv_data = ble_adv_data;
        this.upload_status = upload_status;

    }

    //Used for composing json
    public BluetoothLowEnergyEntry(int id,UUID session_id, UUID location_id, String timestamp, String mac, int rssi, String device_name, String ble_adv_data, String upload_status) {
        this.session_id = session_id;
        this.location_id = location_id;

        this.timestamp = timestamp;
        this.mac = mac;
        this.rssi = rssi;
        this.device_name = device_name;
        this.ble_adv_data = ble_adv_data;
        this.upload_status = upload_status;

    }


    public void setId(int id) {
        this.id = id;
    }

    public void setSessionId(UUID session_id) {
        this.session_id = session_id;
    }

    public void setLocationId(UUID location_id) {
        this.location_id = location_id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setDeviceName(String device_name) {
        this.device_name = device_name;
    }

    public void setBleAdvData(String ble_adv_data) {
        this.ble_adv_data = ble_adv_data;
    }


    public void setUploadStatus(String upload_status) {
        this.upload_status = upload_status;
    }



    public int getId() {
        return id;
    }

    public UUID getSessionId() {
        return session_id;
    }

    public UUID getLocationId() {
        return location_id;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public String getMac() {
        return mac;
    }

    public int getRssi() {
        return rssi;
    }

    public String getDeviceName() {
        return device_name;
    }

    public String getBleAdvData() {
        return ble_adv_data;
    }


    public String getUploadStatus() {
        return upload_status;
    }
}
