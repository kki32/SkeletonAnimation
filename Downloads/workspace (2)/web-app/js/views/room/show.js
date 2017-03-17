/**  Updates the list of presentations. */
define(["jquery", "mediator", "ajax"], function($,mediator){
	
	var show = {};

	function operationSneakyMonkey(roomid) {

		var currentPresentationIds;
		
		function removePresentation(id) {
			$('#pres'+id).hide(300, function() { $(this).remove(); });
		}
		
		function getPresentationAjax(id) {
			var params = {
					url: '/presentation/get_listed_presentation/'+roomid+'?presid='+id,
					onSuccess: function(data){
						$('#presList').append($(data));
					}
			};
			mediator.publish("ajax-json", params);
		}
		
		/** Checks the latest presentation ids against the currently displayed ones.
		 *  Updates the list of presentations. */
		function getPresentationListAjax(){
			var params = {
					url: '/presentation/update_presentation_list/' + roomid,
					onComplete: function() {
						setTimeout(getPresentationListAjax,4000);
					},
					onSuccess: function(data){
						if (currentPresentationIds) {
							var a = [];
							pIds = data.ids
							for (var i in pIds) {
								a[pIds[i]] = 1;
							}
							for (var i in currentPresentationIds) {
								if (!a[currentPresentationIds[i]]) {
									removePresentation(currentPresentationIds[i]);
								}
								else {
									a[currentPresentationIds[i]] = 2;
								}
							}
							for (var i in a) {
								if (a[i]==1) {
									getPresentationAjax(i);
								}
							}
						}
						currentPresentationIds = data.ids;
					}
			};
			mediator.publish("ajax-json", params);
		}
		getPresentationListAjax();
	}
	show.operationSneakyMonkey = operationSneakyMonkey;
	return show;
});