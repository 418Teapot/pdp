<?php

    // check me out mofo!
     // get user script
    $brugernavn = $_GET['u'];
    //echo "Leder efter brugeren ".$brugernavn;    
if($brugernavn != ""){
    include("./database.php");
    
    $q = mysqli_query($db, "SELECT * FROM users WHERE username='".$brugernavn."'") or die("Kunne ikke hente bruge(re! Fejl: ".mysqli_error($db));
    
    $str = "";
    while($res = mysqli_fetch_array($q, MYSQLI_ASSOC)){       
        $str = $res['bookedWeeks'];                
    }
    
    $weekNumber = date("W"); 
    //echo $weekNumber;
    
    $return = array();    
    $weekStr = "";
    if($str != ""){
        foreach(explode(", ", $str) as &$v){
            //echo $weekNumber." = ".$v." <br>";
            if($weekNumber != $v){
                // vi har fundet en uge! slet den!
                $weekStr .= $v.", ";
            }
        }
    }    
        $updatedWeeks = substr($weekStr, 0, -2);    
        //$r = mysqli_query($db, "UPDATE users SET bookedWeeks='".$updatedWeeks."' WHERE username='".$brugernavn."'") or die("Kunne ikke opdatere uger");
    $r = 1;
    $superVal = false;
    if($r == "1" || $r == 1){
        $superVal = true;
    }
    array_push($return, $superVal);
    echo json_encode($return);
}

?>