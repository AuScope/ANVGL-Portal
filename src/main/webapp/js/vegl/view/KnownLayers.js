Ext.define('anvgl.view.KnownLayers', {
    /** @lends view.KnownLayers */
    
	alias	: 'view.KnownLayers',
	extend	: 'Ext.panel.Panel',
	
	id		: "layers-wrapper",
	layout	: 'border',
    region	: 'west',
    border	: false,
    split	: true,  
    width	: 370,
	
    /**
     * Creates a 'view' on the map interface that holds all 'known layers'
     * The 'west' region in ExtJS terminology
     * @constructs
     * @param {object} config
     */
	constructor : function(config) {
		this.callParent(arguments);
	}
});