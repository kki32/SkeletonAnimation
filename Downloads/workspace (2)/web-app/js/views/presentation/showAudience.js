define(['jquery', "mediator",'notificationHandler', "ajax"], function($, mediator){

	//On load request the current state of the presentation
	//and place it into storage.

	function operationSleekPanther(id) {

		var currentQuestionIds;
		var currentAllowQuestions;
		
		function removeQuestion(id) {
			$('#messageDiv'+id).css('color', 'grey');
			$('#messageDiv'+id).html("This question has been disabled");
			$('#messageDiv'+id).show();
			$('#form'+id).hide(1000, function(){ $('#form'+id).remove(); })
			setTimeout(function(){
					$('#questionDiv'+id).hide(600, function(){ $('#questionDiv'+id).remove();
						if ($('#questionList :visible').children().length===0) {
							$('#defaultMessage').show();
						}
					})
				}, 2000);
			
		}
		/** Checks the latest question ids against the currently displayed ones.
		 *  Updates any newly asked questions. */
		function getAudiencePresentationAjax(){
			var params = {
					url: '/presentation/update_audience_view/' + id,
					onComplete: function() {
						setTimeout(getAudiencePresentationAjax,4000);
					},
					onSuccess: function(data){
						
						if (data.allowUserQuestions) {
							$('#askPQuestion').show('fast');
						}
						else {
							$('#askPQuestion').hide('fast');
							$('#askPresenterQuestion').hide('fast');
							if (currentAllowQuestions) {
								require(['mediator', 'notificationHandler'], function(mediator) {
									mediator.publish("notification-alert", {
										text: "The lecturer has stopped student questions at this time."					
									});
								});
							}
						}
						currentAllowQuestions = data.allowUserQuestions;
						
						var a = [];
						qIds = data.ids
						for (var i in qIds) {
							a[qIds[i]] = 1;
						}
						for (var i in currentQuestionIds) {
							if (!a[currentQuestionIds[i]]) {
								removeQuestion(currentQuestionIds[i]);
							}
							else {
								a[currentQuestionIds[i]] = 2;
							}
						}
						for (var i in a) {
							if (a[i]==1) {
								getQuestionAjax(i, id);
							}
						}
						
						currentQuestionIds = qIds;
						
									
					}
			};
			
			mediator.publish("ajax-json", params);
		}
		/** Fetches the question view and adds it to the list */
		function getQuestionAjax(quesid, id) {
			var params = {
				url:'/presentation/get_question_by_id/'+id,
				data: {quesid: quesid},
				onSuccess: function(data) {
					$('#questionList').append(data);
					$('#defaultMessage').hide();
					var disabled = $('#questionDiv'+quesid).hasClass('disabledQuestion');
					if (disabled) {
						refreshDisabledness(quesid);
					}
				}
			};
			mediator.publish("ajax-json", params);
		}
		
		function initialiseAudiencePresentationAjax(data){
			currentQuestionIds = data.ids;
			currentAllowQuestions = data.allowUserQuestions;
			if (data.allowUserQuestions) {
				$('#askPQuestion').show('fast');
			}
			else {
				$('#askPQuestion').hide('fast');
				$('#askPresenterQuestion').hide('fast');
			}
			setTimeout(getAudiencePresentationAjax,4000);
		}
		mediator.publish("ajax-json", {
			url:'/presentation/update_audience_view/'+id,
			onSuccess: initialiseAudiencePresentationAjax
		});
		
	}

	
	function setup(id) {
		operationSleekPanther(id);
		var showingAnswered = false;
		
		/**
		 * Checks if a selection has been made
		 */
		function validateSelection(id) {
			if (undefined === $("input[name='choice"+id+"']:checked").val()) {
				$('#messageDiv'+id).css('color', "red");
				$('#messageDiv'+id).html("* Please select an answer!");
				$('#messageDiv'+id).show();
				return false;
			}
			submitAnswer(id);
			return false;
		}
		$('#questionList').on('click', '.sendSelection', function(event){
			var sendButton = event.currentTarget;
			var id = sendButton.id.substr(3);
			return validateSelection(id);
			
		});
		onclick="validateSelection(${id})"
		/**
		 * Checks for too many/little characters
		 */
		function validateFreeText(id) {
			var textInput = $('#text'+id).val()
			if (textInput.length>1000) {
				$('#messageDiv'+id).css('color', "red");
				$('#messageDiv'+id).html("* Too many characters! (limit 1000)");
				$('#messageDiv'+id).show();
				return false;
			}
			if (textInput.length==0) {
				$('#messageDiv'+id).css('color', "red");
				$('#messageDiv'+id).html("Please enter an answer first!");
				$('#messageDiv'+id).show();
				return false;
			}
			submitAnswer(id);
			return false;
		}
		$('#questionList').on('click','.ftSendSelection', function(event){
			var sendButton = event.currentTarget;
			var id = sendButton.id.substr(3);
			return validateFreeText(id);
		});
		
		/**
		 * Submits an answer using json and displays the message received from server.
		 * Then removes the question from the list if successful.
		 */
		function submitAnswer(id) {
			var params = {
				url:'/presentation/respond/' + id,
				type: "POST",
				data: $('#form'+id).serialize(),
				onSuccess: function(data) {
					mediator.publish(data.notification, {
						text: data.message					
					});
					if (data.success) {
						$('#questionDiv'+id).addClass('disabledQuestion');
						refreshDisabledness(id);
					}
				}
			};
			mediator.publish('ajax-json', params);
		}
		
		function updateSelectionStyles() {
			$(':radio').parent().parent().removeClass('selected')
		    $(':checkbox').parent().parent().removeClass('selected')
		    $(':radio:checked').parent().parent().addClass('selected')
		    $(':checkbox:checked').parent().parent().addClass('selected')
		}
		
		onclick="updateSelectionStyles()"
		$('#questionList').on('click', '.updateSelectionStyles', updateSelectionStyles);
		
		
		
		function sendQuestion(event) {
			event.preventDefault();
			var params = {
				url:'/presentation/ask_presenter_question/',
				type: "POST",
				data: $('#askQuestionForm').serialize(),
				onSuccess: function(data) {
					mediator.publish(data.notification, {
						text: data.message					
					});
					if(data.success) {
						$('#textResponse').val('')
						$('#askPresenterQuestion').toggle('slow');
					}
				}
			};
			mediator.publish('ajax-json', params);
		}
		$('#submitQuestion').on('click', sendQuestion);
		
		function refreshDisabledness(id) {
			$('#sub'+id).remove();
			$('#form'+id+' :input').attr("disabled", "disabled");
			if (!showingAnswered) {
				$('#questionDiv'+id).hide(300, function() {
					showDefaultIfEmpty();
				});
			}
			updateSelectionStyles();
		}
		
		function showDefaultIfEmpty() {
			if ($('#questionList :visible').children().length===0) {
				$('#defaultMessage').show();
			}
			else {
				$('#defaultMessage').hide();
			}
		}
		
		$('#top-bit').on('click', '#showAnswers', function() {
			$('.disabledQuestion').toggle(300, function() {
				showDefaultIfEmpty();
			});
			if (showingAnswered) {
				$('#showAnswers').html('Show my answers');
			} else {
				$('#showAnswers').html('Hide my answers');
			}
			showingAnswered = !showingAnswered;
		});
		$('.disabledQuestion').each(function(i, qDiv) {
			var id = $(qDiv).find("input[name='quesid']").val()
			refreshDisabledness(id)
		});
		
		$('#top-bit').on('click', '#askPQuestion', function() {
			$('#askPresenterQuestion').toggle('fast');
		});
	}
	return {
		setup: setup
	};
	
});