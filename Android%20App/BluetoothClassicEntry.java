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
