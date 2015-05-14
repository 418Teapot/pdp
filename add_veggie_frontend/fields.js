
function moreFields() {
	var newFields = document.getElementById('readroot').cloneNode(true);
	newFields.id = '';
	newFields.style.display = 'block';
	var newField = newFields.childNodes;
	var insertHere = document.getElementById('writeroot');
	insertHere.parentNode.insertBefore(newFields,insertHere);
}

window.onload = moreFields;