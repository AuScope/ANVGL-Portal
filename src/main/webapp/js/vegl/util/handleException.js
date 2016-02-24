Ext.define("anvgl.util.handleException", {
    /** @lends util.handleException */
    
    /**
     * Creates the Exception Handling module.
     * At this stage has some basic exception handling and can be extended to be more useful 
     * @constructs
     */
    constructor : function() {},

    /**
     * Details on an exception on a specific function.
     * At this stage it logs out the function name and the exception details
     * @function
     * @param {function} f 
     * @param {object} e 
     */
    onFunction : function(f, e) {
        console.log("**************************************************************");
        console.log("An exception has been caught on function '" + f.callee.name + "()'.");
        console.log(e);
        console.log("**************************************************************");
    }
});