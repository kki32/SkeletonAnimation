/**
 * Module: Mediator.
 *
 * Purpose: Handles all communication between modules.
 * The idea behind this is to keep a clear separation between modules.
 *
 */

define(["jquery"], function($){
    'use strict';
    
 // Add ECMA262-5 Array methods if not supported natively
	//
	if (!('indexOf' in Array.prototype)) {
	    Array.prototype.indexOf= function(find, i /*opt*/) {
	        if (i===undefined) i= 0;
	        if (i<0) i+= this.length;
	        if (i<0) i= 0;
	        for (var n= this.length; i<n; i++)
	            if (i in this && this[i]===find)
	                return i;
	        return -1;
	    };
	}
    // -------PRIVATE VARS---------
    //
    // map of channel name to list of subscribers.
    var channels = {};

    // -------PUBLIC VARS----------


    // Object to return.
    var mediator = {
        // Takes a callback and adds it to the list for the channel that is being subscribed to.
        subscribe: function(channel, callback){

            // Create the channel if required.
            if (!channels[channel]) {
                channels[channel] = [];
            }
            
            // Add the callback unless it already is registered.
            if (channels[channel].indexOf(callback) == -1){
                channels[channel].push(callback);
            }
        },

        // Removes a callback from a channel.
        unsubscribe: function(channel, callback){

            // Can only unsubscribe if the channel exists
            if(channels[channel]){
                // Find the callback we are removing.
            	

                // If we found it remove it.
                if(index > -1) {
                    var callbacks = channels[channel];
                    // remove the callback at index
                    
                    callbacks.splice(index, 1);
                }
                
             }
        },

        // Notifies all the subscribers on the channel.
        publish: function(channel, params){
            // Check if the channel actually exists
            if(channels[channel]){

                // Run all the callbacks in the channel
                for(var i = 0, l = channels[channel].length; i < l; i++){
                    var callback = channels[channel][i];
                    callback(params);
                }
            }
        }
    };

    return mediator;

});
