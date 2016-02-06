/**
 * @class
 */
Ext.define('anvgl.view.FeaturedLayers', {
    /** @lends FeaturedLayers */
    
    extend : "portal.widgets.panel.KnownLayerPanel",
	alias : "view.FeaturedLayers",

	 /**
       * featured layers view
       * @constructs 
       * @param {object} config
       *  
       */
	constructor : function (config) {
    	this.title = config.title;
    	this.tooltip = config.tooltip;
    	
    	this.map = config.map;
    	this.store = config.store;
    	this.activelayerstore = config.activelayerstore;
    	this.layerFactory = config.layerFactory;

    	this.callParent(arguments);
    }
});