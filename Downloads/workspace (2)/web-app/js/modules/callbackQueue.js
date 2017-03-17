/**
 * Module: CallbackQueue
 *
 * Purpose: Allow 'calls' to callbacks to be queued with a variable delay between each.
 * There are two callbacks, one for the start of the delay and another for the end.
 *
 *  @Author: Max Brosnahan
 */

define(function(){
    'use strict';

    /**
     * We may need multiple queues on the same page, so better provide a constructor to create unique queues.
     */
    var constuctor = {
        /**
         * Use a method instead of relying on the new operator. 'new' is a bit dangerous to use.
         */
        create: function(params){

            // ---------PRIVATE VARS---------

            // The queue where the params will be stored
            var queue = [];
            
            // Default delay in case one is not specified in the params.
            var defaultDelayTime = 3000; // three seconds
            
            // Initial set the queue status to inactive.
            var dequeuing = false;
            
            // Current timeout id. Should be null if not in use.
            var timeoutId = null;

            // Current item. Last item that was taken off the queue;
            var currentItem = null;
            // --------PUBLIC VARS-----------
            /**
             * Public facing vars. Initialise to a sensible default.
             *
             */
            var callbackQueue = {
                startedCallback: params && params.startedCallback || null,
                finishedCallback: params && params.finishedCallback || null,
                delay: params && params.delay || defaultDelayTime
            };

            // --------PRIVATE FUNCTIONS------
            
            /**
             * Start the dequeuing process.
             */
            var dequeue = function(){
                // Only dequeue if we are not already in the process or if there is nothing to dequeue.
                if(!dequeuing && queue.length > 0){

                    // We are dequeuing now.
                    dequeuing = true;
                    
                    // Get the item we are dequeuing.
                    currentItem = queue.shift();

                    // Call the started callback if it is defined.
                    callbackQueue.startedCallback && callbackQueue.startedCallback(currentItem.params);
                    // Set the timeout to call the finished callback after the specified delay.
                    timeoutId = setTimeout(function(){
                        onTimeout();
                    },currentItem.timeout || callbackQueue.delay);
                }
            };

            /**
             * Called when the end of the delay is reached or if the delay is skipped prematurely 
             */
            var onTimeout = function(){
                callbackQueue.finishedCallback && callbackQueue.finishedCallback(currentItem.params);
                
                // Finished dequeuing
                dequeuing = false;
                timeoutId = null;
                // Might need to carry on dequeuing.
                dequeue();

            }

            // -------PUBLIC FUNCTIONS--------
            /**
             * Add the obj to the queue with the timeout.
             */
            callbackQueue.enqueue = function(params, timeout){
                // If the delay is less then 0 skip the queue.
                if(timeout && timeout < 0|| timeout === undefined && this.delay < 0) {
                     // Skip the queue
                    this.startedCallback && this.startedCallback(params);
                    this.finishedCallback && this.finishedCallback(params);
                }
                else {
                    // Use the queue
                    queue.push({params: params, timeout: timeout });
                    dequeue();
                }
            };

            callbackQueue.finishCurrent = function() {
                clearTimeout(timeoutId);
                // Pretend that the timeout finished normally, we are just calling terminating function manually.
                onTimeout();
            };

            // Return the queue we just created.
            return callbackQueue;
        }
    }

    return constuctor;
});

