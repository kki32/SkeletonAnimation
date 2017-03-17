define(['jquery', 'mediator', 'notificationHandler', 'ajax'], function($, mediator){
	var mail = {presentationId: null};
	mail.setup = function() {
		
		function getPresenterPresentationAjax(){
			
			var params = {
				url: '/presentation/get_total_responses_json/' + mail.presentationId,
				onComplete: function() {
					setTimeout(getPresenterPresentationAjax,4000);
				},
				onSuccess: function(data){
					$('#feedbackCount').html(data.feedbackCount);
				}
			};

			mediator.publish("ajax-json", params);
		}
		
		function initialisePresenterPresentationAjax(){
			setTimeout(getPresenterPresentationAjax,4000);
		}
		
		getPresenterPresentationAjax();
		$("#mailButton").on('click', function(){
			if(mail.presentationId){
				window.location = "/presentation/show_am_questions/" + mail.presentationId;
			} else {
				mediator.publish('notification-alert', {text: "Oops. Something has gone wrong. Please try refreshing your browser."});
			}
		});
	}
	return mail;
});