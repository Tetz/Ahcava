<?php

/**
 * Parse Push Notificaions Class
 * 
 * @author Tetsuro Takemoto
 */

Function Push($channel){

    //TODO Add $title and $alert as parameters
    $title = 'アカバ！';
    $alert = '誰かがあなたを呼んでいます。';

    $APPLICATION_ID = "fafeafeaefafea";
    $REST_API_KEY = "bafsaefaveafeaw";

    $url = 'https://api.parse.com/1/push';

    $data = array(
            'channel' => 'user_' . $channel,
            'type' => 'android',
            'expiry' => 1451606400,
            'data' => array(
                'title' => $title,
                'alert' => $alert,
                'action' => 'com.ver1.avacha.UPDATE_STATUS',
                ),
            );
    $_data = json_encode($data);
    $headers = array(
            'X-Parse-Application-Id: ' . $APPLICATION_ID,
            'X-Parse-REST-API-Key: ' . $REST_API_KEY,
            'Content-Type: application/json',
            'Content-Length: ' . strlen($_data),
            );

    $curl = curl_init($url);
    curl_setopt($curl, CURLOPT_POST, 1);
    curl_setopt($curl, CURLOPT_POSTFIELDS, $_data);
    curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
    curl_exec($curl);
}
?>

