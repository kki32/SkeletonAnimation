/**
 * All the javascript required for the home index page
 */
define(["jquery","mediator", "notificationHandler"], function($, mediator){
	
	function login(){
		$('#loginForm').submit();
	}
	
	function join(){
		$('#go-to-presentation-form').submit();
	}
	
	$("#login-button").on("click", login);
	$("#Password").on("keypress", function(event){
		if(event.keyCode === 13){
			login();
		}
	});
	$("#join-button").on("click", join);
	$("#PresentationCode").on("keypress", function(event){
		if(event.keyCode === 13){
			join();
		}
	});
	
//    mediator.publish("notification-alert", {text: "Hello. I am Harold, and I am here to take you to the pizza.", displayTime:10000000});
//    mediator.publish("notification-warning", {text: "Warning: pizza may have been lost in transit (buuurp...)", displayTime:10000000});
//    mediator.publish("notification-debug", {text: "Error: pizza missing, do not alert the humans...."});
});
