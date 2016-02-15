Ext.define('anvgl.view.Tabs', {
    /** @lends view.Tabs */
    
	alias			: "view.Tabs",
	extend			: "Ext.TabPanel",
	
	id 				: 'auscope-tabs-panel',
	enableTabScroll : true,
	height 			: '70%',
    region 			: 'center',
    split 			: true,
    width 			: '100%',
    margin			: '100 0 0 0',
    
    /**
     * Extends 'Ext.TabPanel to create a 'view' on the map interface to holds all tabs.
     * The active tab is not passed in as a configuration and at this stage the first is always active by default
     * @constructs
     * @param {object} config
     */
    constructor 	: function(config) {
    	this.callParent(arguments);
    	
    	// always set the first tab active
    	this.activeTab = 0;
    }
});