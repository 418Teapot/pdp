<?php
    
    include("database.php");

    $returnArray = array();

    $q = mysqli_query($db, "SELECT * FROM stock") or die("Kunne ikke vælge veggies!");

while($res = mysqli_fetch_array($q, MYSQL_ASSOC)){
    array_push($returnArray, $res);       
}
mysqli_close($db);
echo json_encode($returnArray);

?>