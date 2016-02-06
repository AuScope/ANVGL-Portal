/**
 * @class
 * @extends Ext.data.Store
 * @classdesc Featured Layer Store 
 */
Ext.define('anvgl.store.FeaturedLayers', {
     /** @lends FeaturedLayers */
    
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
    
    /**
     * load the 'featured layers' store and work out any configuration for sorting and grouping
     * @constructs 
     *  
     */
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