<?php

$APPLICATION_ID = "zHaC9bMjdv5IRwpnOn5Y9EAd5iGun5Muv8zMJDgQ";
$REST_API_KEY = "B9jcMzkTyQIA53sPXINnSk0WHNpM0pvxVW8tkzC1";

$url = 'https://api.parse.com/1/push';

$data = array(
        'channel' => 'Giants',
        'type' => 'android',
        'expiry' => 1451606400,
        'data' => array(
            'title' => 'The Push',
            'alert' => 'Test Push',
            'action' => 'com.parse.starter.UPDATE_STATUS',
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

?>
