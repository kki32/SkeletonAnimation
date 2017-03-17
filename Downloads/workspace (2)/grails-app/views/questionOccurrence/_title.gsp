<%--Title, on/off & nav--%>

<div class="graphHeading text-center">

	<span class="large">${questionOccurrenceInstance.askedQuestion.toString()}</span>&nbsp;&nbsp;
	<ucanask:toggle name="questionToggle" active="${questionOccurrenceInstance.active}" id="${questionOccurrenceInstance.id}"/>		
	<div>
		<span id="prevQBtn" class="button_normal" title="Display previous question">Previous Question</span>
		<span id="nextQBtn" class="button_normal" title="Display next question">Next Question</span>
	</div>
</div>
<r:script disposition="defer">
	require(["views/common/ui", "views/questionOccurrence/common"], function(ui, common){
		ui.setupToggle(".questionToggle", "/presentation/toggle_question/", {presid:${questionOccurrenceInstance?.presentation.id}});
		common.questionOccurrenceId = ${questionOccurrenceInstance.id};
		common.setupNavButtons();
	});
</r:script>