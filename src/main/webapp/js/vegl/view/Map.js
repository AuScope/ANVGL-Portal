/**
 * @class Maps
 * @extends Ext.panel.Panel
 * 
 */
Ext.define("anvgl.view.Map", {
    extend: "Ext.panel.Panel",
    alias: "view.Map",

    id: "center_region",
    html: "<div style='width:100%; height:100%' id='center_region-map'></div>",
    region: "center",
    margin: '100 0 0 0',

    constructor: function(config) {
        this.map = config.map;
        this.defaultBaseLayerName = config.defaultBaseLayerName || "";
        this.map.layerStore = config.layerStore;
        this.callParent(arguments);
    },

    listeners: {
        afterrender: function () {
            this.map.renderToContainer(Ext.get("center_region-map"),"center_region-map");
            if (this.defaultBaseLayerName.length > 0) {
                this.setDefaultLayer(this.map, this.defaultBaseLayerName);
            }
        }
    },

    setDefaultLayer : function(map, defaultBaseLayerName) {
        Ext.each(map.map.layers, function(layer) {
            if (layer.name === defaultBaseLayerName) {
                map.map.setBaseLayer(layer);
                return false;
            }
        });
    }
});