var images = new Array(
	
	);

function slide(nr){
	var i = nr;			
	document.getElementById('slider').style.marginTop = "-" + i + "px" ;
	nr = nr+100;
	if(nr >= 500) nr = 0;
	setTimeout("slide(" + nr + ")",1400);
}
	setTimeout("slide(0)",1000);

