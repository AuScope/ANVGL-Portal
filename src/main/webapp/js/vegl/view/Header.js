Ext.define("anvgl.view.Header", {
 /** @lends view.Header */
    
    /**
     * Renders the header component of the application.
     * Presently the class is not used, and the header is generated using a JSP include on the templates.
     * At some stage it could make an AJAX call and render the header component
     * @constructs
     */  
    constructor : function() {
        this.callParent(arguments);
    },
    
    extend	: "Ext.panel.Panel",
    alias	: "view.Header",
    id		: "anvgl-header",
});