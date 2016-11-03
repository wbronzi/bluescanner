<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
$db = new DB_Functions(); 
//Get JSON posted by Android Application
$json = $_POST["locationsJSON"];
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
$res = $db->storeLocation($data[$i]->location_id,$data[$i]->session_id,$data[$i]->timestamp,$data[$i]->latitude,$data[$i]->longitude,$data[$i]->speed
			,$data[$i]->bearing,$data[$i]->altitude,$data[$i]->accuracy);
	//Based on inserttion, create JSON response
	if($res){
		$b["location_id"] = $data[$i]->location_id;
		$b["status"] = 'yes';
		array_push($a,$b);
	}else{
		$b["location_id"] = $data[$i]->location_id;
		$b["status"] = 'no';
		array_push($a,$b);
	}
}
//Post JSON response back to Android Application
echo json_encode($a);
?>
