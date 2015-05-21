<?
if($_POST != null){ // post er ikke submitted!
    header("CHARSET=UTF-8");
    if($_POST['password'] == $_POST['confPass']){
    include_once("./database.php");
        $password = saltPW($_POST['password']);
    mysqli_query($db, "INSERT INTO users (username, password, email) VALUES('".$_POST['navn']."', '".$password."','".$_POST['email']."')") or die("Kunne ikke oprette brugeren. FEJL: ".mysqli_error($db));        
        mysqli_close($db);
        die("Brugeren er oprettet!");
    } else {
        die("Passwords var ikke ens - gå tilbage og prøv igen!");   
    }
} else {
?>
<!doctype html>
<html lang="da">
    <head>
        <title>Opret Bruger</title>
        <meta charset="utf-8" />
    </head>
    <body>
        <h2>Opret bruger</h2>
        <form method="post">
            <p>Brugernavn:
                <input type="text" id="navn" name="navn" required /></p>
            <p>Password:
                <input type="password" id="password" name="password" required /></p>
            <p>Gentag Password:
                <input type="password" id="confPass" name="confPass" required /></p>
            <p>Email:
                <input type="email" id="email" name="email" required /></p>
            <button type="submit">Opret bruger!</button>            
        </form>
    </body>
</html>
<? 
       }
?>