<?php
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
    if($str != ""){
        foreach(explode(", ", $str) as &$v){
            //echo $weekNumber." = ".$v." <br>";
            if($weekNumber == $v){
                //$return = "true";
                $val = true;
                array_push($return, $val);
            }
        }
    }    
    if(count($return) == 0){
        $val = false;
        array_push($return, $val);
    }
    
    echo json_encode($return);   
}
?>