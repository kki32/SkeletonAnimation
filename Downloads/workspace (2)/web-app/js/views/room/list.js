define(['jquery'], function($){
	
	function join(event){
		$(event.currentTarget).parent().submit();
	}
	
	$("#join-button").on("click", join);
	$("#PresentationCode").on("keypress", function(event){
		if(event.keyCode === 13){
			join();
		}
	});
});