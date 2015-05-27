<?php
    // get user script
    $brugernavn = $_GET['u'];
    //echo "Leder efter brugeren ".$brugernavn;
if($brugernavn != ""){
    include("./database.php");
    
    $q = mysqli_query($db, "SELECT * FROM users") or die("Kunne ikke hente bruge(re! Fejl: ".mysqli_error($db));
    
    $usrArr = array();
    
    while($res = mysqli_fetch_array($q, MYSQLI_ASSOC)){
        //echo $res['username']." = ".$brugernavn;
        if($res['username'] == $brugernavn){
            //echo("VI HAR EN!");
            // vi har fundet en bruger der matcher! pak alt i en array og send den med json!        
            array_push($usrArr, $res['bookedWeeks']);
        }
    }
    echo json_encode($usrArr);   
}
?>