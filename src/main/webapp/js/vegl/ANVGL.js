Ext.application({
    /** @lends anvgl */
    
    // application name
    name : "anvgl",
    
    // ExtJS application folder
    // Used to hook the application name to a base folder for namespaces
    appFolder : "js/vegl",
    
    // models / stores
    stores : ["FeaturedLayers"],
    
    // views
    views : [
             "Header", 
             "FeaturedLayers", 
             "KnownLayers", 
             "Tabs", 
             "Map",
             "Footer"
     ],
    
    // dependencies register
    requires : ["anvgl.util.handleException"],
    
    /**
      * Application launch.
      * Here we build our GUI from existing components - this function should only be assembling the GUI
      * Any processing logic should be managed in dedicated classes - don't let this become a
      * monolithic 'do everything' function
      * 
      * @constructs 
      *  
      */
    launch : function() {
        // map
        var map = null;
        
        // default base layer name
        var defaultBaseLayerName = "Google Satellite";
        
        // instantiate exception handler
        var handleException  = new anvgl.util.handleException();
        
        // send these headers with every AJax request we make to ensure we use gzip for most of our requests (where available)*/
        Ext.Ajax.defaultHeaders = {
            'Accept-Encoding': 'gzip, deflate' 
        };

        // create our store for holding the set of layers that have been added to the map
        var layerStore = Ext.create('portal.layer.LayerStore', {});

        // we need something to handle the clicks on the map
        var queryTargetHandler = Ext.create('portal.layer.querier.QueryTargetHandler', {});
        
        // create our map implementations
        var mapCfg = {
            container : null,   /** We will be performing a delayed render of this map */
            layerStore : layerStore,
            allowDataSelection : true,
            listeners : {
                query : function(mapWrapper, queryTargets) {
                    queryTargetHandler.handleQueryTargets(mapWrapper, queryTargets);
                }
            }
        };
        
        var urlParams = Ext.Object.fromQueryString(window.location.search.substring(1));
        var isDebugMode = urlParams.debug;
        
        // it is always an OpenLayersMap at this stage
        // the support for GoogleMap was taken off, see commit 54519f72af3f3f1f2e9d2e3837c194c284803622
        map = Ext.create('portal.map.openlayers.OpenLayersMap', mapCfg);
        

        var layerFactory = Ext.create('portal.layer.LayerFactory', {
            map : map,
            formFactory : Ext.create('vegl.layer.filterer.VeglFormFactory', {map : map}),
            downloaderFactory : Ext.create('vegl.layer.VeglDownloaderFactory', {map: map}),
            querierFactory : Ext.create('vegl.layer.VeglQuerierFactory', {map: map}),
            rendererFactory : Ext.create('vegl.layer.VeglRendererFactory', {map: map})
        });
        
        // layers sorter, passed to the FeaturedLayers store as a config parameter
        var layersSorter = new Ext.util.Sorter({
            sorterFn: function(record1, record2) {
                var order1 = (record1.data.order.length ? record1.data.order : record1.data.name);
                var order2 = (record2.data.order.length ? record2.data.order : record2.data.name);
                return order1 > order2 ? 1 : (order1 < order2 ? -1 : 0);
            },
            direction: 'ASC'
        });
        
        // layers grouper, passed to the FeaturedLayers store as a config parameter
        var layersGrouper = new Ext.util.Grouper({
            groupFn: function(item) {
                return item.data.group;
            },
            sorterFn: function(record1, record2) {
                var order1 = (record1.data.order.length ? record1.data.order : record1.data.group);
                var order2 = (record2.data.order.length ? record2.data.order : record2.data.group);
                return order1 > order2 ? 1 : (order1 < order2 ? -1 : 0);
            },
            direction: 'ASC'
        });
        
        // featured layers store
        var featuredLayerStore = Ext.create("store.FeaturedLayers", {
            layersGrouper : layersGrouper,
            layersSorter : layersSorter
        });

        // 'layers' wrapper
        var featuredLayers = Ext.create("view.FeaturedLayers", {
            title : "Featured",
            tooltip : {
                 anchor : 'top',
                 title : 'Featured Layers',
                 text : '<p1>This is where the portal groups data services with a common theme under a layer. This allows you to interact with multiple data providers using a common interface.</p><br><p>The underlying data services are discovered from a remote registry. If no services can be found for a layer, it will be disabled.</p1>',
                 showDelay : 100,
                 icon : 'portal-core/img/information.png',
                 dismissDelay : 30000
            },
            map : map,
            activelayerstore : layerStore,
            store : featuredLayerStore,
            layerFactory : layerFactory
        });
        
        // tabs view
        var viewTabs = Ext.create("view.Tabs");
        viewTabs.add([featuredLayers]);

       // layers view
        var viewLayers = Ext.create("view.KnownLayers");
        viewLayers.add(viewTabs);
        
        // map view
        var viewMap = Ext.create("anvgl.view.Map", {
            map: map, 
            defaultBaseLayerName : defaultBaseLayerName,
            layerStore: layerStore
        });
        
        // footer view
        var viewFooter = Ext.create("view.Footer");
        viewLayers.add(viewFooter);
        
        // application view
        var app = Ext.create('Ext.container.Viewport', {
            layout:'border',
            items:[viewLayers, viewMap]
        });
        
        // the subset button needs a handler for when the user draws a subset bbox on the map:
        map.on('dataSelect', function(map, bbox, intersectedRecords) {
          /** show a dialog allow users to confirm the selected data sources */
          if (intersectedRecords.length > 0) {
              Ext.create('Ext.Window', {
                  width : 710,
                  maxHeight : 400,
                  title : 'Confirm which datasets you wish to select',
                  modal : true,
                  autoScroll : true,
                  items : [{
                      xtype : 'dataselectionpanel',
                      region : bbox,
                      itemId : 'dataselection-panel',
                      cswRecords : intersectedRecords
                  }],
                  buttons : [{
                      text : 'Capture Data',
                      iconCls : 'add',
                      align : 'right',
                      scope : this,
                      handler : function(btn) {
                          var parentWindow = btn.findParentByType('window');
                          var panel = parentWindow.getComponent('dataselection-panel');

                          panel.saveCurrentSelection(function(totalSelected, totalErrors) {
                              if (totalSelected === 0) {
                                  Ext.Msg.alert('No selection', 'You haven\'t selected any data to capture. Please select one or more rows by checking the box alongside each row.');
                              } else if (totalErrors === 0) {
                                  Ext.Msg.alert('Request Saved', 'Your ' + totalSelected + ' dataset(s) have been saved. You can either continue selecting more data or <a href="jobbuilder.html">create a job</a> to process your existing selections.');
                                  parentWindow.close();
                              } else {
                                  Ext.Msg.alert('Error saving data', 'There were one or more errors when saving some of the datasets you selected');
                                  parentWindow.close();
                              }
                          });
                      }
                  }]
              }).show();
          }

        });

        // create our permalink generation handler
        Ext.get('permanent-link').on('click', function() {
            var mss = Ext.create('portal.util.permalink.MapStateSerializer');

            mss.addMapState(map);
            mss.addLayers(layerStore);

            mss.serialize(function(state, version) {
                var popup = Ext.create('portal.widgets.window.PermanentLinkWindow', {
                    state : state,
                    version : version
                });

                popup.show();
            });
        });
        
        // handle de-serialisation -- ONLY if we have a uri param called "state"
        var deserializationHandler;
        var urlParams = Ext.Object.fromQueryString(window.location.search.substring(1));
        if (urlParams && (urlParams.state || urlParams.s)) {
            var decodedString = urlParams.state ? urlParams.state : urlParams.s;
            var decodedVersion = urlParams.v;

            deserializationHandler = Ext.create('portal.util.permalink.DeserializationHandler', {
                knownLayerStore : knownLayerStore,
                layerFactory : layerFactory,
                layerStore : layerStore,
                map : map,
                stateString : decodedString,
                stateVersion : decodedVersion
            });

        }
    }
});