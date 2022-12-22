var buttonLoad;
var dropDownCountry;

$(document).ready(function() {

	buttonLoad = $("#buttonLoadCountries");
	dropDownCountry = $("#dropDownCountry");

	buttonLoad.click(function() {

		loadCountries();

	});

});

function loadCountries() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropDownCountry.empty();

		$.each(responseJSON, function(index, country) {
			optionValue = country.id + "-" + country.code;
			console.log(optionValue);
		});
	});
};
