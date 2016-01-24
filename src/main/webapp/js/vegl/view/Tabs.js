/**
 * @class Tabs
 * @extends Ext.TabPanel
 * 
 */
Ext.define('anvgl.view.Tabs', {
	alias			: "view.Tabs",
	extend			: "Ext.TabPanel",
	
	id 				: 'auscope-tabs-panel',
	activeTab 		: 0,
	enableTabScroll : true,
	height 			: '70%',
    region 			: 'center',
    split 			: true,
    width 			: '100%',
    margin			: '100 0 0 0',
    
    constructor 	: function(config) {
    	this.callParent(arguments);
    }
});