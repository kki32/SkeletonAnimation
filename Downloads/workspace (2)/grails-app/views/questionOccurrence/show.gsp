<%@ page import="org.ucanask.QuestionOccurrence"%>
<!doctype html>
<html>

<%--<r:require modules="chartUtils" />--%>
<r:script>
    require(["views/questionOccurrence/show", "views/questionOccurrence/common"],function(show, common){
        common.hidden = ${hide}
        show.setupFreeTextFilter(${questionOccurrenceInstance.id});
        
    });
</r:script>
<% opacity = hide == 'true' ? '0.01' : '1' %>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<g:set var="entityName"	value="${message(code: 'questionOccurrence.label', default: 'QuestionOccurrence')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>	
</head>
<body>
	<%--Bread trail	--%>
	<content tag="tree">
		<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
		<g:link controller="room" action="show" id="${questionOccurrenceInstance.presentation.room.id}">${questionOccurrenceInstance.presentation.room.name}</g:link><sep/>
		<g:link controller="presentation" action="show" id="${questionOccurrenceInstance.presentation.id}">${questionOccurrenceInstance.presentation.name}</g:link><sep/>
		<g:link controller="questionOccurrence" action="show" id="${questionOccurrenceInstance.id}">${questionOccurrenceInstance}</g:link>
	</content>
	<g:render template="/common/mail" model="['presentationInstance': questionOccurrenceInstance.presentation]" />
	
	<!-- Display Graphs for MultiChoice Questions -->
	<g:if test="${questionOccurrenceInstance.askedQuestion.answerStrategy.instanceOf(org.ucanask.AnswerStrategy.MultiChoiceStrategy)}">
		<g:render template="multiChoiceView" value="${questionOccurrenceInstance.id}" var="questionOccurrenceInstance" />
	</g:if>

	<!-- Display Graphs for Freetext -->
	<g:if test="${questionOccurrenceInstance.askedQuestion.answerStrategy.instanceOf(org.ucanask.AnswerStrategy.FreeTextStrategy)}"> 
		<g:render template="freeTextView" value="${questionOccurrenceInstance.id}" var="questionOccurrenceInstance" />
	</g:if>
</body>
<r:script disposition='defer'>
	require(["views/common/guestcode"], function(guestcode) {
    	guestcode.setup(${questionOccurrenceInstance?.presentation.id});
   	});
</r:script>
</html>
