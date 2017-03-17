define(["jquery", "mediator", "ajax"], function($, mediator, ajax){
    
    describe("Ajax", function(){

        function fail() {
            expect(false).toBeTruthy();
        }
        function jsToHtmlParameterList(data){
            var result = "";
            for(var key in data){
                result += key + "=" + data[key] + "&";
            }
            return result.substr(0, result.length-1);
        }

        beforeEach(function(){
            jasmine.Ajax.useMock();

            setFixtures(sandbox({id: "content", style: 'display: none;'}))
            this.addMatchers({
                toThrowException: function(){
                    try {
                        this.actual();
                    }
                    catch(e){
                        return true;
                    }
                    return false;
                }
            });
        });
        
        describe("json requests", function(){
            
            var ajaxJsonChannel = "ajax-json";

            it("handles successful requests", function(){
                var responseData = {somevar: 1};
                
                var response = {
                    'status': 200,
                    responseText: JSON.stringify(responseData)
                }
            
                var callback = jasmine.createSpy();
                
                var params = {
                    url: "/test/some/url",
                    onSuccess: callback
                }
                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                request.response(response);
                
                expect(callback).toHaveBeenCalledWith(responseData);
                expect($('#notification')).not.toExist();
            });

            it("handles parse errors for responses", function() {
                var response = {
                    'status': 200,
                    responseText: "{[invalid json here.:;"
                };

                var callback = jasmine.createSpy();

                var params = {
                    url: 'some/url',
                    onSuccess: callback
                }
                window.console = {};
                console.log = jasmine.createSpy();
                waits(6000);
                runs(function() {
                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                request.response(response);


                expect(callback).not.toHaveBeenCalled();
                expect(console.log).toHaveBeenCalled();
                expect($("#notification")).toExist();
                });
                waits(3000);
                
            });

            it("handles redirected requests", function(){
                
                // Permanent redirect
                var response = {
                    'status': 301
                }

                var callback = jasmine.createSpy();

                var params = {
                    url: "some/url",
                    onSuccess: callback
                }

                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                request.response(response);

                expect(callback).not.toHaveBeenCalled();
                expect($("#notification")).toExist();

                waits(3000*2);
                runs(function(){
                    // Temporary redirect

                    response['status'] = 302;
                    callback = jasmine.createSpy();

                    mediator.publish(ajaxJsonChannel, params);
                    request = mostRecentAjaxRequest();
                    request.response(response);

                    expect(callback).not.toHaveBeenCalled();
                    expect($("#notification")).toExist();
                });
                waits(3000);
            });

            it("handles lost internet connection", function(){
                var callback = jasmine.createSpy();
                var params = {
                    url: "some/url",
                    onSuccess: callback
                }
                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                jasmine.Clock.useMock();
                request.responseTimeout();

                expect(callback).not.toHaveBeenCalled();
                expect($("#notification")).toExist();

            });

            it("handles data for ajax request", function(){
                var callback = jasmine.createSpy();
                var params = {
                    url: "some/url",
                    onSuccess: callback,
                    data: {a: 1, b:2 }
                }
                var response = {'status': 200}

                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                request.response(response);
                var urlParams = request.url.substr(request.url.indexOf("?") + 1);
                expect(urlParams.indexOf(jsToHtmlParameterList(params.data))).not.toEqual(-1);
                expect(callback).toHaveBeenCalled();
            });

            it("gracefully handles incorrect parameters", function(){

                // No params
                window.console = {};
                console.log = jasmine.createSpy();

                var ajaxCall = function(){
                    mediator.publish(ajaxJsonChannel)
                }
                expect(ajaxCall).not.toThrowException();
                expect(console.log).toHaveBeenCalled();
            });

            it("allows on error callback to be registered", function(){
                var onSuccess = jasmine.createSpy();
                var onError = jasmine.createSpy();

                var params = {
                    url: "some/url",
                    onSuccess: onSuccess,
                    onError: onError
                };
                var response = {
                    'status': 200,
                    responseText: "{some invalid stuff in here[["
                };

                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                request.response(response);

                expect(onError).toHaveBeenCalled();

            });

            it("allows posts to be made", function(){
                var data = {
                    a: 1,
                    b: 2
                };
                var params = {
                    url: "some/url",
                    type: "POST",
                    data: data
                }
                
                mediator.publish(ajaxJsonChannel, params);
                var request = mostRecentAjaxRequest();
                expect(request.method).toEqual("POST");
                expect(request.params).toEqual(jsToHtmlParameterList(data));
            });
            
            it("handles 404 errors", function(){
            	var onSuccess = jasmine.createSpy();
            	var params = {
            			url: "some/url",
            			onSuccess: onSuccess
            	}
            	var response = {
            			'status': 404
            	}
            	console = {};
            	console.log = jasmine.createSpy();
            	mediator.publish(ajaxJsonChannel, params);
            	var request = mostRecentAjaxRequest();
            	request.response(response);
            	
            	expect(console.log).toHaveBeenCalled();
            })
        });
    });
});
