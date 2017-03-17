
define(["jquery","mediator"], function($,mediator){
	//On load request a list of the current text responses
	//and store them. If the number of responses changes
	//the data is replaced in the page.

	function operationWinterWolf(id) {

		var currrentQuestionOccurrenceVersion;
		
		//Callback function to replace the list of responses
		function updateTextResponses(data){
			$('#textResponseList').html(''); //Empty the list
			for(key in data){
				var li = $('<li id="responseItem' + key + '"><span class="button_normal removeResponseButton noPadding" id="removeResponse' + key +'"><img class="noPadding" src="/images/ui/delete_white_small.png" alt="Remove Response" /></span></li>');
				$('#textResponseList').append(li.append('&nbsp;' + data[key]));
				bindRemoveResponseButton(id,key);
			}
		}
		
		//Looping callback function to check for new responses
		function getTextResponsesAjax(){
			$.getJSON('/questionOccurrence/getVersion/' + id,function(data){
				
				if (currrentQuestionOccurrenceVersion != data){
					
					//Update the page
					$.getJSON('/questionOccurrence/getResponsesJSON/' + id + '?displayType=rawFT' + '&ignorecache=' + myTimestamp(), updateTextResponses);
					
					currrentQuestionOccurrenceVersion = data;
				}

				//Loop
				setTimeout(getTextResponsesAjax,1000);

				
			});
		}
		
		//Initial callback to set the version
		function initialiseAudiencePresentationAjax(data){
			
			
			currrentQuestionOccurrenceVersion = data;
			setTimeout(getTextResponsesAjax,1000);
		}
		
		//Start of execution
		$.getJSON('/questionOccurrence/getVersion/' + id + '?ignorecache=' + myTimestamp(), initialiseAudiencePresentationAjax);
		
	}
	
	function showFilter() {
		
		$('#showFilter').on('click', function() {
			$('#filter').toggle(200, function() {
				if ($('#filter').css('display') === 'none') {
					$('#showFilter').prop('title', "Show filtering options")
				}
				else {
					$('#showFilter').prop('title', "Hide filtering options")
				}
			});			
		});
	}
	
	function removeResponseAnswer(rId){
		$('#responseItem'+rId).remove();
	}
	
	function bindRemoveResponseButton(qId, rId){
		
		//console.debug($('#removeResponse' + rId));
		
		 $('#removeResponse' + rId).on('click',function(){
	          var params = {
	              url: '/questionOccurrence/removeResponseAjax',
	              onSuccess: removeResponseAnswer(rId),
	              data: {
	                  qoccId: qId,
	                  respId: rId
	              }
	          };
	          
	          mediator.publish("ajax-json",params);
	      });
	}

	return {
		operationWinterWolf: operationWinterWolf,
		showFilter: showFilter,
		bindRemoveResponseButton: bindRemoveResponseButton
	};
});

