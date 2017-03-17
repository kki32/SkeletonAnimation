define(["jquery", "mediator", "ajax",  "jquery-ui"], function($,mediator){
	function setup(id) {
		/** Expand and hide **/
		function toggleExpand(event) {
			var qForm = $(event.currentTarget).parent()
			qForm.find('.imgDown').toggle();
			qForm.find('.imgUp').toggle();
			qForm.find('.dropdownDiv').toggle(300);
		}
		
		$(".toggleDiv .label").on("click", toggleExpand);
	}
	return {
		setup: setup
	}
});