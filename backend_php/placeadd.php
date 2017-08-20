<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['name']) && isset($_POST['address']) && isset($_POST['latitude']) && isset($_POST['longitude']) && isset($_POST['description'])) {

	$name = $_POST['name'];
	$address = $_POST['address'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $description = $_POST['description'];

    // if ($db->isPlaceExisted($name)) {
    //     // user already existed
    //     $response["error"] = TRUE;
    //     $response["error_msg"] = "Place already existed with " . $name;
    //     echo json_encode($response);
    // } else {
    	$place = $db->storePlace($name, $address, $latitude, $longitude, $description);
        	if ($place) {
            	$response["error"] = FALSE;
            	$response["place"]["name"] = $place["name"];
            	$response["place"]["address"] = $place["address"];
            	$response["place"]["latitude"] = $place["latitude"];
            	$response["place"]["longitude"] = $place["longitude"];
            	$response["place"]["description"] = $place["description"];
            	$response["place"]["created_at"] = $place["created_at"];
            
            	echo json_encode($response);
        	} else {
            	// place failed to store
            	$response["error"] = TRUE;
            	$response["error_msg"] = "Unknown error occurred in place registration!";
            	echo json_encode($response);
        	}
        // }
}
else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (name, address, latitude, longitude or description) are missing!";
    echo json_encode($response);
}

?>