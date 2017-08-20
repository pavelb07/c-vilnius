<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['uid']) && isset($_POST['pid'])&& isset($_POST['comment'])) {

	$uid = $_POST['uid'];
	$pid = $_POST['pid'];
    $comment = $_POST['comment'];

	$commentary = $db->commentPlace($uid,$pid, $comment);
	if ($commentary) {
    	$response["error"] = FALSE;
    	$response["commentary"]["uid"] = $commentary["uid"];
    	$response["commentary"]["pid"] = $commentary["pid"];
        $response["commentary"]["comment"] = $commentary["comment"];
            
    	echo json_encode($response);
	} else {
    	$response["error"] = TRUE;
        $response["error_msg"] = "Unknown error occurred when adding commentary!";
    	echo json_encode($response);
	}
        
}
else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (uid,pid, comment) are missing!";
    echo json_encode($response);
}


?>