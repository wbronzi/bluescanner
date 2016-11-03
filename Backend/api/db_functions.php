<?php

class DB_Functions {

    private $db;

    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {}

    /**
     * Storing new user
     * returns user details
     */
    public function storeTrip($SessionId,$Imei,$Transport,$TimestampStart,$TimestampEnd,$AppVersion) {
        // Insert user into database
        $result = mysql_query("INSERT INTO trips VALUES('$SessionId','$Imei','$Transport',NULL,'$TimestampStart','$TimestampEnd','$AppVersion') ON DUPLICATE KEY UPDATE timestamp_end = '$TimestampEnd'");
	
	if ($result) {
			return true;
        } else {
			if( mysql_errno() == 1062) {
				// Duplicate key - Primary Key Violation
				return true;
			} else {
				// For other errors
				return false;
			}            
        }
    }
	 /**
     * Getting all users
     */
    public function getAllTrips() {
        $result = mysql_query("select * FROM trips");
        return $result;
    }
	
    public function getTotalTrips($Imei) {
        $result = mysql_query("SELECT * FROM trips WHERE imei = '$Imei' AND TIMESTAMPDIFF(MINUTE,timestamp_start,timestamp_end) > 10");
		
			
		$num_rows = mysql_num_rows($result);
        return $num_rows;
    }


/**
     * Storing new location
     * returns user details
     */
    public function storeLocation($LocationId,$SessionId,$Timestamp,$Latitude,$Longitude,$Speed,$Bearing,$Altitude,$Accuracy) {
        // Insert user into database
        $result = mysql_query("INSERT INTO locations VALUES('$LocationId','$SessionId','$Timestamp',$Latitude,$Longitude,$Speed,$Bearing,$Altitude,$Accuracy,NULL,NULL)");

   
        if ($result) {
                        return true;
        } else {
                        if( mysql_errno() == 1062) {
                                // Duplicate key - Primary Key Violation
                                return true;
                        } else {
                                // For other errors
                                return false;
                        }
        }
    }
         /**
     * Getting all users
     */
    public function getAllLocations() {
        $result = mysql_query("select * FROM locations");
        return $result;
    }


	/**
     * Storing new bc entry
     * returns user details
     */
    public function storeBc($SessionId,$LocationId,$Timestamp,$Mac,$Type,$RSSI,$DeviceName,$BcClass) {
        // Insert user into database
		//make sure characters are properly handled
		$DeviceName = mysql_real_escape_string($DeviceName);
		
	$query = "INSERT INTO bc VALUES(NULL,'$SessionId','$LocationId','$Timestamp','$Mac',$Type,$RSSI,'$DeviceName','$BcClass',NULL)";
	$result = mysql_query($query);

        
		
        if ($result) {
                        return true;
        } else {
                        if( mysql_errno() == 1062) {
                                // Duplicate key - Primary Key Violation
                                return true;
                        } else {
                                // For other errors
                                return false;
                        }
        }
    }
         /**
     * Getting all users
     */
    public function getAllBc() {
        $result = mysql_query("select * FROM bc");
        return $result;
    }


public function storeBle($SessionId,$LocationId,$Timestamp,$Mac,$RSSI,$DeviceName,$BleAdvData) {
        // Insert user into database
		//make sure characters are properly handled
		$DeviceName = mysql_real_escape_string($DeviceName);
		
        $query = "INSERT INTO ble VALUES(NULL,'$SessionId','$LocationId','$Timestamp','$Mac',$RSSI,'$DeviceName','$BleAdvData',NULL)";
        $result = mysql_query($query);

       

        if ($result) {
                        return true;
        } else {
                        if( mysql_errno() == 1062) {
                                // Duplicate key - Primary Key Violation
                                return true;
                        } else {
                                // For other errors
                                return false;
                        }
        }
    }
         /**
     * Getting all users
     */
    public function getAllBle() {
        $result = mysql_query("select * FROM ble");
        return $result;
    }



}

?>
