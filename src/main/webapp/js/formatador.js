function maskCPF(CPF) {
	var evt = window.event;
	kcode = evt.keyCode;
	if (kcode == 8)
		return;

	switch (CPF.value.length) {
	case 3:
		CPF.value = CPF.value + '.';
		break;
	case 7:
		CPF.value = CPF.value + '.';
		break;
	case 11:
		CPF.value = CPF.value + '-';
		break;
	}
}

function maskData(obj, prox) {
	switch (obj.value.length) {
	case 2:
		obj.value = obj.value + "/";
		break;
	case 5:
		obj.value = obj.value + "/";
		break;
	case 10:
		prox.focus();
		break;
	}
}

function formataCPF(value) {
	with (value) {
		value = value.substr(0, 3) + '.' + value.substr(3, 3) + '.'
				+ value.substr(6, 3) + '-' + value.substr(9, 2);
	}
}

function fone(obj, prox) {
	switch (obj.value.length) {
	case 1:
		obj.value = "(" + obj.value;
		break;
	case 3:
		obj.value = obj.value + ")";
		break;
	case 8:
		obj.value = obj.value + "-";
		break;
	case 13:
		prox.focus();
		break;
	}
}

function formata_cep(obj, prox) {
	switch (obj.value.length) {
	case 2:
		obj.value = obj.value + ".";
		break;
	case 6:
		obj.value = obj.value + "-";
		break;
	case 10:
		prox.focus();
		break;
	}
}

// ******************************************************************************************
function replace(valor) {

	valor = valor.toString().replace("a", "");
	valor = valor.toString().replace("b", "");
	valor = valor.toString().replace("c", "");
	valor = valor.toString().replace("d", "");
	valor = valor.toString().replace("e", "");
	valor = valor.toString().replace("f", "");
	valor = valor.toString().replace("g", "");
	valor = valor.toString().replace("h", "");
	valor = valor.toString().replace("i", "");
	valor = valor.toString().replace("j", "");
	valor = valor.toString().replace("k", "");
	valor = valor.toString().replace("l", "");
	valor = valor.toString().replace("m", "");
	valor = valor.toString().replace("n", "");
	valor = valor.toString().replace("o", "");
	valor = valor.toString().replace("p", "");
	valor = valor.toString().replace("q", "");
	valor = valor.toString().replace("r", "");
	valor = valor.toString().replace("s", "");
	valor = valor.toString().replace("t", "");
	valor = valor.toString().replace("u", "");
	valor = valor.toString().replace("v", "");
	valor = valor.toString().replace("w", "");
	valor = valor.toString().replace("x", "");
	valor = valor.toString().replace("y", "");
	valor = valor.toString().replace("z", "");

	return valor;

}

function FormataValor(id, tammax, teclapres) { 
	if (window.event) { // Internet Explorer
		var tecla = teclapres.keyCode;
	} else if (teclapres.which) { // Nestcape / firefox
		var tecla = teclapres.which;
	}

	vr = document.getElementById( id ).value;
	vr = replace(vr);
	vr = vr.toString().replace("/", "");
	vr = vr.toString().replace("/", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(".", "");
	vr = vr.toString().replace(".", "");
	vr = vr.toString().replace(".", "");
	vr = vr.toString().replace(".", "");

	if ((tecla > 47 && tecla < 58)) { // numeros de 0 a 9

	} else {
		if (tecla != 8) { // backspace
			alert("Digite somente numeros!");
		} else {

		}
	}

	tam = vr.length;

	if (tam < tammax && tecla != 8) {
		tam = vr.length + 1;
	}

	if (tecla == 8) {
		tam = tam - 1;
	}

	if (tecla == 8 || tecla >= 48 && tecla <= 57 || tecla >= 96 && tecla <= 105) {
		if (tam <= 2) {
			document.getElementById(id).value = vr;
		}
		if ((tam > 2) && (tam <= 5)) {
			document.getElementById(id).value = vr.substr(0, tam - 2) + ','
					+ vr.substr(tam - 2, tam);
		}
		if ((tam >= 6) && (tam <= 8)) {
			document.getElementById(id).value = vr.substr(0, tam - 5) + '.'
					+ vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam);
		}
		if ((tam >= 9) && (tam <= 11)) {
			document.getElementById(id).value = vr.substr(0, tam - 8) + '.'
					+ vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3) + ','
					+ vr.substr(tam - 2, tam);
		}
		if ((tam >= 12) && (tam <= 14)) {
			document.getElementById(id).value = vr.substr(0, tam - 11) + '.'
					+ vr.substr(tam - 11, 3) + '.' + vr.substr(tam - 8, 3)
					+ '.' + vr.substr(tam - 5, 3) + ','
					+ vr.substr(tam - 2, tam);
		}
		if ((tam >= 15) && (tam <= 17)) {
			document.getElementById(id).value = vr.substr(0, tam - 14) + '.'
					+ vr.substr(tam - 14, 3) + '.' + vr.substr(tam - 11, 3)
					+ '.' + vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3)
					+ ',' + vr.substr(tam - 2, tam);
		}
	}
}