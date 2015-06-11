<?php

    // check me out mofo!
     // get user script
    $veggie = $_GET['v'];
    $amt = $_GET['amt'];

    $reset = $_GET['r'];
    //echo "Leder efter brugeren ".$brugernavn;    
if($veggie != "" && $amt != ""){
    include("./database.php");
    
    $existingExtra = 0;
    
     //$veggie = str_replace("20%", " ", $veggie);
    
    $qr = mysqli_query($db, "SELECT * from `stock` WHERE `veggie`='".$veggie."'") or die("Ku ik' finde ekstra! MySQL: ".mysqli_error($db));
    while($res = mysqli_fetch_array($qr, MYSQL_ASSOC)){
        if($res['veggie'] == $veggie)
            $existingExtra = $res['hasExtra'];
    }
    
    if($reset == "y"){
        $newAmt = intval($amt);
    } else {
        $newAmt = intval($amt)+intval($existingExtra);
    }
    $q = mysqli_query($db, "UPDATE `stock` SET `hasExtra`='".$newAmt."' WHERE `veggie`='".$veggie."'") or die("Kunne ikke opdatere m ekstra Fejl: ".mysqli_error($db));
    
    $return = array();
    array_push($return, true);
    mysqli_close($db);
    echo json_encode($return);
    die();
}

?>