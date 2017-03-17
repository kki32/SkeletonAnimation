

define(["mediator", "notificationHandler" ], function(mediator, notificationHandler){
    
    describe("notificationHandler", function(){
    
        beforeEach(function(){
            //setFixtures(sandbox({id: "content",style: "display: none;"}));

            this.addMatchers({
                toThrowException: function(){
                    try {
                        this.actual();
                    }
                    catch (e) {
                        return true;
                    }
                    return false;
                }
            });
        });

        it("displays alert notification in the dom when one is published to the mediator", function(){
            var channel = "notification-alert";
            var displayTime = 10;
            var message = "Hello i am message";
            mediator.publish(channel, {text: message, displayTime: displayTime});
            expect($("#notification")).toExist();
            expect($("#notification .notification-text").text()).toEqual(message);
            expect($("#notification")).toHaveClass("alert");
            waits(displayTime);
        });

        it("displays warning notifications", function(){
        
            var channel = "notification-warning";
            var displayTime = 10;
            var message = "hello i am a warning message";

            mediator.publish(channel, {text: message, displayTime: displayTime});

            expect($('#notification')).toExist();
            expect($('#notification .notification-text').text()).toEqual(message);
            expect($('#notification')).toHaveClass("warning");
            waits(displayTime);
        });

        it("displays debug notifications", function() {
            
            var channel = "notification-debug";
            var displayTime = 10;
            var message = "hello i am a debug message";

            mediator.publish(channel, {text: message, displayTime: displayTime});

            expect($('#notification')).toExist();
            expect($('#notification .notification-text').text()).toEqual(message);
            expect($('#notification')).toHaveClass("debug");

            waits(displayTime)
        });

        it("only displays notification for a defined period", function() {
            var channel = "notification-alert";

            var message = "hello i am a timed alert message";

            var displayTime = 1000;

            mediator.publish(channel, {text:message, displayTime: displayTime});
            expect($("#notification-wrapper")).toExist();
            waits(displayTime*2); // Make sure we are done

            runs(function() {
                expect($("#notification-wrapper")).not.toExist();
            });
        });

        it("handles unexpected parameters gracefully", function(){
            var channel = "notification-alert";
            window.console = {};
            console.log = jasmine.createSpy();
            expect(function(){
                mediator.publish(channel, null);
            }).not.toThrowException();
            expect(console.log).toHaveBeenCalled();
            expect($('#notification .notification-text').text().indexOf("error")).not.toEqual(-1);
            waits(3000*1.2);
            runs(function(){
                expect($("#notifcation-wrapper")).not.toExist();
                // Check that the module still works
                var message = "hello";
                var displayTime = 100;
                mediator.publish(channel, {text: message, displayTime: displayTime});
                expect($("#notification")).toExist();
            });
            waits(100*1.2);
           

            runs(function(){
                expect($("#notifcation-wrapper")).not.toExist();
                message = function(){};
                console.log = jasmine.createSpy();
                expect(function(){mediator.publish(channel, {text: message});}).not.toThrowException();

                expect(console.log).toHaveBeenCalled();
                expect($('#notification .notification-text').text().indexOf("error")).not.toEqual(-1);
            });
            waits(3000*1.2);
            runs(function(){
                // Check it again
                message = "checking";

                mediator.publish(channel, {text: message});
                expect($("#notification")).toExist();
                expect($("#notification .notification-text").text()).toEqual(message);
                waits(10000);
            });
        });

        it("allows notification to be dismissed", function(){
            var channel = "notification-alert";
            var message = "notification to dismiss";
            expect($("#notification")).not.toExist();
            mediator.publish(channel, {text: message});
            expect($("#notification .remove")).toExist();
            $("#notification .remove").click();
            expect($("#notification-wrapper")).not.toExist();
        });
    });
});
