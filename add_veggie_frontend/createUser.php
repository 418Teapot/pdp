<?
if($_POST != null){ // post er ikke submitted!
    echo "I was submitted!";
} else {
?>
<!doctype html>
<html lang="da">
    <head>
        <title>Opret Bruger</title>
        <meta charset="utf-8" />
    </head>
    <body>
        <form method="post" action="<?=echo PHP_SELF; ?>">
            <p>Brugernavn:
                <input type="text" id="navn" name="navn" required /></p>
            <p>Password:
                <input type="password" id="navn" name="navn" required /></p>
            <p>Gentag Password:
                <input type="password" id="navn" name="navn" required /></p>
            <p>Email:
                <input type="email" id="navn" name="navn" required /></p>
            <button type="submit">Opret bruger!</button>            
        </form>
    </body>
</html>
<? 
       }
?>