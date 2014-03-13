#!/php -q
<?php
// Run from command prompt > php demo.php
require_once("websocket.server.php");
require_once("mysql_config.php");
require_once("f_sql.php");

//Establish a connection to MySQL 
$con = mysql_connect(dbhost,dbuname,dbpass);
mysql_select_db(dbname);

/**
 * Avacha Handler
 *
 * @author T.Tetsuro
 *
 */
class AvachaHandler extends WebSocketUriHandler {
   
    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
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
        $json_array["my_avatar"][0]["good_count"] = get_good_data($uuid);
        $json_array["my_avatar"][0]["her_name"] = get_my_name($uuid); //On Her Client, the her name is my name.
	$msg->setData(json_encode($json_array));
       
        $this->say($uuid);
        $this->say($action);
        $this->say($change_user);
        
        if(!empty($uuid)){          
          //Ini 
          insert_connection_uuid($user->getId(), $uuid);
          insert_user_name($uuid);
          //Login
          login($uuid); 
  
          if($change_user == "random"){ // Change User Button -------------------------------------
            //Reset my to_uuid
            reset_my_to_uuid($uuid);
            //SELECT UUID
            $check_uuid = select_to_uuid($uuid);
            //Reset her to_uuid
            reset_her_to_uuid($uuid); 

            if($check_uuid == 0){ 
              //INSERT to_uuid
              insert_to_uuid($uuid);        
            }
            // Get the Connection ID the user send to
            $to_connection = intval(get_connections($uuid));        
            // var_dump($to_connection);
    
            //Send JSON data to the user.
            foreach ($this->users as $user){
              if($user->getId() == $to_connection){
                $user->sendMessage($msg);
              }
            }
          } 

          if($text_msg != ""){ // Send Message ---------------------------------------------------- 
           
            // Get the Connection ID the user send to
            $to_connection = intval(get_connections($uuid));        
    
            //Send JSON data to the user.
            foreach ($this->users as $user){
              if($user->getId() == $to_connection){
                $user->sendMessage($msg);
              }
            }
          }

          if($action == "good"){
            //Counter of Good Action
            update_action($uuid);
            //Get the number of actions
            
            // Get the Connection ID the user send to
            $to_connection = intval(get_connections($uuid));        
    
            //Send JSON data to the user.
            foreach ($this->users as $user){
              if($user->getId() == $to_connection){
                $user->sendMessage($msg);
              }
            }
          }
        }
    }

 
    public function onAdminMessage(IWebSocketConnection $user, IWebSocketMessage $obj) {
        $this->say("[DEMO] Admin TEST received!");
        $frame = WebSocketFrame::create(WebSocketOpcode::PongFrame);
        $user->sendFrame($frame);
    }

}

/**
 * Avacha ChatLog Handler 
 *
 * @author T.Tetsuro
 *
 */
class ChatLogHandler extends WebSocketUriHandler {

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
	// Debug
	$this->say("ChatLogHandler->onMessage()");
 	// INSERT Connection ID and UUID
        $json_array = json_decode($msg->getData(), true);
        $text_msg = $json_array["my_avatar"][0]["message"];
        $uuid = $json_array["my_avatar"][0]["uuid"];
        $action = $json_array["my_avatar"][0]["action"];
        $change_user = $json_array["my_avatar"][0]["change_user"];
        $json_array["my_avatar"][0]["good_count"] = get_good_data($uuid);
	$msg->setData(json_encode($json_array));
        //TODO debug
        $this->say($uuid);
        $this->say($action);
        $this->say($change_user);
    }

}

/**
 * Avacha NameLog Handler 
 *
 * @author T.Tetsuro
 *
 */
class NameHandler extends WebSocketUriHandler {

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
	// Debug
	$this->say("NameHandler->onMessage()");
 	// INSERT Connection ID and UUID
        $json_array = json_decode($msg->getData(), true);
        $name = $json_array["my_avatar"][0]["name"];
        $return_name = $json_array["my_avatar"][0]["return_name"];
        $uuid = $json_array["my_avatar"][0]["uuid"];
        // Edit JSON Data
        $json_array["my_avatar"][0]["name"] = get_my_name($uuid);
	$msg->setData(json_encode($json_array));

        if($return_name == "return"){
          // Send My Name From Database
          $user->sendMessage($msg); 
        }else if($return_name == "none"){
          // UPDATE NAME
          update_my_name($name, $uuid);
        }

        //debug
        $this->say($name);
        $this->say($uuid);
        $this->say($return_name);
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

    public function __construct() {
        $this->server = new WebSocketServer("tcp://49.212.196.168:9000", 'superdupersecretkey');
        $this->server->addObserver($this);
        $this->server->addUriHandler("echo", new AvachaHandler());
        //TODO
        $this->server->addUriHandler("log", new ChatLogHandler());
        $this->server->addUriHandler("name", new NameHandler());

    }

    public function onConnect(IWebSocketConnection $user) {
        $this->say("DemoSocketServer->onConnect()");
        $this->say("[DEMO] {$user->getId()} connected");
	// INSERT Hash 
        $my_hash = spl_object_hash($user);
        $this->say("My hash = " . $my_hash);
        insert_hash($my_hash);
   }

    public function onMessage(IWebSocketConnection $user, IWebSocketMessage $msg) {
        $this->say("[DEMO] {$user->getId()} says '{$msg->getData()}'");
    	// Debug
    	$this->say("DemoSocketServer->onMessage()");
    }

    public function onDisconnect(IWebSocketConnection $user) {
        $this->say("[DEMO] {$user->getId()} disconnected");
        logout($user->getId());
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
