Ext.define("anvgl.view.Footer", {
    /** @lends view.Footer */
    
    /**
     * Renders the footer components of the application.
     * At this stage it is a static list comprised of partner logos on the map interface
     * @constructs
     * 
     * @example
     * &#60;ul>
     *  &#60;li>&#60;img id='img-gswa' src='img/logos/geographical-survey-of-western-australia.jpg' alt='Geographical Survey of Wstern Australia' />&#60;/li>
     *  &#60;li>&#60;img id='img-eic' src='img/logos/exploration-incentive-scheme.jpg' alt='Exploration Incentive Scheme' />&#60;/li>
     * &#60;/ul> 
     */  
    constructor : function() {
        this.callParent(arguments);
    },
    
    extend	: "Ext.panel.Panel",
    alias	: "view.Footer",
    height	: 90,
    region	: "south",
    
    html	: "<ul>"
                + 	"<li><img id='img-gswa' src='img/logos/geographical-survey-of-western-australia.jpg' alt='Geographical Survey of Western Australia' /></li>"
                + 	"<li><img id='img-eic' src='img/logos/exploration-incentive-scheme.jpg' alt='Exploration Incentive Scheme' /></li>"
            + "</ul>",
            
    id		: "anvgl-footer"
});