/**
 * @class FeaturedLayers
 * @extends Ext.data.Store
 * @classdesc Featured Layer Store: When instantiated does not load the store automatically (autoLoad: false) 
 */
Ext.define('anvgl.store.FeaturedLayers', {
	extend : 'Ext.data.Store',
	alias : "store.FeaturedLayers",
	
	model : 'portal.knownlayer.KnownLayer',
	groupField : 'group',
	
    proxy : {
        reader : {
            type : 'json',
            rootProperty : 'data'
        },
        type : 'ajax',
        url : 'getKnownLayers.do'
    },
    
    constructor : function(config) {
    	this.callParent(arguments);
    	
    	this.load();
    	
    	// group if specified
    	if (config && config.layersGrouper) {
    		this.group(config.layersGrouper);
    	}
    	
    	// sort if specified
    	if (config && config.layersSorter) {
    		this.sort(config.layersSorter);
    	}
    }
});