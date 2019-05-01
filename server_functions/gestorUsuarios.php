<?php
    // extract calls JSON parameters
    $parametros = json_decode( file_get_contents( 'php://input' ), true );

    if($parametros["register"]==true){
        // attempt register
        // get email and pass
        $email=$parametros['email'];
        $password=$parametros['password'];
        // try to register
        register ($email, $password);

   
    }else if($parametros["register"]==false){
         // close session
        $_SESSION["k_username"] = null; 
    } else {
        // wrong https call
        header('Content-Type: application/json');
        $datos = array(
                    'estado' => 'fail',
                    'code' => "bad request", 
                    );
        echo json_encode($datos);
    }

    // this function tryes to register specified e-mail and password, wether e-mail already exists tries to login
    function register($email, $password){

        // save hash funct output for pass
        $password=hash('sha512',$password);
        // stablish DB connection
        $conectar =mysqli_connect('localhost', "Xeandres011", "2g1Y9h2yDS", "Xeandres011_das");
        // check status
        if(!$conectar){
            // return error log
            echo "Error: No se pudo conectar a MySQL." . PHP_EOL;
            echo "errno de depuración: " . mysqli_connect_errno() . PHP_EOL;
            echo "error de depuración: " . mysqli_connect_error() . PHP_EOL;
        }else{
            // retrieve emails check
            $sentenciaMYSQL="SELECT * FROM usuario WHERE `email`='$email'";
            // query select
            $sentencia=  mysqli_query($conectar,$sentenciaMYSQL);
            // if already exist
            if(mysqli_num_rows($sentencia) > 0) { 
                // attempt login
                login($email, $password);
            }else { 
                // insert new user into DB
                $sentenciaMYSQL="INSERT INTO usuario (email, pass) VALUES ('$email', '$password')";
        
                // execute Query
                if($conectar -> query( $sentenciaMYSQL) === TRUE){
                    // user registered and loged in
                    $_SESSION["k_username"] = $email;
                    // return status
                    header('Content-Type: application/json');
                    $datos = array(
                        'estado' => 'ok',
                        'code' => 200, 
                        );
                    echo json_encode($datos);
                }else{
                    // log error
                    echo "Errormessage: %s\n", $conectar->error ;
                }
            }

            // close connection
            if(!mysqli_close($conectar)){
                // log close fail
                header('Content-Type: application/json');
                $datos = array(
                    'estado' => 'fail',
                    'code' => 4, 
                    );
                echo json_encode($datos);
            }     
        }
    
    }    

    // this function tries to login the user specified by email and password
    function login($email, $password){
        // stablish DB connection
        $conectar =mysqli_connect('localhost', "Xeandres011", "2g1Y9h2yDS", "Xeandres011_das");
        // check errors
        if(!$conectar){
            // log errors
            header('Content-Type: application/json');
            $datos = array(
                        'estado' => 'fail',
                        'code' => 4, 
                        );
            echo json_encode($datos);
        }else{
            // search for specified user
            $sentenciaMYSQL="SELECT * FROM usuario WHERE `email`='$email'";
            // checks any query error
            if($resultado = $conectar->query($sentenciaMYSQL)) { 
                    // retrieve user row
                    $row = $resultado->fetch_assoc();
                    // check hash password matches
                    if($row["pass"] == $password){
                        // if matches success login on server (new session)
                        $_SESSION["k_username"] = $row['email'];
                        // return request
                        header('Content-Type: application/json');
                        $datos = array(
                            'estado' => 'ok',
                            'code' => 200, 
                            );
                        echo json_encode($datos);
                    }else{
                        // return wrong password advice
                        header('Content-Type: application/json');
                        $datos = array(
                                'estado' => 'fail',
                                'code' => 0, 
                                );
                        echo json_encode($datos);
                    }
            }

            // close connection
            if(!mysqli_close($conectar)){
                // log server errors
                header('Content-Type: application/json');
                $datos = array(
                                'estado' => 'fail',
                                'code' => 4, 
                                );
                echo json_encode($datos);
            }
        }
    }
?>