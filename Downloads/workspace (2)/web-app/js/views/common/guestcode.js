
define(["jquery"], function($){
	// Update the audience members questions for the presenter
	function guestCode(id) {

		var currrentPresentationVersion;
		
		function myTimestamp(){
		    tstmp = new Date();    
		    return tstmp.getTime();
		}
		
		//Callback function to replace the list of questions
		function toggle(data){
			$('#guestCode').toggle(150)
		}
		
		//Looping callback function to check for new questions
		function geGuestCodeAjax(){
			$.getJSON('/presentation/presentation_open_version/' + id,function(data){
				
				if (currrentPresentationVersion != data){
					
					//Update the page
					$.getJSON('/presentation/presentation_open_version/' + id + '?ignorecache=' + myTimestamp(), toggle);
					
					currrentPresentationVersion = data;
				}

				//Loop
				setTimeout(geGuestCodeAjax,1000);

				
			});
		}
		
		//Initial callback to set the version
		function initialiseGuestCodeAjax(data){
			
			
			currrentPresentationVersion = data;
			setTimeout(geGuestCodeAjax,1000);
		}
		
		//Start of execution
		$.getJSON('/presentation/presentation_open_version/' + id + '?ignorecache=' + myTimestamp(), initialiseGuestCodeAjax);
		
	}
	
	return {
		setup: guestCode
	};
})