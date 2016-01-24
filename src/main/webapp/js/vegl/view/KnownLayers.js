Ext.define('anvgl.view.KnownLayers', {
	alias	: 'view.KnownLayers',
	extend	: 'Ext.panel.Panel',
	requires : ["anvgl.view.Tabs"],
	
	id		: "layers-wrapper",
	layout	: 'border',
    region	: 'west',
    border	: false,
    split	: true,  
    width	: 370,
		
	constructor : function(config) {
		this.callParent(arguments);
	}
});