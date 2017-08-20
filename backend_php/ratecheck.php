<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['pid']) && isset($_POST['uid'])) {

	$pid = $_POST['pid'];
	$uid = $_POST['uid'];

	$rating = $db->isRated($uid,$pid);

	if ($rating) {
        $response["check"] = TRUE;
        $response["rate"] = $rating["rate"];

        echo json_encode($response);
    } else {
    	 $response["check"] = FALSE;
    	 echo json_encode($response);
    	}
} else{
	echo "Parametrs are missing";
}

?>