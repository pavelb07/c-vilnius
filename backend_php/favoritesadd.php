<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['uid']) && isset($_POST['pid'])) {

	$uid = $_POST['uid'];
	$pid = $_POST['pid'];

	if ($db->isFavoriteExisted($uid,$pid)) {
        // user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "Favorites already added";
        echo json_encode($response);
    } else {
		$favorite = $db->addFavorites($uid,$pid);
        	if ($favorite) {
            	$response["error"] = FALSE;
            	$response["favorite"]["uid"] = $favorite["uid"];
            	$response["favorite"]["pid"] = $favorite["pid"];
            
            	echo json_encode($response);
        	} else {
            	// place failed to store
            	$response["error"] = TRUE;
            	$response["error_msg"] = "Unknown error occurred when adding to favorites!";
            	echo json_encode($response);
        	}
        }
        
}
else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (uid,pid) are missing!";
    echo json_encode($response);
}


?>