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
	autoLoad : false,
	
    proxy : {
        reader : {
            type : 'json',
            rootProperty : 'data'
        },
        type : 'ajax',
        url : 'getKnownLayers.do'
    },
    
    sorters: new Ext.util.Sorter({
		        sorterFn: function(record1, record2) {
		            var order1 = record1.data.order;
		                order2 = record2.data.order;
		            return order1 > order2 ? 1 : (order1 < order2 ? -1 : 0);
		        },
		        direction: 'ASC'
		    }),
		    
    grouper: new Ext.util.Grouper({
		        groupFn: function(item) {
		            return item.data.group;
		        },
		        sorterFn: function(record1, record2) {
		            var order1 = record1.data.order;
		                order2 = record2.data.order;
		            return order1 > order2 ? 1 : (order1 < order2 ? -1 : 0);
		        },
		        direction: 'ASC'
		    })
});