<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
$db = new DB_Functions(); 
//Get JSON posted by Android Application
$json = $_POST["tripsJSON"];
//Remove Slashes
if (get_magic_quotes_gpc()){
$json = stripslashes($json);
}
//Decode JSON into an Array
$data = json_decode($json);
//Util arrays to create response JSON
$a=array();
$b=array();
//Loop through an Array and insert data read from JSON into MySQL DB
for($i=0; $i<count($data) ; $i++)
{
//Store Trip into MySQL DB
$res = $db->storeTrip($data[$i]->session_id,$data[$i]->imei,$data[$i]->transport,$data[$i]->timestamp_start,$data[$i]->timestamp_end,$data[$i]->app_version);
	//Based on inserttion, create JSON response
	if($res){
		if($data[$i]->timestamp_end == 0){
			$b["session_id"] = $data[$i]->session_id;
                        $b["status"] = 'partial';
                        array_push($a,$b);
		
		}else{
			$b["session_id"] = $data[$i]->session_id;
			$b["status"] = 'yes';
			array_push($a,$b);
		}
	}else{
		$b["session_id"] = $data[$i]->session_id;
		$b["status"] = 'no';
		array_push($a,$b);
	}
}
//Post JSON response back to Android Application
echo json_encode($a);
?>
