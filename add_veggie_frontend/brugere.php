<?php

?>
<!doctype html>
<html lang="da">
    <head>
        <title>AOFF Brugerstyring</title>
        <meta charset="utf-8" />
    </head>
    
    <body>
        
        <table>
            <thead>
                <th>Brugernavn</th>
                <th>Email</th>
                <th></th>
            </thead>
            <tbody id="tableBody">
                
            </tbody>
        </table>
        
        <div id="bookingDiv" style="display:none;">
            <h3>Book uge(r) til <span id="bookUser"></span></h3>
            <p>Bookede uger: <span id="weeksBookedSpan"></span></p>
            <p>Book en uge</p>
            <input type="number" min="1" max="52" id="weekToBook" />
            <button onclick="bookAWeek();">Book Uge</button>
        </div>
        
    <script src="http://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script>
        $( document ).ready(function() {
            loadUsers();
        });
        
        var selUser = "";
        var selWeeks = "";
        
        function bookWeeks(user, weeks){
            console.log("BOOKIN' til "+user);   
            
            if(weeks == null || weeks == "null"){
                weeks = "Ingen uger booket!";                
            }
            
            $('#weeksBookedSpan').html(weeks);
            $('#bookUser').html(user);
            selUser = user;
            selWeeks = weeks;
            console.log(selWeeks);
            $('#bookingDiv').show('fast');
                                                            
        }
        
        function bookAWeek(){
               if(selUser != ""){
                   var newWeek = $('#weekToBook').val();
                   var weeks2Book = "";
                   
                   if(selWeeks == ""){
                        weeks2Book = newWeek;
                   } else {
                        weeks2Book = selWeeks+", "+newWeek;   
                   }
                   
                   console.log("Booker "+weeks2Book+" til "+selWeeks);
                   $.post("./bookWeek.php", { w: weeks2Book, u: selUser }).done(function(data){
                        console.log(data);  
                   });
                    
                   selWeeks = weeks2Book;
                   console.log("REFRESH med "+weeks2Book);
                   $('#weeksBookedSpan').html(weeks2Book);
                    
                   
               }
        }
        
        function loadUsers(){
            $.get("http://178.62.139.101/pdp/getUser.php?u=a", function(data,               status){
               if(status == "success"){
                    data = JSON.parse(data);
                   console.log(data);
                   for(var x in data){
                        var htmlStr = "<tr>";
                       htmlStr += "<td>"+data[x].username+"</td>";
                       htmlStr += "<td>"+data[x].email+"</td>";
                       htmlStr += "<td><a href='#' onclick='bookWeeks(\""+data[x].username+"\", \""+data[x].bookedWeeks+"\")'>Book en uge</a></td>";
                       $('#tableBody').html($('#tableBody').html()+htmlStr);
                   }
               }
            });
        }
        
        
    </script>
    </body>
</html>