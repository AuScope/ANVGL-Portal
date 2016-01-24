/**
  * the footer 
  */
Ext.define("anvgl.view.Footer", {
	extend	: "Ext.panel.Panel",
	alias	: "view.Footer",
	
	height	: 90,
	region	: "south",
	html	: "<ul>"
			+ 	"<li><img id='img-gswa' src='img/logos/geographical-survey-of-western-australia.jpg' alt='Geographical Survey of Western Australia' /></li>"
			+ 	"<li><img id='img-eic' src='img/logos/exploration-incentive-scheme.jpg' alt='Exploration Incentive Scheme' /></li>"
			+ "</ul>",
	id		: "anvgl-footer"
});