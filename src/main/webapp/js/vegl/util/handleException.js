Ext.define("anvgl.util.handleException", function() {
    // constructor: do nothing
    var constructor = function() {};

    //  logs the exception to the console
    var onFunction = function(f, e) {
        console.log("**************************************************************");
        console.log("An exception has been caught on function '" + f.callee.name + "()'.");
        console.log(e);
        console.log("**************************************************************");
    };

    /** public interface to the class */
    return {
        constructor : constructor,
        onFunction : onFunction
    }
});