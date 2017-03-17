/**
 *
 * Module: Ajax.
 *
 * Purpose: Handle all communication with the server, handle any errors and simplify ajax calls.
 *
 */

define(["mediator", "jquery", "notificationHandler"], function(mediator, $){

    // Error messages
    var messages = {
        somethingWrong:  "Oops... Something has gone wrong, try refreshing the page.",
        internetDown: "Oops... Something has gone wrong. Check your internet connection."
    }

    /**
     * Handle any errors during the ajax process.
     */
    function ajaxError(jqXHR, textStatus, errorThrown) {
        var hasBeenHandled = false;

        // Check for html and processing errors.
        switch(jqXHR["status"]) {
            case 301: // permanently moved
                mediator.publish("notification-alert", {text: messages.somethingWrong});
                mediator.publish("notification-debug", {text: "Permenantly moved: Error 301. Message: "+ textStatus + "Details: " +jqXHR});
                hasBeenHandled = true;
                break;
            case 302: // temporarily moved
                mediator.publish("notification-alert", {text: messages.somethingWrong});
                hasBeenHandled = true;
                break;
            case 404: // page missing
            	mediator.publish("notification-debug", {text: "Failed with error 404"});
            	hasBeenHandled = true;
            	break;
            case 405: // invalid method type
            	mediator.publish("notification-debug", {text: "Failed with error 405. Check request type (POST or GET)."});
            	hasBeenHandled = true;
            	break;
            case 500:
            	mediator.publish("notification-alert", {text: "Oops something has gone wrong please try refreshing the page."});
            	mediator.publish("notification-alert", {text: "Failed with error 500. Check the server."});
            	hasBeenHandled = true;
            case 0:
            	mediator.publish("notification-alert", {text: "Oops it looks like ucanask is down. Please try refreshing your page."});
            	mediator.publish("notification-debug", {text: "It appears as though the server is down."});
            	hasBeenHandled = true;
            case 200: // Comms okay but something else happened.
                switch(textStatus){
                    case "parsererror":
                        hasBeenHandled = true;
                        mediator.publish("notification-debug", {text: "Parse error. Response text: " + jqXHR.responseText });

                }
                break;
        }
        
        // Check for connectivity errors ? (not super sure about this -- difficult to test).
        if(!hasBeenHandled){
            if(textStatus === "error"){
                mediator.publish("notification-alert", {text: messages.internetDown});
            }
        }
    }

    /**
     * Makes the actual calls to the ajax function.
     * @param params map of parameters of the form { url: ... ,onSuccess: ..., data: {<someprop>: ...}}
     */
    function jsonRequester(params){
        try{
             // Make the ajax call
            $.ajax({url: params.url,
                    datatype: 'json',
                    cache: false,
                    type: params.type || "GET",
                    data: params.data,
                    success: function(data){
                    	params.onSuccess && params.onSuccess(data);
                    },
                    complete: function() {
                    	params.onComplete && params.onComplete()
                    },
                    error: function(jqXHR, textStatus, errorThrown){
                           ajaxError(jqXHR, textStatus, errorThrown);
                           if(params.onError){
                            params.onError(jqXHR,textStatus, errorThrown);
                           }
                    }
            });
        }
        catch(ex) {
            mediator.publish("notification-debug", {text:"Incorrect params for ajax call. Got: "+ params + " With exception: "+ex});
        }
    
    }

    // Subscribe the module to the ajax-json channel so that other modules can indirectly use this one.
    mediator.subscribe("ajax-json", jsonRequester);

});
