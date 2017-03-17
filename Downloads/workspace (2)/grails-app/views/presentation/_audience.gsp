
<!--  Need to change when this link displays -->
<!-- <g:link action="_presenter" id="{presentationInstance.id}">Go to presenter view </g:link> -->
<!-- Javascript -->

<r:script disposition='defer'>
	require(['views/presentation/showAudience'], function(showAudience){
		showAudience.setup(${presentationInstance.id});
	});
</r:script>

<div class="grids" id="top-bit">
	<div class="grid-8 behind">
		<h2 style="margin-bottom:0">${presentationInstance.name}</h2>
	</div>
	<div class="grid-8 infront">
		<div class="text-right margin-mobile ">
			<span id="askPQuestion" <g:if test="${!presentationInstance.allowUserQuestions}">style="display: none;"</g:if>>
				<span class="button_normal"><g:message code='default.button.ask.presenter' default='Ask the Presenter a Question'/></span>
			</span>&nbsp
			<span class="button_normal" id="showAnswers">Show my answers</span>
		</div>
		<div style="display: none;" class="text-right" id="askPresenterQuestion">
			<form id="askQuestionForm">
				<g:hiddenField name="id" value="${presentationInstance?.id}" />	
				<g:textArea id="textResponse" name="textResponse" style="width: 100%;" required="" rows="3"/>
				<br />
				<g:if test="${userExists}">
					<span style="padding-bottom:10px;">Tick here to include your username with this question</span> 
					<input type="checkbox" name="anonymous" value="false"/>
				</g:if>
				<g:submitButton id="submitQuestion" name="ask" class="button_normal" value="${message(code: 'default.button.ask.label', default: 'Send')}" />
			</form>
		</div>
	</div>
</div>


<div class="text-center" id="defaultMessage" <g:if test="${questionsToShow.size()>0}">style="display:none;"</g:if>>There are no questions to answer</div>
<div id="questionList" style="padding-top: 25px;">
	<g:render template="questionView" collection="${questionsToShow}" var="questionOccurrence"/>
</div>