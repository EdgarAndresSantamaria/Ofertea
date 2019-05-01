<?php
    // get JSON object
    $parametros = json_decode( file_get_contents( 'php://input' ), true );

    // check wether call is meant to upload a new photo 
    if($parametros['addImage'] == true){
        // get image data
        $base=$parametros['imagen'];
        // get image id
        $id=$parametros['url'];
        // save the image in the server
        addImage($base, $id);
    }else{
        // wrong call response
        header('Content-Type: application/json');
        $datos = array(
                    'estado' => 'fail',
                    'code' => 3
                    );
        echo json_encode($datos);
        exit();
    }

    function addImage($base, $id){
        // decode image
        $binary=base64_decode($base);
        header('Content‐Type: bitmap; charset=utf‐8');
        //save  "id.jpg" file 
        $file = fopen("'$id'.jpg", 'w+');
        // write image
        fwrite($file, $binary); 
        // close file
        fclose($file);
        // add photo id to DB
        $conectar =mysqli_connect('localhost', "Xeandres011", "2g1Y9h2yDS", "Xeandres011_das");
        // check status
        if(!$conectar){
            // return error log
            echo "Error: No se pudo conectar a MySQL." . PHP_EOL;
            echo "errno de depuración: " . mysqli_connect_errno() . PHP_EOL;
            echo "error de depuración: " . mysqli_connect_error() . PHP_EOL;
        }else{
            // insert new path into DB
            $sentenciaMYSQL="INSERT INTO imagenes (path) VALUES ('$id')";
            // execute Query
            if($conectar -> query( $sentenciaMYSQL) === TRUE){
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
    }
?>
