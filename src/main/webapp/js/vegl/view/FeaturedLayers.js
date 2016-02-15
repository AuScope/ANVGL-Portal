Ext.define('anvgl.view.FeaturedLayers', {
    /** @lends view.FeaturedLayers */
    
    extend : "portal.widgets.panel.KnownLayerPanel",
    alias : "view.FeaturedLayers",

     /**
       * Extends 'portal.widgets.panel.KnownLayerPanel' and provides for the title, layer stores and other configuration
       * @constructs
       * 
       * @example
       * var featuredLayers = Ext.create("view.FeaturedLayers", {
       *     title : "Featured",
       *     tooltip : {
       *          anchor : 'top',
       *          title : 'Featured Layers',
       *          text : '<p1>This is where the portal groups data services with a common theme under a layer. This allows you to interact with multiple data providers using a common interface.</p><br><p>The underlying data services are discovered from a remote registry. If no services can be found for a layer, it will be disabled.</p1>',
       *          showDelay : 100,
       *          icon : 'portal-core/img/information.png',
       *          dismissDelay : 30000
       *     },
       *     map : map,
       *     activelayerstore : layerStore,
       *     store : featuredLayerStore,
       *     layerFactory : layerFactory
       * });
       *  
       * @param {object} config
       */
    constructor : function (config) {
        this.title = config.title;
        this.tooltip = config.tooltip;
        
        this.map = config.map;
        this.store = config.store;
        this.activelayerstore = config.activelayerstore;
        this.layerFactory = config.layerFactory;
        
        // execute the parent class
        this.callParent(arguments);
    }
});