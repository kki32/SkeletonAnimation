
define(["jquery", "mediator", "ajax"], function($, mediator){
	// Update the audience members questions for the presenter
	function manageAudienceQuestions(id) {

		var currrentPresentationVersion;
		
		//Callback function to replace the list of questions
		function updateQuestions(data){
			if (data.length !== 0) {
				$('#questionList').html(''); //Empty the list
				for(key in data) {
					if(data[key].anonymous || data[key].userid == null) {
						$('#questionList').append('<li>' +  data[key].text + ' - <i>Anonymous</i></li>');
					} else {
						$('#questionList').append('<li>' +  data[key].text + ' - <i>' + data[key].username + '</i></li>');				
					}
				}
			}
		}
		
		//Looping callback function to check for new questions
		function getQuestionsAjax(){
			mediator.publish('ajax-json', {
				url:'/presentation/get_audience_question_version/' + id,
				onSuccess: function(data) {
					if (currrentPresentationVersion != data){
						//Update the page
						mediator.publish('ajax-json', {url:'/presentation/get_audience_questions_json/' + id, onSuccess: updateQuestions});
						currrentPresentationVersion = data;
					}
				},
				onComplete: function() {
					setTimeout(getQuestionsAjax, 4000);
				}
			});
		}
		
		//Initial callback to set the version
		function initialiseAudienceQuestionAjax(data){
			currrentPresentationVersion = data;
			setTimeout(getQuestionsAjax,4000);
		}
		
		//Start of execution
		mediator.publish('ajax-json', {
			url:'/presentation/get_audience_question_version/' + id,
			onSuccess: initialiseAudienceQuestionAjax()
		});
		
	}
	
	return {
		manageAudienceQuestions: manageAudienceQuestions
	};
})