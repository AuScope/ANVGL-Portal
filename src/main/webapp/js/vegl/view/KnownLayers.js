/**
 * @class KnownLayers
 * @extends Ext.panel.Panel
 * @classdesc known-layers view ('west' panel in EXTJS terms) 
 */
Ext.define('anvgl.view.KnownLayers', {
	alias	: 'view.KnownLayers',
	extend	: 'Ext.panel.Panel',
	
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