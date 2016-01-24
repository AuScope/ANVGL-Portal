Ext.define("anvgl.view.Header", {
	extend	: "Ext.panel.Panel",
	alias	: "view.Header",
	
	id		: "anvgl-header",
	loader: {
        url: 'jsp/page_header.jsp',
       // autoLoad: true
    }
});