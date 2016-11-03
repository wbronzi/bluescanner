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
