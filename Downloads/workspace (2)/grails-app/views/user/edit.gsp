<%@ page import="org.ucanask.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<content tag="tree">
			<g:link controller="user" action="list">Users</g:link><sep/>
			<g:link controller="user" action="show" id="${userInstance?.id}">${userInstance?.toString()}</g:link><sep/>
			<g:link controller="user" action="edit" id="${userInstance?.id}">Edit</g:link>
		</content>
			<g:hasErrors bean="${userInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${userInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form method="post" >
				<g:hiddenField name="id" value="${userInstance?.id}" /> 
				<g:hiddenField name="version" value="${userInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
					<g:each in="${roles}" var="role">${role}<g:checkBox name="Roles" value="${role}" checked="${userInstance.authorities.authority.contains(role)}" /></g:each>
					<br />Update User Details? <g:checkBox name="updateDetails" title="Update Details" checked="false"/>
				</fieldset> 
				<g:form>
						<g:actionSubmit class="nice small blue radius button" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
	                    <g:actionSubmit class="nice small red radius button" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
	   			</g:form>
			</g:form>
	</body>
</html>
