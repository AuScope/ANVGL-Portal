Ext.define("anvgl.view.Map", {
    /** @lends view.Map */
    
    extend: "Ext.panel.Panel",
    alias: "view.Map",

    id: "center_region",
    html: "<div style='width:100%; height:100%' id='center_region-map'></div>",
    region: "center",
    margin: '100 0 0 0',

    /**
     * Reads in the configuration object to work out the map, the layer store and any default base layer
     * @constructs
     *
     * @example
     *  var viewMap = Ext.create("anvgl.view.Map", {
     *       map: map, 
     *       defaultBaseLayerName : defaultBaseLayerName,
     *       layerStore: layerStore
     *  });
     * 
     * @param {object} config
     */
    constructor: function(config) {
        this.map = config.map;
        this.defaultBaseLayerName = config.defaultBaseLayerName || "";
        this.map.layerStore = config.layerStore;
        
        this.callParent(arguments);
    },

    /**
     * Listens to 'afterrender' to hook the map to the div (#center_region-map) 
     * and invokes a function to set the default base layer map
     * @listens
     */
    listeners: {
        afterrender: function () {
            this.map.renderToContainer(Ext.get("center_region-map"),"center_region-map");
            
            if (this.defaultBaseLayerName.length > 0) {
                this.setDefaultLayer(this.map, this.defaultBaseLayerName);
            }
        }
    },

    /**
     * Reads the name of the default base layer, iterates and compares it to the map layers
     * and sets the default
     * @function
     * @param {object} map
     * @param {string} defaultBaseLayerName
     */
    setDefaultLayer : function(map, defaultBaseLayerName) {
        Ext.each(map.map.layers, function(layer) {
            if (layer.name === defaultBaseLayerName) {
                map.map.setBaseLayer(layer);
                return false;
            }
        });
    }
});