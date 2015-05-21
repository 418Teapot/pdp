<?php

    // book en uge
    include('database.php');

    $user = $_POST['u'];
    $week = $_POST['w'];

    mysqli_query($db, "UPDATE users SET bookedWeeks = '".$week."' WHERE username = '".$user."'") or die("FEJL: ".mysqli_error($db));

    echo "WEEK UPDATED!";

?>