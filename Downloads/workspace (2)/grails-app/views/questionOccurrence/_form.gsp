		
<%@ page import="org.ucanask.QuestionOccurrence" %>

<div class="fieldcontain ${hasErrors(bean: questionOccurrenceInstance, field: 'askedQuestion', 'error')} required">
	<label for="askedQuestion">
		<g:message code="questionOccurrence.askedQuestion.label" default="Asked Question" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="askedQuestion" name="askedQuestion.id" from="${org.ucanask.Question.list()}" optionKey="id" required="" value="${questionOccurrenceInstance?.askedQuestion?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: questionOccurrenceInstance, field: 'responses', 'error')} ">
	<label for="responses">
		<g:message code="questionOccurrence.responses.label" default="Responses" />
		
	</label>
	<g:select name="responses" from="${org.ucanask.Responses.Response.list()}" multiple="multiple" optionKey="id" size="5" value="${questionOccurrenceInstance?.responses*.id}" class="many-to-many"/>
</div>

