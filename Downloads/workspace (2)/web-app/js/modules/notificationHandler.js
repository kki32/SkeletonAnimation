/**
 * Module: NotificationHandler
 *
 * Purpose: Display notifications published to the mediator to the user.
 *
 * @Author: Max Brosnahan
 *
 */

define(["mediator", "callbackQueue"], function(mediator, callbackQueue){
    // -----------------PRIVATE -------------------

    /*
     * Set the state of the notifications.
     * For release builds debug should be disabled.
     */
    var levels = {
        debug: {enabled: false},
        warning: {enabled: true},
        "alert": {enabled: true}
    };
    
    // Define placeholder for remove event handler to prevent circular reference
    var removeNotificationHandler = null;

    /**
     * Show the notification in the DOM.
     * @param params contains the type of the notification(alert, warning, debug) and the message.
     */
    function insertIntoDom(params) {
        // Define the html for the notification
        var html ="<div id='notification-wrapper'><div id=\"notification\" class='cf " + params.level + "'><div class=\"notification-text\" title='" + params.message.text +"'>" + params.message.text + "</div><div class=\"point remove\"></div></div></div>";

        // Add the notification to the DOM.
        $("body").prepend(html);
        
        // Register the remove click handler
        $("#notification .remove").one("click", removeNotificationHandler);


    };

    /**
     * Remove the current notification from the DOM.
     */
    function removeFromDom(){
        $("#notification-wrapper").remove();
    }
    
    // Now that we have the start and finish callbacks we can setup the queue.
    // Function to take care of queuing notifications.
    var queue = callbackQueue.create({startedCallback: insertIntoDom, finishedCallback: removeFromDom });

    /**
     * Check that the inputs are what we expect. This prevents anything unexpected from happening.
     *
     */
    function checkNotification(message, level) {

        // Check that the message is of the expected form.
        var error = null;

        if( message === null){
            error = "null";
        }
        else if( message === undefined){
            error = "undefined";
        }
        else if( !message.text || typeof message.text !== 'string'){
            error = message;
        }

        // If there is no error then all is good.
        if(error === null){
            queue.enqueue({message: message, level: level}, message.displayTime);
            if(level === "debug" && console && console.log){
                console.log(message.text);
            }
        }
        else {

            // We have got an error, try and display it.
            if(levels[level] && levels["debug"].enabled) {
                var text = null;
                switch(message){
                    case null:
                        text = "Notification error. Check parameters. Level: " + level + "  Message: null";
                        break;
                    case undefined:
                        text = "Notification error. Check parameters. Level: " + level + " Message: undefined";
                        break;
                    default:
                       text = "Notification error. Check parameters. Level: " + level + " Message: " + message.toString();
                }
                message = {text: text};

                if(console && console.log){
                    console.log(message.text);
                }
                queue.enqueue({message: message, level: 'debug'});
            }
        }
    }
    /**
     * Register the notification channel if it is set enabled in the levels config.
     * Note that the channel registered takes the form 'notification-<level name>'.
     * @param level the level to be registered.
     */
    function registerIfEnabled(level) {
        // Check that the level is enabled
        if(levels[level] && levels[level].enabled){
            // Subscribe to the channel based on the level name. The messages that get published are put into a queue that will get handled when appropriate.
            mediator.subscribe("notification-" + level, function(message){
                checkNotification(message, level);
            });
     
        }
    }

    /**
     * Remove click Handler.
     * Dimisses the current notification from the screen.
     */
    removeNotificationHandler = function() {

        // Tell the queue to finish the current callback cycle early.
        queue.finishCurrent();
    };

    // !-----------------PRIVATE -------------------

    // Setup the levels
    for(var level in levels) {
        registerIfEnabled(level);
    }

       // No need to return anything as there is no need to directly use the notification module. All the interactions occur through the mediator.
});
