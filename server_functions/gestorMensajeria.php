<?php
  // extract calls JSON parameters
  $parametros = json_decode( file_get_contents( 'php://input' ), true );

  // attempt register
  if($parametros["mensaje"]==true){
    // get app token  
    $token = $parametros["token"];
    // get app user 
    $user = $parametros["user"];
    // check if loged
    if($user ==  $_SESSION["k_username"]){
        // if loged add favourite
        sendFCM($token, "Firebase Push Notification","New Favourite");
    }else{
        // if not loged need register
        sendFCM($token, "Firebase Push Notification","Need Register");
    }
   
  }else{
       // wrong call
       header('Content-Type: application/json');
       $datos = array(
                   'estado' => 'fail',
                   'code' => 3, 
                   );
       echo json_encode($datos);
       exit();
  }

  // this function sends a message to specified id trough google firebase message system 
    function sendFCM($id, $body, $title) {
        // set url
        $url = 'https://fcm.googleapis.com/fcm/send';
        // prepare message content
        $fields = array (
                'to' => $id,
                'notification' => array (
                    "body" => "Firebase Push Notification",
                    "title" => "New Favourite"
                )
        );
        // encode message content to JSON format
        $fields = json_encode ( $fields );
        // stablish request heeaders
        $headers = array (
                'Authorization: key=' . "AAAA9VFwh7I:APA91bEBnY_IhKifMh5gxiRdh7RBr0Rf1rbUgREp0LhYJhdDC_ucDuE3ObyLDzkyC2uqesKdXQMIlbFHJwkedGpRnmmikhlliiMZHmFnfHUloJ_w1aio13XgEM6Lwflbo71h7FSiTgQo",
                'Content-Type: application/json'
        );
        // init CURL
        $ch = curl_init ();
        // set request options
        curl_setopt ( $ch, CURLOPT_URL, $url );
        curl_setopt ( $ch, CURLOPT_POST, true );
        curl_setopt ( $ch, CURLOPT_HTTPHEADER, $headers );
        curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
        curl_setopt ( $ch, CURLOPT_POSTFIELDS, $fields );
        // execute request
        $result = curl_exec ( $ch );
        // close CURL
        curl_close ( $ch );
        // return request status
        header('Content-Type: application/json');
        $datos = array(
                    'estado' => 'fail',
                    'code' => 200,
                    'resultado' => $result,
                    'mensaje' => $fields 
                    );
        echo json_encode($datos);
    }

?>
