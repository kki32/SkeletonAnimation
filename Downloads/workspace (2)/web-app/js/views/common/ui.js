define(['jquery', 'mediator', 'jquery-backgroundpos', 'ajax'], function($, mediator){
	var ui = {};
	
	function setupToggle(name, url, params, callbackOnChange) {

		function toggleButtonHandler(event) {
		    var button = $(this);
			if (!button.hasClass("state-moving")) {
				if (button.hasClass("state-on")) {
					button.removeClass("state-on").addClass("state-off");
				} else {
					button.removeClass("state-off").addClass("state-on");
				}
			    animateSwitch(button);
			    changeState(button);
			}
		}
		
		function animateSwitch(button) {
			var positionCss = button.hasClass("state-off") ? "-36px 0" : "0px 0";
		    button.addClass("state-moving");
		    button.animate({backgroundPosition: positionCss}, {
		    	complete: 
		    		function() {
		    			button.removeClass("state-moving");
		        	},
		    	queue: true
		    });
	    }
	    	
		function changeState(button) {
			params.change = true;
			
			var jsonParams = {
				url: url+button.attr('id'),
				type: "POST",
				data: params,
				onSuccess: function(data) {

					if (!data.success) {
						// Something very wrong; refresh
						location.reload();
					}
					else if (!button.hasClass(data.state)) {
						button.removeClass("state-on").removeClass("state-off");
						button.addClass(data.state);
						animateSwitch(button);
					}
					if(data.success) {
						callbackOnChange && callbackOnChange();
					}
				}
			}
			mediator.publish('ajax-json', jsonParams);
		}
		
		$(name).on('click', toggleButtonHandler);
		function startOff() {
			$(name).each(function() {
			    var button = $(this);
				params.change = false;

				var jsonParams = {
					url: url+button.attr('id'),
					type: "POST",
					data: params,
					onSuccess: function(returnData) {
						if (!returnData.success) {
							// Something very wrong; refresh
							location.reload();
						}
						else if (returnData.state=="state-on") {
							button.css('backgroundPosition', "0px 0");
							
						}
						else {
							button.css('backgroundPosition', "-36px 0");
						}
					}
				};
				mediator.publish('ajax-json', jsonParams);
			});
		}

		function toggleForIE(button, change) {
		    var id = button.attr('id')
		    if (id!=null) { id = id.replace('-ie', ''); }
		   
			params.change = change;
			var jsonParams = {
				url: url+id,
				type: "POST",
				data: params,
				onSuccess: function(data) {
					if (!data.success) {
						// Something very wrong; refresh
						location.reload();
					}
					else if (data.state=="state-on") {
						$(button).html('Disable');
					}
					else {
						$(button).html('Enable');
					}
				}
		    }
			mediator.publish('ajax-json', jsonParams);
		}
		
		function ieToggleHandler(event) {
			toggleForIE($(this), true);
		}
		
		function startOffIE() {
			$(name+'-ie').each( function() { toggleForIE($(this), false); } );
		}
		
		$(name+'-ie').click(ieToggleHandler);

		startOff(); // Fixes back button problem
		startOffIE();
		
	}
	ui.setupToggle = setupToggle;
	// Forces browser to re-run js on back (history) 
	window.unload=function(){};
	return ui;
	
});
