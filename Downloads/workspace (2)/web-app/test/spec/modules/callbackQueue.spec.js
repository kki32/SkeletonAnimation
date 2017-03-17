define(["callbackQueue"], function(callbackQueue){
    describe("callbackQueue", function(){
    
        it("a private queue can be created", function(){
            
            var queue = callbackQueue.create({});

            expect(queue.enqueue).toBeDefined();
        });

        it("allows the timeout and startCallbacks to be set.", function(){
            var startedCallback = jasmine.createSpy();
            var finishedCallback = jasmine.createSpy();
            var delay = 1000;

            var queue = callbackQueue.create({
                delay: delay,
                startedCallback: startedCallback,
                finishedCallback: finishedCallback
            });

            expect(queue.delay).toEqual(delay);
            expect(queue.startedCallback).toEqual(startedCallback);
            expect(queue.finishedCallback).toEqual(finishedCallback);
        });

        it("handles no params", function() {
            var queue = callbackQueue.create();

            expect(queue.delay).toEqual(3000); //Hard coded default
            expect(queue.startedCallback).toBeNull();
            expect(queue.finishedCallback).toBeNull();
        });

        it("calls the started callback", function(){
            var startedCallback = jasmine.createSpy();

            var queue = callbackQueue.create({startedCallback: startedCallback});
            queue.enqueue();

            expect(startedCallback).toHaveBeenCalled();
        });

        it("runs the finish callback after required time", function(){
            var finishedCallback = jasmine.createSpy();
            var delay = 1000;
            
            var queue = callbackQueue.create({finishedCallback: finishedCallback, delay:delay});
            runs(function(){
                queue.enqueue();
            
                expect(finishedCallback).not.toHaveBeenCalled();
            })
            waits(delay*2);
            runs(function(){
                expect(finishedCallback).toHaveBeenCalled();
            });
        });

        it("handles multiple queued calls", function(){
            var startedCallback = jasmine.createSpy();
            var finishedCallback = jasmine.createSpy();
            var delay = 100;

            var queue = callbackQueue.create({startedCallback: startedCallback,finishedCallback: finishedCallback,  delay: delay});
            var enqueueLimit = 10;
            runs(function(){
                for(var i = 0; i < enqueueLimit; i++){
                    queue.enqueue();
                };
                expect(startedCallback.callCount).toEqual(1);
                expect(finishedCallback.callCount).toEqual(0);
            });
            var halfTotalTime = (delay * (enqueueLimit+2))/2; // slightly more so that we are definitely finished when we wait two halfTotalTimes
            waits(halfTotalTime);

            runs(function(){
                expect(startedCallback.callCount).not.toEqual(enqueueLimit);
                expect(finishedCallback.callCount).not.toEqual(enqueueLimit);
            });

            waits(halfTotalTime);
            runs(function(){
                expect(startedCallback.callCount).toEqual(enqueueLimit);
                expect(finishedCallback.callCount).toEqual(enqueueLimit);
            });
        });

        it("allows params to be passed to the callbacks", function(){
            var startedCallback = jasmine.createSpy();
            var finishedCallback = jasmine.createSpy();

            var delay = 100;
            var queue = callbackQueue.create({finishedCallback: finishedCallback, startedCallback: startedCallback, delay:delay});
            
            var params = {a: "param"};
            queue.enqueue(params);
            expect(startedCallback).toHaveBeenCalledWith(params);

            waits(delay*2);
            runs(function(){
                expect(finishedCallback).toHaveBeenCalledWith(params);
            });
        });

        it("allows params to be passed to the callbacks for each different enqueue", function(){
            var startedCallback = jasmine.createSpy();
            var finishedCallback = jasmine.createSpy();

            var delay = 100;
            var queue = callbackQueue.create({finishedCallback: finishedCallback, startedCallback: startedCallback, delay: delay});

            var params1 = {a: "params1"};
            queue.enqueue(params1);
            var params2 = {b: "params2"};
            queue.enqueue(params2);
            waits(delay*3); // make sure we have definitely finished

            runs(function(){
                expect(startedCallback).toHaveBeenCalledWith(params2);
                expect(finishedCallback).toHaveBeenCalledWith(params2);
            });
        });


        it("allows default timeout to be overridden", function(){
            var finishedCallback = jasmine.createSpy();

            var delay = 2000;
            var queue = callbackQueue.create({
                finishedCallback: finishedCallback
            });

            queue.enqueue({}, delay*0.5);

            waits(delay*0.6);
            runs(function(){
                expect(finishedCallback).toHaveBeenCalled();
            });
        });

        it("allows the queue to be skipped if the displayTime is negative", function(){
            var startedCallback = jasmine.createSpy();
            var finishedCallback =jasmine.createSpy();
            var delay = -1;
            var queue = callbackQueue.create({
                startedCallback: startedCallback, finishedCallback: finishedCallback, delay: delay});
            queue.enqueue({});
            expect(startedCallback).toHaveBeenCalled();
            expect(finishedCallback).toHaveBeenCalled();
            
            // Check the case where the delay is passed in rather then setting the default
            startedCallback = jasmine.createSpy();
            finishedCallback = jasmine.createSpy();
            queue = callbackQueue.create({startedCallback: startedCallback, finishedCallback: finishedCallback});
            queue.enqueue({}, delay);

            expect(startedCallback).toHaveBeenCalled();
            expect(finishedCallback).toHaveBeenCalled();
        });

        it("uses the correct order for a queue", function() {
            var finishedCallback = jasmine.createSpy();

            var delay = 10;

            var queue = callbackQueue.create({finishedCallback: finishedCallback, delay: delay});
            var params1 = {a: "a"};
            var params2 = {b: "b"};
            var params3 = {c: "c"};

            queue.enqueue(params1);
            queue.enqueue(params2, 100);
            queue.enqueue(params3, 200);
            waits(20); // First one done
            runs(function(){
                expect(finishedCallback).toHaveBeenCalledWith(params1);
                expect(finishedCallback.callCount).toEqual(1);
            });
            waits(200);
            runs(function() {
                expect(finishedCallback.callCount).toEqual(2);
                expect(finishedCallback).toHaveBeenCalledWith(params2);
            });

            waits(100);
            runs(function(){
                expect(finishedCallback.callCount).toEqual(3);
                expect(finishedCallback).toHaveBeenCalledWith(params3);
            });
        });

        it("allows callback cycle to be terminated early", function(){
            var finishedCallback = jasmine.createSpy();

            var delay = 1000;
            var queue = callbackQueue.create({finishedCallback: finishedCallback, delay:delay});
            var params = {a:1};
            queue.enqueue(params);
            queue.finishCurrent();

            // Make sure that it finishes straight away
            expect(finishedCallback).toHaveBeenCalledWith(params);

            // Check that it does not continue anyway
            waits(delay*2);
            runs(function(){
                expect(finishedCallback.callCount).toEqual(1);
            });
        });
    });
});
