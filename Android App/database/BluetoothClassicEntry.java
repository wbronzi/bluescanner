package com.snt.bt.recon.database;

import java.util.UUID;


public class BluetoothClassicEntry {
    private int id;
    private UUID session_id;
    private UUID location_id;
    private String timestamp;
    private String mac;
    private int type;
    private int rssi;
    private String device_name;
    private String bc_class;
    private String upload_status;


    public BluetoothClassicEntry() {
    }

    public BluetoothClassicEntry(UUID session_id, UUID location_id, String timestamp, String mac, int type, int rssi, String device_name, String bc_class, String upload_status) {
        this.session_id = session_id;
        this.location_id = location_id;
        this.timestamp = timestamp;
        this.mac = mac;
        this.type = type;
        this.rssi = rssi;
        this.device_name = device_name;
        this.bc_class = bc_class;
        this.upload_status = upload_status;
    }

    //User for composing json
    public BluetoothClassicEntry(int id,UUID session_id, UUID location_id, String timestamp, String mac, int type, int rssi, String device_name, String bc_class, String upload_status) {
        this.id = id;
        this.session_id = session_id;
        this.location_id = location_id;
        this.timestamp = timestamp;
        this.mac = mac;
        this.type = type;
        this.rssi = rssi;
        this.device_name = device_name;
        this.bc_class = bc_class;
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

    public void setType(int type) {
        this.type = type;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setDeviceName(String device_name) {
        this.device_name = device_name;
    }

    public void setBcClass(String bc_class) {
        this.bc_class = bc_class;
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

    public int getType() {
        return type;
    }

    public int getRssi() {
        return rssi;
    }

    public String getDeviceName() {
        return device_name;
    }

    public String getBcClass() {
        return bc_class;
    }


    public String getUploadStatus() {
        return upload_status;
    }
}
