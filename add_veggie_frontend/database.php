<?php


$dbnm = "it_pdp";
$usr = "root";
$pass = "m1tMySQL";
$host = "localhost";


$db = mysqli_connect(
    $host,
    $usr,
    $pass,
    $dbnm
);

if(mysqli_connect_errno()){
    echo "Fejl: " . mysqli_connect_error();
}

// password salting function
function saltPW($pw){
        $salt = md5("abcdef");
        return md5($salt.$pw.$salt);     
}

?>