/**
 * @class Header
 * @extends Ext.panel.Panel
 * @classdesc 'to-be' application header, not currently referenced. The header 'as-is' is a JSP include on <body>. 
 */
Ext.define("anvgl.view.Header", {
    extend	: "Ext.panel.Panel",
    alias	: "view.Header",

    id		: "anvgl-header",
    loader: {
        url: 'jsp/page_header.jsp',
    }
});