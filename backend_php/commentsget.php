<?php


    require_once 'include/DB_Functions.php';
    $db = new DB_Functions();
    
    if (isset($_POST['pid'])){

    	$pid = $_POST['pid'];
    	$comments = $db->getComments($pid);
    }
    

?>