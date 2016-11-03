<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
$db = new DB_Functions(); 
//Get JSON posted by Android Application
$json = $_POST["bcJSON"];
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
$res = $db->storeBc($data[$i]->session_id,$data[$i]->location_id,$data[$i]->timestamp,$data[$i]->mac,$data[$i]->type,
			$data[$i]->rssi,$data[$i]->device_name,$data[$i]->bc_class);
	//Based on inserttion, create JSON response
	if($res){
		$b["id"] = $data[$i]->id;
		$b["status"] = 'yes';
		array_push($a,$b);
	}else{
		$b["id"] = $data[$i]->id;
		$b["status"] = 'no';
		array_push($a,$b);
	}
}
//Post JSON response back to Android Application
echo json_encode($a);
?>
