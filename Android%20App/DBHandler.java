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



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;




public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 21;
    // Database Name
    public static final String DATABASE_NAME = "db_new";
    // Contacts table name
    public static final String TABLE_TRIPS = "trips";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String TABLE_BC = "bc";
    public static final String TABLE_BLE = "ble";


    // Common Table Columns names
    private static final String KEY_LOCATION_ID = "location_id"; //P/F key
    private static final String KEY_SESSION_ID = "session_id"; //P/F key

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_BEARING = "bearing";
    private static final String KEY_ALTITUDE = "altitude";
    private static final String KEY_ACCURACY = "accuracy";

    private static final String KEY_MAC = "mac";
    private static final String KEY_RSSI = "rssi";
    private static final String KEY_DEVICE_NAME = "device_name";

    // Trips Table Columns names
    private static final String KEY_IMEI = "imei";
    private static final String KEY_TRANSPORT = "transport";
    private static final String KEY_APP_VERSION = "app_version";


    private static final String KEY_TIMESTAMP_START = "timestamp_start";
    private static final String KEY_TIMESTAMP_END = "timestamp_end";
    private static final String KEY_UPLOAD_STATUS = "upload_status";

    // Locations Table Columns names
    //All general

    //BC Table Columns name
    private static final String KEY_BC_CLASS = "bc_class";
    private static final String KEY_TYPE = "type";

    //Ble Table Columns name
    private static final String KEY_BLE_ADV_DATA = "ble_adv_data";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRIPS_TABLE = "CREATE TABLE " + TABLE_TRIPS + "("
                + KEY_SESSION_ID + " VARCHAR(36) PRIMARY KEY," + KEY_IMEI + " TEXT," + KEY_TRANSPORT + " TEXT,"
                + KEY_TIMESTAMP_START + " DATETIME," + KEY_TIMESTAMP_END + " DATETIME, "+ KEY_APP_VERSION + " TEXT, "+ KEY_UPLOAD_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_TRIPS_TABLE);

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_LOCATION_ID + " VARCHAR(36) PRIMARY KEY, "+ KEY_SESSION_ID + " VARCHAR(36)," + KEY_TIMESTAMP + " DATETIME," + KEY_LATITUDE + " FLOAT, "+ KEY_LONGITUDE + " FLOAT, "
                + KEY_SPEED + " FLOAT, "+ KEY_BEARING + " FLOAT, "+ KEY_ALTITUDE + " FLOAT, " + KEY_ACCURACY + " FLOAT, "+ KEY_UPLOAD_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_LOCATIONS_TABLE);

        String CREATE_BC_TABLE = "CREATE TABLE " + TABLE_BC + "("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "+ KEY_SESSION_ID + " VARCHAR(36)," + KEY_LOCATION_ID + " VARCHAR(36)," + KEY_TIMESTAMP + " DATETIME," + KEY_MAC + " VARCHAR(17), "+ KEY_TYPE + " INT(11), "
                + KEY_RSSI + " INT(11), "+ KEY_DEVICE_NAME + " TEXT, "+ KEY_BC_CLASS + " TEXT, "+ KEY_UPLOAD_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_BC_TABLE);

        String CREATE_BLE_TABLE = "CREATE TABLE " + TABLE_BLE + "("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "+ KEY_SESSION_ID + " VARCHAR(36)," + KEY_LOCATION_ID + " VARCHAR(36),"+ KEY_TIMESTAMP + " DATETIME," + KEY_MAC + " VARCHAR(17), "
                + KEY_RSSI + " INT(11), "+ KEY_DEVICE_NAME + " TEXT, "+ KEY_BLE_ADV_DATA + " TEXT, " + KEY_UPLOAD_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_BLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE);


// Creating tables again
        onCreate(db);
    }


    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public <T> String composeJSONfromSQLite(Class<T> cl) throws InstantiationException, IllegalAccessException
    {
        T inst=cl.newInstance();
        List<T> list=new ArrayList<T>();

        // Select All Query
        String selectQuery;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        if(inst instanceof Trip)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_TRIPS + " where "+ KEY_UPLOAD_STATUS +" = '"+"no"+"' or " + KEY_UPLOAD_STATUS +" = '"+"partial"+"'";
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Trip trip = new Trip();
                    trip.setSessionId(UUID.fromString(cursor.getString(0)));
                    trip.setImei(cursor.getString(1));
                    trip.setTransport(cursor.getString(2));

                    trip.setTimestampStart(cursor.getString(3));
                    trip.setTimestampEnd(cursor.getString(4));
                    trip.setAppVersion(cursor.getString(5));

                    // Adding contact to list
                    list.add((T) trip);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }
        else if(inst instanceof GPSLocation)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_LOCATIONS + " where "+ KEY_UPLOAD_STATUS +" = '"+"no"+"'";
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    GPSLocation location = new GPSLocation();
                    location.setLocationId(UUID.fromString(cursor.getString(0)));
                    location.setSessionId(UUID.fromString(cursor.getString(1)));
                    location.setTimestamp(cursor.getString(2));
                    location.setLatitude(cursor.getFloat(3));
                    location.setLongitude(cursor.getFloat(4));
                    location.setSpeed(cursor.getFloat(5));
                    location.setBearing(cursor.getFloat(6));
                    location.setAltitude(cursor.getFloat(7));
                    location.setAccuracy(cursor.getFloat(8));

// Adding contact to list
                    list.add((T) location);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }

        else if(inst instanceof BluetoothClassicEntry)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_BC + " where "+ KEY_UPLOAD_STATUS +" = '"+"no"+"'";
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    BluetoothClassicEntry bc_entry = new BluetoothClassicEntry();
                    bc_entry.setId(cursor.getInt(0));
                    bc_entry.setSessionId(UUID.fromString(cursor.getString(1)));
                    bc_entry.setLocationId(UUID.fromString(cursor.getString(2)));
                    bc_entry.setTimestamp(cursor.getString(3));
                    bc_entry.setMac(cursor.getString(4));
                    bc_entry.setType(cursor.getInt(5));
                    bc_entry.setRssi(cursor.getInt(6));
                    bc_entry.setDeviceName(cursor.getString(7));
                    bc_entry.setBcClass(cursor.getString(8));

// Adding contact to list
                    list.add((T) bc_entry);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }

        else if(inst instanceof BluetoothLowEnergyEntry)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_BLE + " where "+ KEY_UPLOAD_STATUS +" = '"+"no"+"'";
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    BluetoothLowEnergyEntry ble_entry = new BluetoothLowEnergyEntry();
                    ble_entry.setId(cursor.getInt(0));
                    ble_entry.setSessionId(UUID.fromString(cursor.getString(1)));
                    ble_entry.setLocationId(UUID.fromString(cursor.getString(2)));
                    ble_entry.setTimestamp(cursor.getString(3));
                    ble_entry.setMac(cursor.getString(4));
                    ble_entry.setRssi(cursor.getInt(5));
                    ble_entry.setDeviceName(cursor.getString(6));
                    ble_entry.setBleAdvData(cursor.getString(7));

// Adding contact to list
                    list.add((T) ble_entry);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

// return contact list
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        Log.d("DatabaseTest", "composeJSONfromSQLite " + cl.getName() + " " + gson.toJson(list));

        return gson.toJson(list);
    }


    /**
     * Get all from a table and return a list
     * @return
     */
    public <T> List<T> getAll(Class<T> cl) throws InstantiationException, IllegalAccessException
    {
        T inst=cl.newInstance();
        List<T> list=new ArrayList<T>();

        // Select All Query
        String selectQuery;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        if(inst instanceof Trip)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_TRIPS;
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Trip trip = new Trip();
                    trip.setSessionId(UUID.fromString(cursor.getString(0)));
                    trip.setImei(cursor.getString(1));
                    trip.setTransport(cursor.getString(2));

                    trip.setTimestampStart(cursor.getString(3));
                    trip.setTimestampEnd(cursor.getString(4));
                    trip.setAppVersion(cursor.getString(5));
                    trip.setUploadStatus(cursor.getString(6));
                    // Adding contact to list
                    list.add((T) trip);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }
        else if(inst instanceof GPSLocation)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_LOCATIONS;
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    GPSLocation location = new GPSLocation();
                    location.setLocationId(UUID.fromString(cursor.getString(0)));
                    location.setSessionId(UUID.fromString(cursor.getString(1)));
                    location.setTimestamp(cursor.getString(2));
                    location.setLatitude(cursor.getFloat(3));
                    location.setLongitude(cursor.getFloat(4));
                    location.setSpeed(cursor.getFloat(5));
                    location.setBearing(cursor.getFloat(6));
                    location.setAltitude(cursor.getFloat(7));
                    location.setAccuracy(cursor.getFloat(8));
                    location.setUploadStatus(cursor.getString(9));

// Adding contact to list
                    list.add((T) location);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }else if(inst instanceof BluetoothClassicEntry)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_BC;
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    BluetoothClassicEntry bc_entry = new BluetoothClassicEntry();
                    bc_entry.setSessionId(UUID.fromString(cursor.getString(1)));//START FROM 1 SINCE 0 is ID
                    bc_entry.setLocationId(UUID.fromString(cursor.getString(2)));
                    bc_entry.setTimestamp(cursor.getString(3));
                    bc_entry.setMac(cursor.getString(4));
                    bc_entry.setType(cursor.getInt(5));
                    bc_entry.setRssi(cursor.getInt(6));
                    bc_entry.setDeviceName(cursor.getString(7));
                    bc_entry.setBcClass(cursor.getString(8));
                    bc_entry.setUploadStatus(cursor.getString(9));

// Adding contact to list
                    list.add((T) bc_entry);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }
        else if(inst instanceof BluetoothLowEnergyEntry)
        {
            // Select All Query
            selectQuery = "SELECT * FROM " + TABLE_BLE;
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    BluetoothLowEnergyEntry ble_entry = new BluetoothLowEnergyEntry();
                    ble_entry.setSessionId(UUID.fromString(cursor.getString(1)));//START FROM 1 SINCE 0 is ID
                    ble_entry.setLocationId(UUID.fromString(cursor.getString(2)));
                    ble_entry.setTimestamp(cursor.getString(3));
                    ble_entry.setMac(cursor.getString(4));
                    ble_entry.setRssi(cursor.getInt(5));
                    ble_entry.setDeviceName(cursor.getString(6));
                    ble_entry.setBleAdvData(cursor.getString(7));
                    ble_entry.setUploadStatus(cursor.getString(8));

// Adding contact to list
                    list.add((T) ble_entry);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }

        db.close();

// return  list
        return list;
    }


    // Deleting a all
    public <T> void deleteAll(Class<T> cl) throws InstantiationException, IllegalAccessException {
        T inst = cl.newInstance();
        if (inst instanceof Trip)
            getWritableDatabase().execSQL("DELETE FROM " + TABLE_TRIPS + ";");

        else if (inst instanceof GPSLocation)
            getWritableDatabase().execSQL("DELETE FROM " + TABLE_LOCATIONS + ";");

        else if (inst instanceof BluetoothClassicEntry)
            getWritableDatabase().execSQL("DELETE FROM " + TABLE_BC + ";");

        else if (inst instanceof BluetoothLowEnergyEntry)
            getWritableDatabase().execSQL("DELETE FROM " + TABLE_BLE + ";");

    }




    /**
     * Get Sync status of SQLite
     * @return
     */
    public <T> String getSyncStatus(Class<T> cl) throws InstantiationException, IllegalAccessException{
        String msg = null;
        if(this.dbSyncCount(cl) == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed";
        }
        return msg;
    }



    public <T> int dbSyncCount(Class<T> cl) throws InstantiationException, IllegalAccessException{
        T inst = cl.newInstance();
        String selectQuery = "";

        if (inst instanceof Trip)
            selectQuery = "SELECT * FROM " + TABLE_TRIPS + " where "+ KEY_UPLOAD_STATUS +" = '"+"no"+"' or " + KEY_UPLOAD_STATUS +" = '"+"partial"+"'";

        else if (inst instanceof GPSLocation)
            selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " where "+KEY_UPLOAD_STATUS+" = '"+"no"+"'";

        else if (inst instanceof BluetoothClassicEntry)
            selectQuery = "SELECT  * FROM " + TABLE_BC + " where "+KEY_UPLOAD_STATUS+" = '"+"no"+"'";

        else if (inst instanceof BluetoothLowEnergyEntry)
            selectQuery = "SELECT  * FROM " + TABLE_BLE + " where "+KEY_UPLOAD_STATUS+" = '"+"no"+"'";

        int count = 0;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }


    public <T> void updateSyncStatus(Class<T> cl, JSONObject obj) throws InstantiationException, IllegalAccessException, JSONException {
        T inst = cl.newInstance();
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "";

        if (inst instanceof Trip) {
            if (obj.get("status").toString().equals("yes"))
                database.delete(TABLE_TRIPS, KEY_SESSION_ID + "=" + "'" + UUID.fromString(obj.get("session_id").toString()) + "'", null);
            else {
                updateQuery = "UPDATE " + TABLE_TRIPS + " set " +
                        KEY_UPLOAD_STATUS + " = '" + obj.get("status").toString() + "' where " + KEY_SESSION_ID + "=" + "'" + UUID.fromString(obj.get("session_id").toString()) + "'";
                database.execSQL(updateQuery);
            }
        } else if (inst instanceof GPSLocation)
            if (obj.get("status").toString().equals("yes"))
                database.delete(TABLE_LOCATIONS, KEY_LOCATION_ID + "=" + "'" + UUID.fromString(obj.get("location_id").toString()) + "'", null);
            else {
                updateQuery = "UPDATE " + TABLE_LOCATIONS + " set " +
                        KEY_UPLOAD_STATUS + " = '" + obj.get("status").toString() + "' where " + KEY_LOCATION_ID + "=" + "'" + UUID.fromString(obj.get("location_id").toString()) + "'";
                database.execSQL(updateQuery);
            }
        else if (inst instanceof BluetoothClassicEntry)
            if (obj.get("status").toString().equals("yes"))
                database.delete(TABLE_BC, "id=" + obj.getInt("id") + "", null);
            else {
                updateQuery = "UPDATE " + TABLE_BC + " set " +
                        KEY_UPLOAD_STATUS + " = '" + obj.get("status").toString() + "' where id=" + obj.getInt("id") + "";
                database.execSQL(updateQuery);
            }
        else if (inst instanceof BluetoothLowEnergyEntry)
            if (obj.get("status").toString().equals("yes"))
                database.delete(TABLE_BLE, "id=" + obj.getInt("id") + "", null);
            else {
                updateQuery = "UPDATE " + TABLE_BLE + " set " +
                        KEY_UPLOAD_STATUS + " = '" + obj.get("status").toString() + "' where id=" + obj.getInt("id") + "";
                database.execSQL(updateQuery);
            }

        //Log.d("DatabaseTest", updateQuery);
        database.close();
    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    /////////////* TABLE TRIPS   *////////////////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    // Adding new shop
    public void addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, trip.getSessionId().toString());
        values.put(KEY_IMEI, trip.getImei());
        values.put(KEY_TRANSPORT, trip.getTransport());

        values.put(KEY_TIMESTAMP_START, trip.getTimestampStart());
        values.put(KEY_TIMESTAMP_END, trip.getTimestampEnd());
        values.put(KEY_APP_VERSION, trip.getAppVersion());

        values.put(KEY_UPLOAD_STATUS, trip.getUploadStatus());
// Inserting Row
        db.insertOrThrow(TABLE_TRIPS, null, values);
        db.close(); // Closing database connection
    }

    // Updating a trip
    public void updateTripEnd(UUID sessionId,String timestamp) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_TRIPS + " set " + KEY_TIMESTAMP_END + " = '"+ timestamp +"' where "+KEY_SESSION_ID+"="+"'"+ sessionId +"'";
        //Log.d("DatabaseTest", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    // Delete old trips
    public void deleteOldTrips(UUID sessionId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_TRIPS + " where "+KEY_SESSION_ID+"!="+"'"+ sessionId +"' and " + KEY_UPLOAD_STATUS +" != '"+"no"+"'"; //Delete if partial or yes
        //Log.d("DatabaseTest", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
    




    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    /////////////* TABLE LOCATIONS   *////////////////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    // Adding new shop
    public void addLocation(GPSLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_ID, location.getLocationId().toString());
        values.put(KEY_SESSION_ID, location.getSessionId().toString());
        values.put(KEY_TIMESTAMP, location.getTimestamp());
        values.put(KEY_LATITUDE, location.getLatitude());
        values.put(KEY_LONGITUDE, location.getLongitude());
        values.put(KEY_SPEED, location.getSpeed());
        values.put(KEY_BEARING, location.getBearing());
        values.put(KEY_ALTITUDE, location.getAltitude());
        values.put(KEY_ACCURACY, location.getAccuracy());
        values.put(KEY_UPLOAD_STATUS, location.getUploadStatus());

// Inserting Row
        db.insertOrThrow(TABLE_LOCATIONS, null, values);

        db.close(); // Closing database connection
    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    /////////////* TABLE BC   *////////////////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    // Adding new shop
    public void addBcEntry(BluetoothClassicEntry bc_entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, bc_entry.getSessionId().toString());
        values.put(KEY_LOCATION_ID, bc_entry.getLocationId().toString());
        values.put(KEY_TIMESTAMP, bc_entry.getTimestamp());
        values.put(KEY_MAC, bc_entry.getMac());
        values.put(KEY_TYPE, bc_entry.getType());
        values.put(KEY_RSSI, bc_entry.getRssi());
        values.put(KEY_DEVICE_NAME, bc_entry.getDeviceName());
        values.put(KEY_BC_CLASS, bc_entry.getBcClass());
        values.put(KEY_UPLOAD_STATUS, bc_entry.getUploadStatus());

        // Inserting Row
        db.insertOrThrow(TABLE_BC, null, values);
        //Log.d("DatabaseTest1", ""+test);

        db.close(); // Closing database connection
    }


    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    /////////////* TABLE BLE   *////////////////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    // Adding new shop
    public void addBleEntry(BluetoothLowEnergyEntry ble_entry) {
        SQLiteDatabase db = this.getWritableDatabase();

       

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, ble_entry.getSessionId().toString());
        values.put(KEY_LOCATION_ID, ble_entry.getLocationId().toString());
        values.put(KEY_TIMESTAMP, ble_entry.getTimestamp());
        values.put(KEY_MAC, ble_entry.getMac());
        values.put(KEY_RSSI, ble_entry.getRssi());
        values.put(KEY_DEVICE_NAME, ble_entry.getDeviceName());
        values.put(KEY_BLE_ADV_DATA, ble_entry.getBleAdvData());

        values.put(KEY_UPLOAD_STATUS, ble_entry.getUploadStatus());

// Inserting Row
        db.insertOrThrow(TABLE_BLE, null, values);


        db.close(); // Closing database connection
    }

}