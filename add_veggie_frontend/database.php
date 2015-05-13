<?php

//echo "Settings... <br>";

$dbnm = "it_pdp";
$usr = "root";
$pass = "root";
$host = "127.0.0.1";

//echo "Connecting... <br>";
$db = mysqli_connect(
    $host,
    $usr,
    $pass,
    $dbnm
);

if(mysqli_connect_errno()){
    echo "Fejl: " . mysqli_connect_error();
}

?>