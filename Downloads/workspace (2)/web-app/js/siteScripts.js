
function myTimestamp(){
	var tstmp = new Date();    
	return tstmp.getTime();
}


function toggleExpandItem(id) {
	$('#imgDown'+id).toggle();
	$('#imgUp'+id).toggle();
	$('#label'+id).toggleClass('collapsed');
	$('#dropdownDiv'+id).toggle(300);
}