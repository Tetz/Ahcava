<?php
require_once("mysql_config.php");

/**
 * SQL Functions Class For Avacha
 * 
 * @author Tetsuro Takemoto 
 * 
 */
class f_sql{

    private $mysqli;

    public function __construct(){
        //  $this->mysqli = new mysqli(dbhost, dbuname, dbpass, dbname);    
    }

    public function openDB(){
        $this->mysqli = new mysqli(dbhost, dbuname, dbpass, dbname);    
    }

    public function closeDB(){
        $this->mysqli->close();
    }

    //reconnect the database 
    public function reconnectDB(){
        $ping = $this->mysqli->ping();
        if($ping == 0){
            $this->mysqli = new mysqli(dbhost, dbuname, dbpass, dbname); 
        }
    }

    // The onConnect functions
    public function insert_hash($my_hash){
        $query = "INSERT INTO rooms (from_uuid) VALUES (\"$my_hash\")";
        $this->mysqli->query($query);
    }

    // Matching Between Connection ID and UUID.
    public function insert_connection_uuid($connection_id, $uuid){
        $check_query = "SELECT COUNT(*) FROM connection_uuid WHERE uuid=\"$uuid\"";
        $check_result = $this->mysqli->query($check_query);
        $row = $check_result->fetch_array(MYSQLI_BOTH);
        if($row[0] == 0){
            $query = "INSERT INTO connection_uuid (connection_id, uuid) VALUES (\"$connection_id\", \"$uuid\")";
            $this->mysqli->query($query);
        }else if($row[0] > 0){
            $query = "UPDATE connection_uuid SET connection_id=\"$connection_id\" WHERE uuid=\"$uuid\"";
            $this->mysqli->query($query);
        }
    }

    // INSERT NAME
    public function insert_user_name($uuid){
        $query = "SELECT COUNT(*) FROM user_name WHERE uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        if($row[0] == 0){
            $query = "INSERT INTO user_name (uuid) VALUES (\"$uuid\")";
            $this->mysqli->query($query);
        }
    }

    //INSERT Messages
    public function insert_message($uuid, $message){
        $query = "SELECT to_uuid from connection_uuid WHERE uuid=\"$uuid\" and to_uuid!=\"0\" and login_flag=true ORDER BY RAND() LIMIT 1";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        $to_uuid = $row[0];
        if(!empty($to_uuid)){
            $query = "INSERT INTO chat_user_log (uuid, to_uuid, message) VALUES (\"$uuid\", \"$to_uuid\", \"$message\")"; 
            $this->mysqli->query($query);
        }
    }
    // UPDATE connection_uuid
    public function updated_at($uuid){
        $query = "UPDATE connection_uuid SET updated_at=now() WHERE uuid=\"$uuid\"";
        $this->mysqli->query($query);
    }    

    // Login & Logout
    public function login($uuid){
        $query = "UPDATE connection_uuid SET login_flag=true WHERE uuid=\"$uuid\"";
        $this->mysqli->query($query);
    }

    public function logout($connection_id){
        $query = "SELECT uuid FROM connection_uuid WHERE connection_id=\"$connection_id\"";  
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        $uuid = $row[0];
        echo "*********/ " . $uuid . " /**********";
        $query = "UPDATE connection_uuid SET login_flag=false WHERE uuid=\"$uuid\"";
        $this->mysqli->query($query);
    }

    // SELECT Current to_uuid
    public function select_to_uuid($uuid){
        $query = "SELECT to_uuid FROM connection_uuid WHERE uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        return $row[0];
    }

    // INSERT to_uuid
    public function insert_to_uuid($uuid){
        $query = "SELECT uuid from connection_uuid WHERE uuid!=\"$uuid\" and to_uuid=\"0\" and login_flag=true ORDER BY RAND() LIMIT 1";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        $to_uuid = $row[0];
        if(!empty($to_uuid)){
            $query = "UPDATE connection_uuid SET to_uuid=\"$to_uuid\" WHERE uuid=\"$uuid\" and login_flag=true";
            $this->mysqli->query($query);
            $query = "UPDATE connection_uuid SET to_uuid=\"$uuid\" WHERE uuid=\"$to_uuid\" and login_flag=true";
            $this->mysqli->query($query);
            return "login_user";
        }else{
            $query = "SELECT uuid from connection_uuid WHERE uuid!=\"$uuid\" and to_uuid=\"0\" and (to_days(now()) - to_days(updated_at)) < 5 ORDER BY RAND() LIMIT 1";
            $result = $this->mysqli->query($query);
            $row = $result->fetch_array(MYSQLI_BOTH);
            $to_uuid = $row[0];
            //TODO Don't Update to_uuid
            $query = "UPDATE connection_uuid SET to_uuid=\"$to_uuid\" WHERE uuid=\"$uuid\" ";
            $this->mysqli->query($query);
            $query = "UPDATE connection_uuid SET to_uuid=\"$uuid\" WHERE uuid=\"$to_uuid\" ";
            $this->mysqli->query($query);
            //TODO Push Notifications
            Push($to_uuid);
            return "logout_user";
        }    
    }
    // Check the user whether login or logout
    public function login_or_logout($uuid){
        $query = "SELECT login_flag from connection_uuid WHERE uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        $login_flag = $row[0];
        if($login_flag == 1){
            return "login_user";
        }else{
            return "logout_user";
        }    
    }

    // Reset her to_uuid
    public function reset_her_to_uuid($uuid){
        $query = "UPDATE connection_uuid SET to_uuid=0 WHERE to_uuid=\"$uuid\"";
        $this->mysqli->query($query);
    }

    // Reset my to_uuid
    public function reset_my_to_uuid($uuid){
        $query = "UPDATE connection_uuid SET to_uuid=0 WHERE uuid=\"$uuid\"";
        $this->mysqli->query($query);
    }

    // Get Connection ID
    public function get_connections($uuid){
        $query = "SELECT connection_id FROM connection_uuid WHERE to_uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        return $row[0];
    }

    // Counter of Good Action
    public function update_action($uuid){
        $query = "SELECT to_uuid FROM connection_uuid WHERE uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        $to_uuid = $row[0];
        $query = "UPDATE connection_uuid SET good_count=good_count+1 WHERE uuid=\"$to_uuid\"";
        $this->mysqli->query($query);
    }

    //  Get Loing Flag
    public function get_login_flag($to_uuid){
        $query = "SELECT login_flag FROM connection_uuid WHERE uuid=\"$to_uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        return $row[0];
    } 

    // Get the number of actions
    public function get_good_data($uuid){
        $query = "SELECT good_count FROM connection_uuid WHERE to_uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        return $row[0];
    }

    // Get My Name
    public function get_my_name($uuid){
        $query = "SELECT name FROM user_name WHERE uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        return $row[0];
    }

    // Get Her Name 
    public function get_her_name($uuid){
        $query = "SELECT to_uuid FROM connection_uuid WHERE uuid=\"$uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        $to_uuid = $row[0];

        $query = "SELECT name FROM user_name WHERE uuid=\"$to_uuid\"";
        $result = $this->mysqli->query($query);
        $row = $result->fetch_array(MYSQLI_BOTH);
        return $row[0];
    }

    // Functions For Name
    public function update_my_name($name,$uuid){
        $query = "UPDATE user_name SET name=\"$name\" WHERE uuid=\"$uuid\" ";
        $result = $this->mysqli->query($query);
    }



    // Functions For ChatLogHandler ----------------------------------------------------------------------------------

    public function get_user_log($uuid){
        $query = "SELECT to_uuid, max(created_at) as date FROM chat_user_log WHERE uuid=\"$uuid\" GROUP BY to_uuid ORDER BY DATE(created_at) ASC LIMIT 5";  
        // $query = "SELECT to_uuid, max(created_at) as date FROM chat_user_log WHERE uuid=\"a\" AND uuid!=\"0\" GROUP BY to_uuid ORDER BY DATE(created_at) ASC LIMIT 5";  
        $result = $this->mysqli->query($query);
        $i = 0;
        $user_log = array();
        while($row = $result->fetch_array(MYSQLI_ASSOC)){
            $user_log[$i] = $row;
            $i++;
        }
        return $user_log; 
    }

    // Function For Avatar images ----------------------------------------------------------------------------------
    public function insert_parts_name($uuid, $hair, $face, $body){
        $query = "INSERT INTO avatar_images (uuid, hair, face, body) VALUES (\"$uuid\", \"$hair\", \"$face\", \"$body\")";
        $this->mysqli->query($query);
    }

    public function select_parts_name($uuid){
        $query = "SELECT hair, face, body FROM avatar_images WHERE uuid=\"$uuid\" ORDER BY created_at DESC LIMIT 1";
        $result = $this->mysqli->query($query);
        $i = 0;
        $parts_name = array();
        while($row = $result->fetch_array(MYSQLI_ASSOC)){
            $parts_name[$i] = $row;
            $i++;
        }
        return $parts_name;
    }

    // Functions For Statistics  ----------------------------------------------------------------------------------
    public function insert_login_date($uuid){
        $query = "INSERT INTO login (uuid) VALUES (\"$uuid\")";
        $this->mysqli->query($query);
    }


}// The end of f_sql class



?>
