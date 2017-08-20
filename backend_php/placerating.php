<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);




if (isset($_POST['pid'])) {

	$pid = $_POST['pid'];

    $rating = $db->placeRating($pid);

	if ($rating) {
        $response["check"] = TRUE;
        $response["rate"] = $rating["SUM(rate)"];

        $num = $db->ratingNum($pid);

        if ($num) {
            $response["rate_num"] = $num["COUNT(rate)"];
        } else {
            $response["check"] = FALSE;
            $response["rate"] = "NA";
        }


        echo json_encode($response);
    } else if ($rating == null){
	        $response["check"] = FALSE;
            $response["rate"] = "NA";
            echo json_encode($response);
        }
        
}
else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter pid is missing!";
    echo json_encode($response);
}


?>