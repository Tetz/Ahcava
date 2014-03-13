#!/php -q
<?php

require_once("websocket.server.php");
require_once("f_sql.php");
require_once("Push.php");

/**
 *  Version Handler 
 *
 * @author T.Tetsuro
 *
 */
class VersionHandler extends WebSocketUriHandler {
    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        //ver1.0(MON Jun 10)
        //ver1.04(Fri Jul 5)
        //ver1.05(Fri Jul 5)
        $current_version = "1.05";
        $json_array = json_decode($msg->getData(), true);
        $json_array["my_avatar"][0]["version"] = $current_version;
        $msg->setData(json_encode($json_array));
        $user->sendMessage($msg);
    }
}


/**
 * Avacha Handler
 *
 * @author T.Tetsuro
 *
 */
class AvachaHandler extends WebSocketUriHandler {

    private $f_sql;

    public function __construct(){
        parent::__construct();
        $this->f_sql = new f_sql;
    }

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        // Preparation of Sending Json data to my own
        $me = $user;
        // open DB
        $this->f_sql->openDB();

        $this->say("[ECHO] " . strlen($msg->getData()) . " bytes");
        // Debug
        $this->say("AvachaHandler->onMessage()");
        // INSERT Connection ID and UUID
        $json_array = json_decode($msg->getData(), true);
        $text_msg = $json_array["my_avatar"][0]["message"];
        $uuid = $json_array["my_avatar"][0]["uuid"];
        $action = $json_array["my_avatar"][0]["action"];
        $change_user = $json_array["my_avatar"][0]["change_user"];
        // Edit JSON Format 
        $json_array["my_avatar"][0]["good_count"] = $this->f_sql->get_good_data($uuid);
        $json_array["my_avatar"][0]["her_name"] = $this->f_sql->get_my_name($uuid); //On Her Client, the her name is my name.

        $this->say($uuid);
        $this->say($action);
        $this->say($change_user);

        if(!empty($uuid) && $change_user == "onOpen"){
            //Ini
            $this->f_sql->insert_connection_uuid($user->getId(), $uuid);
            //Login
            $this->f_sql->login($uuid);
            // When the connection is established, the client send JSON to the server. 
            if($change_user == "onOpen"){
                //SELECT UUID
                $to_uuid = $this->f_sql->select_to_uuid($uuid);
                if($to_uuid != "0"){
                    //rebuild JSON Data
                    $parts_name = $this->f_sql->select_parts_name($to_uuid);
                    $in_or_out = $this->f_sql->login_or_logout($to_uuid);
                    // put her name
                    if($this->f_sql->get_her_name($uuid) != null){
                        $json_array["my_avatar"][0]["her_name"] = $this->f_sql->get_her_name($uuid); 
                    }
                    echo "\n" . $in_or_out . "\n";
                    if(count($parts_name) > 0){
                        $json_array["my_avatar"][0]["my_hair_img"] = $parts_name[0]["hair"];
                        $json_array["my_avatar"][0]["my_face_img"] = $parts_name[0]["face"];
                        $json_array["my_avatar"][0]["my_body_img"] = $parts_name[0]["body"];
                        // When Logout User Matching
                        if($in_or_out == "logout_user"){
                            // Show Busy Status
                            $json_array["my_avatar"][0]["my_hair_img"] = "1210ef07_73_trimmed.png";   
                            $json_array["my_avatar"][0]["my_face_img"] = "1210ef07_73_trimmed.png";
                            $json_array["my_avatar"][0]["my_body_img"] = "1210ef12_74_trimmed.png";
                        }
                    }else {
                        //Set Default Image Name
                        $json_array["my_avatar"][0]["my_hair_img"] = "1210ef07_73_trimmed.png";   
                        $json_array["my_avatar"][0]["my_face_img"] = "1210ef07_73_trimmed.png";
                        $json_array["my_avatar"][0]["my_body_img"] = "1210ef07_73_trimmed.png";
                        $json_array["my_avatar"][0]["her_name"] = "見つかりませんでした。";    
                    }
                    $msg->setData(json_encode($json_array));
                    $me->sendMessage($msg); 
                }
            }
        }

        if(!empty($uuid) && $change_user != "onOpen"){ 
            //INSERT Login date 
            $this->f_sql->insert_login_date($uuid);
            //UPDATE Login Date
            $this->f_sql->updated_at($uuid); 
            //Ini
            $this->f_sql->insert_connection_uuid($user->getId(), $uuid);
            $this->f_sql->insert_user_name($uuid);
            //Login
            $this->f_sql->login($uuid);


            if($change_user == "random"){ // Change User Button -------------------------------------
                // INSERT Avatar Parts Name
                $hair = $json_array["my_avatar"][0]["my_hair_img"];  
                $face = $json_array["my_avatar"][0]["my_face_img"];
                $body = $json_array["my_avatar"][0]["my_body_img"];
                $this->f_sql->insert_parts_name($uuid, $hair, $face, $body);

                //JSON Encode
                $msg->setData(json_encode($json_array));
                //Reset my to_uuid
                $this->f_sql->reset_my_to_uuid($uuid);
                //SELECT UUID
                $check_uuid = $this->f_sql->select_to_uuid($uuid);
                //Reset her to_uuid
                $this->f_sql->reset_her_to_uuid($uuid); 

                if($check_uuid == 0){ 
                    //INSERT to_uuid
                    $login_or_logout = $this->f_sql->insert_to_uuid($uuid);
                    echo "\n" . $login_or_logout . "\n";
                }
                // Get the Connection ID the user send to
                $to_connection = intval($this->f_sql->get_connections($uuid));        

                //Send JSON data to the user.
                foreach ($this->users as $user){
                    if($user->getId() == $to_connection){
                        $user->sendMessage($msg);
                    }
                }
                if($check_uuid == 0){
                    // SELECT Avatar Parts Name
                    $to_uuid = $this->f_sql->select_to_uuid($uuid); 
                    //rebuild JSON Data
                    $parts_name = $this->f_sql->select_parts_name($to_uuid);
                    var_dump($parts_name);
                    $json_array = json_decode($msg->getData(), true);
                    if($this->f_sql->get_her_name($uuid) != null){
                        $json_array["my_avatar"][0]["her_name"] = $this->f_sql->get_her_name($uuid); 
                    }
                    if(count($parts_name) > 0){
                        $json_array["my_avatar"][0]["my_hair_img"] = $parts_name[0]["hair"];
                        $json_array["my_avatar"][0]["my_face_img"] = $parts_name[0]["face"];
                        $json_array["my_avatar"][0]["my_body_img"] = $parts_name[0]["body"];
                        // When Logout User Matching
                        if($login_or_logout == "logout_user"){
                            // Show Busy Status
                            $json_array["my_avatar"][0]["my_hair_img"] = "1210ef07_73_trimmed.png";   
                            $json_array["my_avatar"][0]["my_face_img"] = "1210ef07_73_trimmed.png";
                            $json_array["my_avatar"][0]["my_body_img"] = "1210ef12_74_trimmed.png";
                        }
                    }else {
                        //Set Default Image Name
                        $json_array["my_avatar"][0]["my_hair_img"] = "1210ef07_73_trimmed.png";   
                        $json_array["my_avatar"][0]["my_face_img"] = "1210ef07_73_trimmed.png";
                        $json_array["my_avatar"][0]["my_body_img"] = "1210ef07_73_trimmed.png";
                        $json_array["my_avatar"][0]["her_name"] = "見つかりませんでした。";    
                    }
                    $msg->setData(json_encode($json_array));
                    $me->sendMessage($msg); 
                } 
            } else if($change_user == "reset"){
                //Set Default Image Name
                $json_array["my_avatar"][0]["my_hair_img"] = "1210ef07_73_trimmed.png";   
                $json_array["my_avatar"][0]["my_face_img"] = "1210ef07_73_trimmed.png";
                $json_array["my_avatar"][0]["my_body_img"] = "1210ef07_73_trimmed.png";
                $msg->setData(json_encode($json_array));

                // Get the Connection ID the user send to
                $to_connection = intval($this->f_sql->get_connections($uuid));        

                //Send JSON data to the user.
                foreach ($this->users as $user){
                    if($user->getId() == $to_connection){
                        $user->sendMessage($msg);
                    }
                }
                $this->f_sql->reset_my_to_uuid($uuid);
                $this->f_sql->reset_her_to_uuid($uuid);
            }    

            if($text_msg != ""){ // Send Message ---------------------------------------------------- 
                $json_array["my_avatar"][0]["to_uuid"] = $this->f_sql->select_to_uuid($uuid);
                $msg->setData(json_encode($json_array));
                // Get the Connection ID the user send to
                $to_connection = intval($this->f_sql->get_connections($uuid));        

                // INSERT Messages
                $this->f_sql->insert_message($uuid, $text_msg);

                //Send JSON data to the user.
                foreach ($this->users as $user){
                    if($user->getId() == $to_connection){
                        $user->sendMessage($msg);
                    }
                }
            }

            if($action == "good"){

                $msg->setData(json_encode($json_array));
                //Counter of Good Action
                $this->f_sql->update_action($uuid);

                // Get the Connection ID the user send to
                $to_connection = intval($this->f_sql->get_connections($uuid));        

                //Send JSON data to the user.
                foreach ($this->users as $user){
                    if($user->getId() == $to_connection){
                        $user->sendMessage($msg);
                    }
                }
            }
        }

        // close DB
        $this->f_sql->closeDB(); 
    }


    public function onAdminMessage(IWebSocketConnection $user, IWebSocketMessage $obj) {
        $this->say("[DEMO] Admin TEST received!");
        $frame = WebSocketFrame::create(WebSocketOpcode::PongFrame);
        $user->sendFrame($frame);
    }

}

/**
 * ChatLog Handler 
 *
 * @author T.Tetsuro
 *
 */
class ChatLogHandler extends WebSocketUriHandler {

    private $f_sql;

    public function __construct(){
        parent::__construct();
        $this->f_sql = new f_sql;  
    } 

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        // open DB 
        $this->f_sql->openDB(); 
        // Debug
        $this->say("ChatLogHandler->onMessage()");
        // INSERT Connection ID and UUID
        $json_array = json_decode($msg->getData(), true);
        $uuid = $json_array["my_avatar"][0]["uuid"];

        // Get User Log
        $user_log = array();
        $user_log = $this->f_sql->get_user_log($uuid);
        var_dump($user_log);

        // Set Values To Array
        if($user_log[0]["date"] != null){
            $json_array["my_avatar"][0]["user_1"]["name"] = $this->f_sql->get_my_name($user_log[0]["to_uuid"]);
            $json_array["my_avatar"][0]["user_1"]["date"] = $user_log[0]["date"];
        }else{ 
            $json_array["my_avatar"][0]["user_1"]["name"] = "";
            $json_array["my_avatar"][0]["user_1"]["date"] = "";
        }  

        if($user_log[1]["date"] != null){
            $json_array["my_avatar"][0]["user_2"]["name"] = $this->f_sql->get_my_name($user_log[1]["to_uuid"]);
            $json_array["my_avatar"][0]["user_2"]["date"] = $user_log[1]["date"];
        }else{
            $json_array["my_avatar"][0]["user_2"]["name"] = ""; 
            $json_array["my_avatar"][0]["user_2"]["date"] = "";
        } 

        if($user_log[2]["date"] != null){
            $json_array["my_avatar"][0]["user_3"]["name"] = $this->f_sql->get_my_name($user_log[2]["to_uuid"]);
            $json_array["my_avatar"][0]["user_3"]["date"] = $user_log[2]["date"];
        }else{
            $json_array["my_avatar"][0]["user_3"]["name"] = "";
            $json_array["my_avatar"][0]["user_3"]["date"] = "";
        } 

        if($user_log[3]["date"] != null){
            $json_array["my_avatar"][0]["user_4"]["name"] = $this->f_sql->get_my_name($user_log[3]["to_uuid"]);
            $json_array["my_avatar"][0]["user_4"]["date"] = $user_log[3]["date"];
        }else{
            $json_array["my_avatar"][0]["user_4"]["name"] = "";
            $json_array["my_avatar"][0]["user_4"]["date"] = "";
        } 

        if($user_log[4]["date"] != null){
            $json_array["my_avatar"][0]["user_5"]["name"] = $this->f_sql->get_my_name($user_log[4]["to_uuid"]);
            $json_array["my_avatar"][0]["user_5"]["date"] = $user_log[4]["date"];
        }else{
            $json_array["my_avatar"][0]["user_5"]["name"] = "";
            $json_array["my_avatar"][0]["user_5"]["date"] = "";
        } 

        $msg->setData(json_encode($json_array));

        //Send JSON to the client
        $user->sendMessage($msg); 

        // close DB
        $this->f_sql->closeDB();
    }

}

/**
 *  Handler 
 *
 * @author T.Tetsuro
 *
 */
class NameHandler extends WebSocketUriHandler {

    private $f_sql;

    public function __construct(){
        parent::__construct();
        $this->f_sql = new f_sql;// Debug
    } 

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        // open DB
        $this->f_sql->openDB();

        $this->say("NameHandler->onMessage()");
        // INSERT Connection ID and UUID
        $json_array = json_decode($msg->getData(), true);
        $name = $json_array["my_avatar"][0]["name"];
        $return_name = $json_array["my_avatar"][0]["return_name"];
        $uuid = $json_array["my_avatar"][0]["uuid"];
        // Edit JSON Data
        $json_array["my_avatar"][0]["name"] = $this->f_sql->get_my_name($uuid);
        $msg->setData(json_encode($json_array));

        if($return_name == "return"){

            // Send My Name From Database
            $user->sendMessage($msg); 
        }else if($return_name == "none"){
            // UPDATE NAME
            $this->f_sql->update_my_name($name, $uuid);
        }

        //debug
        $this->say($name);
        $this->say($uuid);
        $this->say($return_name);
        // close DB
        $this->f_sql->closeDB();
    }

}


/**
 * Demo socket server. Implements the basic eventlisteners and attaches a resource handler for /echo/ urls.
 *
 *
 * @author Chris
 *
 */
class DemoSocketServer implements IWebSocketServerObserver {

    protected $debug = true;
    protected $server;
    private $f_sql;
    private $mysqli;
    private $host;
    private $ip;

    public function __construct() {
        $this->host = 'ahcavaip';
        $this->ip = gethostbyname($this->host);
        $this->f_sql = new f_sql;
        $this->server = new WebSocketServer("tcp://" . $this->ip . ":9000", 'superdupersecretkey');
        $this->server->addObserver($this);
        $this->server->addUriHandler("echo", new AvachaHandler());
        $this->server->addUriHandler("log", new ChatLogHandler());
        $this->server->addUriHandler("name", new NameHandler());
        $this->server->addUriHandler("version", new VersionHandler());

    }

    public function onConnect(IWebSocketConnection $user) {
        // open DB
        $this->f_sql->openDB();

        $this->say("DemoSocketServer->onConnect()");
        $this->say("[DEMO] {$user->getId()} connected");
        // INSERT Hash 
        $my_hash = spl_object_hash($user);
        $this->say("My hash = " . $my_hash);
        // $this->f_sql->insert_hash($my_hash);
        // close DB
        $this->f_sql->closeDB();
    }

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        $this->say("[DEMO] {$user->getId()} says '{$msg->getData()}'");
        // Debug
        $this->say("DemoSocketServer->onMessage()");
    }

    public function onDisconnect(IWebSocketConnection $user) {
        // open DB
        $this->f_sql->openDB();
        $this->say("[DEMO] {$user->getId()} disconnected");
        $this->f_sql->logout($user->getId());
        // close DB
        $this->f_sql->closeDB();
    }

    public function onAdminMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        $this->say("[DEMO] Admin Message received!");
        $frame = WebSocketFrame::create(WebSocketOpcode::PongFrame);
        $user->sendFrame($frame);
    }

    public function say($msg) {
        echo "$msg \r\n";
    }

    public function run() {
        $this->server->run();
    }

}

// Start server
$server = new DemoSocketServer();
$server->run();
