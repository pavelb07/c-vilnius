<?php


    require_once 'include/DB_Functions.php';
    $db = new DB_Functions();
    
    if (isset($_POST['uid'])) {

    $uid = $_POST['uid'];


    $username = $db->getUsername($uid);

	if ($username) {
        $response["error"] = FALSE;
            $response["user"]["username"] = $username["username"];
                echo json_encode($response);
            }
                else{
                    $response["error"] = TRUE;
                    $response["error_msg"] = "Unknown error occurred when getting username!";
                    echo json_encode($response);
                }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter uid is missing!";
    echo json_encode($response);
}


?>