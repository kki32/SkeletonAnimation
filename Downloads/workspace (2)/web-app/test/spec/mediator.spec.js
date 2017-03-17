/**
 * Tests for the mediator core module. This is itself a module.
 */

define(['mediator'], function(mediator){
    
    describe("Mediator", function() {

        var channel = "a channel";

        beforeEach(function() {
            this.addMatchers({
                caughtException: function(){
                    var exceptionCaught = false;
                    try {                
                           this.actual();
                    }
                    catch(e) {
                        exceptionCaught = true;
                    }
                    return exceptionCaught;
                }
            });
        });
        it("has subscribe and publish ability", function() {
            expect(mediator.subscribe).toBeDefined();
            expect(mediator.publish).toBeDefined();
            

        });

        it("allows subscriptions to be made", function() {
            var callback = function(){
                return "called";
            };
            var callSubscribe = function() {
                mediator.subscribe(channel, callback);
            };
            expect(callSubscribe).not.caughtException();
        });
       
        it("allows channels to be unsubscribed from", function() {
            var callback = jasmine.createSpy();

            mediator.subscribe(channel, callback);
            mediator.unsubscribe(channel, callback);
            mediator.publish(channel, {});
            expect(callback).not.toHaveBeenCalled();
        });

        it("can publish notifications", function(){
            var callback = jasmine.createSpy();
            mediator.subscribe("a channel", callback);

            mediator.publish("a channel")
            
            expect(callback).toHaveBeenCalled();
            
            var anotherCallback = jasmine.createSpy();

            mediator.subscribe("different channel", anotherCallback);
            
            mediator.publish("different channel",[1,2]);
            expect(anotherCallback).toHaveBeenCalledWith([1,2]);
        });

        it("only allows a single subscription per subscriber for each channel", function(){
            var callback = jasmine.createSpy();

            mediator.subscribe("channel for one", callback);
            mediator.subscribe("channel for one", callback);

            mediator.publish("channel for one", null);
            expect(callback.callCount).toEqual(1);
        });
    });
});

