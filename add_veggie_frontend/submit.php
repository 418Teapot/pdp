<?
    include("database.php");

foreach($_POST['veggie'] as $index => $VEGGIE){
        mysqli_query($db, "INSERT INTO stock (veggie,amount,date) VALUES ('" . $VEGGIE . "','" . $_POST['amount'][$index] . "','" . $_POST['dag'] . "')") or die("fail to submit: " . mysqli_error($db));
}
    mysqli_close($db);
    header("Location: ./index.php");
    die();
?>