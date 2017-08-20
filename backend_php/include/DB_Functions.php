<?php


class DB_Functions {

    private $conn;

    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {
        
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($username, $name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt

        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, username, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("ssssss", $uuid, $username, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }

    public function getUsername($uid){
        $stmt = $this->conn->prepare("SELECT username FROM users WHERE unique_id = ?");
            $stmt->bind_param("s", $uid);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            if ($user){
                return $user;
            } else {
                return false;
            }
            
    }

    public function storePlace($name, $address, $latitude, $longitude, $description) {
     
        $stmt = $this->conn->prepare("INSERT INTO places(name, address, latitude, longitude, description, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("ssdds", $name, $address, $latitude, $longitude, $description);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM places WHERE name = ?");
            $stmt->bind_param("s", $name);
            $stmt->execute();
            $place = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $place;
        } else {
            return false;
        }
    }

    public function addFavorites($uid,$pid){
        $stmt = $this->conn->prepare("INSERT INTO favorites(uid,pid) VALUES(?, ?)");
        $stmt->bind_param("si", $uid, $pid);

        $result = $stmt->execute();
        $stmt->close();

        if ($result){
            $stmt = $this->conn->prepare("SELECT * FROM favorites WHERE uid = ? and pid = ?");
            $stmt->bind_param("si", $uid, $pid);
            $stmt->execute();
            $favorite = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $favorite;
        } else {
            return false;
        }
    }

    public function commentPlace($uid, $pid, $comment){
        
        $stmt = $this->conn->prepare("INSERT INTO comment(commentary, post_date, uid, pid) VALUES(?, NOW(), ?, ?)");
        $stmt->bind_param("ssi", $comment, $uid, $pid);
        $result = $stmt->execute();
        $stmt->close();

        

        if ($result){
            $stmt = $this->conn->prepare("SELECT * FROM comment WHERE uid = ? and pid = ? ORDER BY post_date DESC");
            $stmt->bind_param("si", $uid, $pid);
            $stmt->execute();
            $rating = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $comment;
        } else {
            
            return false;
        }

    }

    public function ratePlace($uid, $pid, $rate){
        
        $stmt = $this->conn->prepare("INSERT INTO ratings(rate, uid, pid) VALUES(?, ?, ?)");
        $stmt->bind_param("dsi", $rate, $uid, $pid);
        $result = $stmt->execute();
        $stmt->close();

        

        if ($result){
            $stmt = $this->conn->prepare("SELECT * FROM ratings WHERE uid = ? and pid = ?");
            $stmt->bind_param("si", $uid, $pid);
            $stmt->execute();
            $rating = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $rating;
        } else {
            
            return false;
        }

    }

    public function updateRate($rate, $uid, $pid){
        $stmt = $this->conn->prepare("UPDATE ratings SET rate = ? WHERE uid = ? and pid = ?");
        $stmt->bind_param("dsi", $rate, $uid, $pid);
        $result = $stmt->execute();
        $stmt->close();

         if ($result){
            $stmt = $this->conn->prepare("SELECT * FROM ratings WHERE uid = ? and pid = ?");
            $stmt->bind_param("si", $uid, $pid);
            $stmt->execute();
            $rating = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $rating;
        } else {
            
            return false;
        }

    }

    public function isRated($uid,$pid) {
        $stmt = $this->conn->prepare("SELECT * from ratings WHERE uid = ? and pid = ?");

        $stmt->bind_param("si", $uid,$pid);

        $result = $stmt->execute();

        if ($result) {
            
            $rating = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $rating;
        } else {
            // not rated
            return false;
        }
    }

    public function placeRating($pid) {
        $stmt = $this->conn->prepare("SELECT SUM(rate) FROM ratings WHERE pid = ?");
        $stmt->bind_param("i", $pid);
        $stmt->execute();
        $rating = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        if ($rating) {
            return $rating;
        } else {
            return false;
        }
    }

    public function ratingNum($pid) {
        $stmt = $this->conn->prepare("SELECT COUNT(rate) FROM ratings WHERE pid = ?");
        $stmt->bind_param("i", $pid);
        $stmt->execute();
        $num = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        if ($num) {
            return $num;
        } else {
            return false;
        }
    }
    


    /**
     * Get user by username and password
     */
    public function getUserByUsernameAndPassword($username, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");

        $stmt->bind_param("s", $username);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Get places from database
     */

    public function getPlaces()
    {
        $pdo = new PDO('mysql:host=localhost;dbname=android_api', 'root', 'Brilliant7');
        $query = $pdo->prepare('SELECT * FROM places');
        $query->execute();
        $results = $query->fetchAll(PDO::FETCH_ASSOC);  
        $json = json_encode( $results, JSON_UNESCAPED_UNICODE );

        echo $json;

        return $json;
    }

     public function getComments($pid)
    {
        $pdo = new PDO('mysql:host=localhost;dbname=android_api', 'root', 'Brilliant7');
        $query = $pdo->prepare('SELECT * FROM comment WHERE pid ='. $pid);
        $query->execute();
        $results = $query->fetchAll(PDO::FETCH_ASSOC);  
        $json = json_encode( $results, JSON_UNESCAPED_UNICODE );

        echo $json;

        return $json;
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($username) {
        $stmt = $this->conn->prepare("SELECT username from users WHERE username = ?");

        $stmt->bind_param("s", $username);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }

    public function isPlaceExisted($name) {
        $stmt = $this->conn->prepare("SELECT name from place WHERE name = ?");

        $stmt->bind_param("s", $name);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // place existed 
            $stmt->close();
            return true;
        } else {
            // place not existed
            $stmt->close();
            return false;
        }
    }

    public function isFavoriteExisted($uid,$pid) {
        $stmt = $this->conn->prepare("SELECT * FROM favorites WHERE uid = ? and pid = ?");

        $stmt->bind_param("si", $uid,$pid);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // place existed 
            $stmt->close();
            return true;
        } else {
            // place not existed
            $stmt->close();
            return false;
        }
    }

    public function deleteFavorites($uid,$pid){
        $stmt = $this->conn->prepare("DELETE FROM favorites WHERE uid = ? and pid = ?");

        $stmt->bind_param("si", $uid,$pid);

        $stmt->execute();

        $result = $stmt->execute();
        $stmt->close();
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}

?>
