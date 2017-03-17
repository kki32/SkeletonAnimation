define(["jquery"], function($){

	var common = {
			hidden: true,
			questionOccurrenceId: null
	};
	common.toggleChartVisibility = function() {
		if ($(".chartDiv").css('opacity') === '1') {
			$(".chartDiv").animate({
				opacity: '0.01'
			}, {
				duration: 250,
				complete: function() {
					btnShow(false)
				}
			});
		}
		else {
			$(".chartDiv").animate({
				opacity: '1'
			}, {
				duration: 250,
				complete: function() {
					btnShow(true)
				}
			});
		}
	}
	
	$("#chartVisibleBtn").on("click", common.toggleChartVisibility);
	function btnShow(show) {
		if (show) {
			$("#chartVisibleBtn").html("Hide");
			$("#chartVisibleBtn").prop('title', "Hide this chart");
			common.hidden = false;
		}
		else {
			$("#chartVisibleBtn").html("Show");
			$("#chartVisibleBtn").prop('title', "Show this chart");
			common.hidden = true;
		}
	}
	
	
	function prevQuestion(id) {
		window.location.assign("/questionOccurrence/prevQuestion/" + id + "?hide=" + common.hidden);
	}
	
	function nextQuestion(id) {
		window.location.assign("/questionOccurrence/nextQuestion/" + id + "?hide=" + common.hidden);
	}
	common.setupNavButtons =function(){
		$("#prevQBtn").on("click", function(){
			prevQuestion(common.questionOccurrenceId);
		});
		$("#nextQBtn").on("click", function(){
			nextQuestion(common.questionOccurrenceId);
		});
	}
	return common;
});