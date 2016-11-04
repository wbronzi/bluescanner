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

public class Trip {
    private UUID session_id;
    private String imei;
    private String transport;
    private String timestamp_start;
    private String timestamp_end;
    private String app_version;
    private String upload_status;



    public Trip() {
    }

    public Trip(UUID session_id, String imei, String transport, String timestamp_start, String timestamp_end, String app_version,String upload_status) {
        this.session_id = session_id;
        this.imei = imei;
        this.transport = transport;

        this.timestamp_start = timestamp_start;
        this.timestamp_end = timestamp_end;
        this.app_version = app_version;
        this.upload_status = upload_status;

    }

    public void setSessionId(UUID session_id) {
        this.session_id = session_id;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }


    public void setTimestampStart(String timestamp_start) {
        this.timestamp_start = timestamp_start;
    }

    public void setTimestampEnd(String timestamp_end) {
        this.timestamp_end = timestamp_end;
    }

    public void setAppVersion(String app_version) {
        this.app_version = app_version;
    }


    public void setUploadStatus(String upload_status) {
        this.upload_status = upload_status;
    }

    public UUID getSessionId() {
        return session_id;
    }

    public String getImei() {
        return imei;
    }
    public String getTransport() {
        return transport;
    }


    public String getTimestampStart() {
        return timestamp_start;
    }

    public String getTimestampEnd() {
        return timestamp_end;
    }

    public String getAppVersion() {
        return app_version;
    }


    public String getUploadStatus() {
        return upload_status;
    }
}
