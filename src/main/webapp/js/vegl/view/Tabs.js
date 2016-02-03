/**
 * @class Tabs
 * @extends Ext.TabPanel
 * @classdesc view - tabs
 * 
 */
Ext.define('anvgl.view.Tabs', {
	alias			: "view.Tabs",
	extend			: "Ext.TabPanel",
	
	id 				: 'auscope-tabs-panel',
	enableTabScroll : true,
	height 			: '70%',
    region 			: 'center',
    split 			: true,
    width 			: '100%',
    margin			: '100 0 0 0',
    
    constructor 	: function(config) {
    	this.callParent(arguments);
    	
    	// always set the first tab active
    	this.activeTab = 0;
    }
});