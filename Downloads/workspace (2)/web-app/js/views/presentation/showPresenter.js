/**
 * All the js for making the presentation show page work
 */

define(["jquery", "mediator", "ajax", "notificationHandler", "jquery-ui"], function($, mediator){

	var showPresenter = {};
	
	function setup(id) {
		
		/**
		 * Confirms and then hides the presentation, to be removed on next save.
		 */
		function deleteQuestion(id) {
			if (confirm("Are you sure you want to remove this presentation?")) {
				var params = {
						url:'/presentation/delete/' + id,
						type: "POST",
						onSuccess: function(data) {
							if(data.success) {
								window.location = '/room/show/' + data.roomId
							} else {
							    mediator.publish("notification-alert", {text: "There was a problem deleting this presentation"});
							}
						}
				};
				mediator.publish('ajax-json', params);
			}
		}
		
		$('#delete').on('click', function() {
			deleteQuestion(id);
		});
		
	}

	/** Allow questions to be draggable to change their order **/
	function setupDraggable(id) {
		$( "#sortable" ).sortable({
			revert: true,
			containment: 'parent',
			stop: function(event, ui) {
				var array = [].slice.call(this.getElementsByTagName('li'), 0);
				var index = array.indexOf(ui.item[0]);
				var questionId = ui.item[0].id;
				var params = {
					url: '/presentation/sort_questions/' + id,
					data: { 'index':index, 'questionId':questionId },
					type: 'post'
				}
				mediator.publish("ajax-json", params);
			}
		});
		$( "ul, li" ).disableSelection();
	}
	
	function reloadPresentersResponsesReceived(id) {

		function getPresenterPresentationAjax(){
			
			var params = {
				url: '/presentation/get_total_responses_json/' + id,
				onComplete: function() {
					setTimeout(getPresenterPresentationAjax,4000);
				},
				onSuccess: function(data){
					for (var ques in data.responseCounts) {
						$('#responseCount'+ques).html(data.responseCounts[ques]);				
					}
				}
			};

			mediator.publish("ajax-json", params);
		}
		
		function initialisePresenterPresentationAjax(){
			setTimeout(getPresenterPresentationAjax,4000);
		}
		
		getPresenterPresentationAjax();
		
	}
	
	showPresenter.setup = setup;
	showPresenter.setupDraggable = setupDraggable;
	showPresenter.reloadPresentersResponsesReceived = reloadPresentersResponsesReceived;
	
	return showPresenter;
	
});