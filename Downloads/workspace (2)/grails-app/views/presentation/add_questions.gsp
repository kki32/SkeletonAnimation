
<%@ page import="org.ucanask.Presentation" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'presentation.label', default: 'Presentation')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
	
		<%-- Breadcrumb trail	--%>
		<content tag="tree">
			<g:link controller="room" action="list">Rooms</g:link><sep/>
			<g:link controller="room" action="show" id="${roomInstance?.id}">${roomInstance?.toString() }</g:link><sep/>
			<g:link controller="presentation" action="show" id="${presentationInstance?.id}">${presentationInstance?.name}</g:link><sep/>
			<g:link controller="presentation" action="add_questions" id="${presentationInstance?.id}">Add Questions</g:link>
		</content>
			
			<br/>
			<h3><g:message code="default.label.presentationNewQuestions" default="Question"/></h3>
			<fieldset class="buttons">
			<g:form>
				<g:hiddenField name="id" value="${roomInstance?.id}" />
				<label for="textUser">
					<g:message code="default.question.find" default="Question Filter:" />
				</label>
				<g:textField name="question"/>
				<g:submitButton name="findQuestion" class="medium blue button" value="${message(code: 'default.button.find.label', default: 'Find')}" />
			</g:form>
			</fieldset>
			
			<g:form action="addPlannedQuestions" >
				<fieldset class="form">
			
				<g:hiddenField name="id" value="${presentationInstance?.id}" />
				<table><tbody>
				<g:each in="${allQuestions}" var="question">
					<g:if test="${presentationInstance?.questionNotUsed(question)}">
						<tr><td>
							<input type="checkbox" name="questionsToAdd" value="${question.id}" />  ${question.toString()}
						</td></tr>
					</g:if>
				</g:each>
				</tbody></table>
				</fieldset>
				
				<g:submitButton name="addPlannedQuestions" class="medium blue button" value="${message(code: 'default.button.add.label', default: 'Add')}" />
				
			</g:form>
		</div>
	</body>
</html>
