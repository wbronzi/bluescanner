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
