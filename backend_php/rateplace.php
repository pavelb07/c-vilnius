<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['pid']) && isset($_POST['uid']) && isset($_POST['rate'])) {

	$pid = $_POST['pid'];
    $uid = $_POST['uid'];
    $rate = $_POST['rate'];

	if ($db->isRated($uid,$pid)) {
        $rating = $db->updateRate($uid, $pid, $rate);
        if ($rating) {
            $response["error"] = FALSE;
                $response["rating"]["rate"] = $rating["rate"];
                $response["rating"]["uid"] = $rating["uid"];
                $response["rating"]["pid"] = $rating["pid"];
                $response["info_msg"] = "Updated rating!";
                echo json_encode($response);
            }
                else{
                    $response["error"] = TRUE;
                    $response["error_msg"] = "Unknown error occurred when rating!";
                    echo json_encode($response);
                }
        }
    else {
		$rating = $db->ratePlace($uid, $pid, $rate);
        	if ($rating) {
            	$response["error"] = FALSE;
                $response["rating"]["rate"] = $rating["rate"];
            	$response["rating"]["uid"] = $rating["uid"];
            	$response["rating"]["pid"] = $rating["pid"];
                $response["info_msg"] = "Rated, thank you!";
            
            	echo json_encode($response);
        	} else {
            	$response["error"] = TRUE;
            	$response["error_msg"] = "Unknown error occurred when rating!";
            	echo json_encode($response);
        	}
        }
        
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (uid,pid, rate) are missing!";
    echo json_encode($response);
}


?>