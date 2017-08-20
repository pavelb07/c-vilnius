<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['uid']) && isset($_POST['pid'])) {

	$uid = $_POST['uid'];
	$pid = $_POST['pid'];

	if ($db->isFavoriteExisted($uid,$pid)) {
        
        $response["error"] = TRUE;
        echo json_encode($response);
         } else {
    		$response["error"] = FALSE;
    		echo json_encode($response);
    		}
}

?>