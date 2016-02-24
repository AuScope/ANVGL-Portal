Ext.define('anvgl.store.FeaturedLayers', {
     /** @lends store.FeaturedLayers */
    
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
     * Loads the 'featured layers' store and work out any configuration for sorting and grouping
     * @constructs 
     * @example
     *  var featuredLayerStore = Ext.create("store.FeaturedLayers", {
     *      layersGrouper : layersGrouper,
     *          layersSorter : layersSorter
     *  });
     * 
     * @param {object} config
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