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
    
    constructor 	: function(config) {
    	this.callParent(arguments);
    }
});