<?
    include("database.php");
?>

<!DOCTYPE html>
<html lang="da">
<head>
	<title>vøgt</title>
	<link rel="stylesheet" type="text/css" href="style.css">
    <script src="fields.js"></script>
    <meta charset="utf-8" />
</head>
<body>

    <h1>Veggies in stock</h1>
    <?php 
      $q = mysqli_query($db, "SELECT * FROM stock") or die("Fejl: ".mysqli_error($db));
        while($row = mysqli_fetch_array($q, MYSQL_ASSOC)){
            echo "Dag: " . $row['date'] . 
                " Type: " . $row['veggie'] . 
                " Gram i alt: " . $row['amount'] ."g. ".
                "<br>";   
        }
        mysqli_close($db);
    ?>
    
    <br>    
    <div id="readroot" style="display: none">
        Grøntsag:
        <!---<input type="text" name="veggie[]">--->
        <select id="veggie" name="veggie[]">
            <option value="Æbler">Æbler</option>
            <option value="Ærter">Ærter</option>
            <option value="Cheese">Cheese</option>
            <option value="Kaffe">Kaffe</option>
        </select>
        Gram i alt:
        <input type="text" name="amount[]">
        <br>
    </div>
      <button type="button" id="fields" >VEGGIES</button>
        <script>
            document.getElementById("fields").addEventListener("click", moreFields);
        </script>
    <form method="post" action="submit.php">
        Dato: <br>
        <input type="date" name="dag" min="2015-01-02"><br>
        
        <span id="writeroot"></span>
        
        <br><br>
  
        <input type="submit" name="submit" value="Indsend veggies" >
    </form>
    
    
    
</body>
</html>
