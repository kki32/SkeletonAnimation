
<!--  Javascript -->
<r:script disposition="defer">

    require(["views/common/ui", "views/presentation/showPresenter", "views/presentation/createQuestion"], function(ui, showPresenter, createQuestion){
    	
    	function resetPres() {
    		$(".presentationToggle").off('click');
    		presToggleSetup();
    	}
    	
    	function resetQuest() {
    		$(".questionToggle").off('click');
    		questionToggleSetup();
    	}
    	
    	function presToggleSetup() { ui.setupToggle(".presentationToggle","/presentation/togglePresentationStatus/", {}, resetQuest); }
    	presToggleSetup();
    	function questionToggleSetup() { ui.setupToggle(".questionToggle", "/presentation/toggle_question/", {presid:${presentationInstance?.id}}, resetPres); }
    	questionToggleSetup(); 
    	ui.setupToggle(".feedbackToggle", "/presentation/enable_am_questions/", {});
    	ui.setupToggle(".guestToggle", "/presentation/allow_guests/", {}, function() { $('#guestCode').toggle(); });
    	
    	showPresenter.accessKey = ${accesskey};
    	showPresenter.setup(${presentationInstance?.id});
    	showPresenter.setupDraggable(${presentationInstance?.id});
    	showPresenter.reloadPresentersResponsesReceived(${presentationInstance.id});
    	
    	createQuestion.presid = ${presentationInstance?.id};
    });    
	
</r:script>

<g:render template="/common/mail" model="['presentationId': presentationInstance.id]" />

<div class="right">
	<g:render template="qr_code" model="['guestAllowed': guestAllowed, 'accesskey': accesskey, 'presentationId': presentationInstance.id]"/>
</div>
<%--Title--%>
<div class="page-controls" style="margin-top: 25px; margin-bottom: 30px;">
	<img  src="/images/questions.png"/>
	<h2>${presentationInstance?.name}</h2>
</div>

<%--Lecture Options--%>
<div class="group">
	<div class="label">
		<span class="bold">
			<g:message code="default.presentation.options" default="Public Presentation"/>
		</span>
	</div>
	<div> <!-- Used for listed formatting -->
		<div class="grids listed">
			<div class="grid-16 left">					
				<g:message code="default.label.amQustions.description" default="Allow guest to particpate in this lecture" />
				<div class="right" style="margin-right:5px">
					<ucanask:toggle name="guestToggle" active="${presentationInstance?.open}" id="${presentationInstance?.id}" />
				</div>
			</div>
		</div>
	</div>
	<div> <!-- Used for listed formatting -->
		<div class="grids listed">
			<div class="grid-16 left">
				<g:message code="default.label.amQuestions.description" default="Allow Audience Questions" />&nbsp;
				<g:link class="button_normal" action="show_am_questions" id="${presentationInstance?.id}">
					<g:message code="default.label.clickToView" default="Click here to View"/>
				</g:link>
				<div class="right" style="margin-right:5px">
					<ucanask:toggle name="feedbackToggle" active="${presentationInstance?.allowUserQuestions}" id="${presentationInstance?.id}" />
				</div>
				
			</div>
		</div>
	</div>
</div>

<%--Questions--%>
<div class="group">
	<div class="label">
		<span class="bold">
			<g:message code="default.label.questions" default="Questions"/>
		</span>
		<div class="right"><span class="button_normal" style="margin-right:11px;" id="createQuestion">
			<img alt="Add New Question" src="/images/ui/add_white.png"/>
		</span></div>
	</div>
	<g:if test="${presentationInstance?.questions}">
		<div class="grids margin-side-5">
		<div class="grid-16 left">	
			<ol id="sortable" class="sortableList">
				<g:each in="${presentationInstance.questions}" var="c">
				<g:set var="mcquestion" value="${c.askedQuestion.answerStrategy?.instanceOf(org.ucanask.AnswerStrategy.MultiChoiceStrategy)}" />
				<g:set var="ftquestion" value="${c.askedQuestion.answerStrategy?.instanceOf(org.ucanask.AnswerStrategy.FreeTextStrategy)}" />
					<li id="${c.id}" class="ui-state-default listed">
						<span style="font-weight: normal;">
						<g:if test="${mcquestion}">
							<g:img file="ui/icon_multichoice.png" />
						</g:if>
						<g:elseif test="${ftquestion}">
							<g:img file="ui/icon_openended.png" />
						</g:elseif>
						<g:link controller="questionOccurrence" action="show" id="${c.id}">${c}</g:link>
						<span class="right">
							<span id="responseCount${c.id}">${c.responses?.size()}</span> 
							<g:message code="default.button.response.label" default="Response"/>
						 	<ucanask:toggle name="questionToggle" active="${c.active}" id="${c.id}"/>
						</span>
						</span>
					</li>
				</g:each>
			</ol>
		</div>
		</div>
	</g:if>
</div>
<div class="grids">
	<div class="grid-16" id="questionList">
	</div>
</div>
<br /><br />