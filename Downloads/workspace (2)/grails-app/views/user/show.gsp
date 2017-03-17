
<%@ page import="org.ucanask.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<content tag="tree">
			<g:link controller="user" action="list">Users</g:link><sep/>
			<g:link controller="user" action="show" id="${userInstance?.id}">${userInstance?.toString()}</g:link>
		</content>

			<ul class="property-list user">
			
				<g:if test="${userInstance?.username}">
				<li class="fieldcontain">
					<span id="username-label" class="property-label" style="font-weight:bold;"></b><g:message code="user.username.label" default="Username" />:</span>
					
						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${userInstance}" field="username"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.displayName}">
				<li class="fieldcontain">
					<span id="displayName-label" class="property-label" style="font-weight:bold;"><g:message code="user.displayName.label" default="Display Name" />:</span>
					
						<span class="property-value" aria-labelledby="displayName-label"><g:fieldValue bean="${userInstance}" field="displayName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.accountExpired}">
				<li class="fieldcontain">
					<span id="accountExpired-label" class="property-label" style="font-weight:bold;"><g:message code="user.accountExpired.label" default="Account Expired" />:</span>
					
						<span class="property-value" aria-labelledby="accountExpired-label"><g:formatBoolean boolean="${userInstance?.accountExpired}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.accountLocked}">
				<li class="fieldcontain">
					<span id="accountLocked-label" class="property-label" style="font-weight:bold;"><g:message code="user.accountLocked.label" default="Account Locked" />:</span>
					
						<span class="property-value" aria-labelledby="accountLocked-label"><g:formatBoolean boolean="${userInstance?.accountLocked}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label" style="font-weight:bold;"><g:message code="user.email.label" default="Email" />:</span>
					
						<span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${userInstance}" field="email"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.enabled}">
				<li class="fieldcontain">
					<span id="enabled-label" class="property-label" style="font-weight:bold;"><g:message code="user.enabled.label" default="Enabled" />:</span>
					
						<span class="property-value" aria-labelledby="enabled-label"><g:formatBoolean boolean="${userInstance?.enabled}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.passwordExpired}">
				<li class="fieldcontain">
					<span id="passwordExpired-label" class="property-label" style="font-weight:bold;"><g:message code="user.passwordExpired.label" default="Password Expired" />:</span>
					
						<span class="property-value" aria-labelledby="passwordExpired-label"><g:formatBoolean boolean="${userInstance?.passwordExpired}" /></span>
					
				</li>
				</g:if>
				
				<g:if test="${userInstance?.authorities}">
                <li class="fieldcontain">
                    <span id="authorities-label" class="property-label" style="font-weight:bold;"><g:message code="user.authorities.label" default="Authorities" />:</span>
                    
                        <span class="property-value" aria-labelledby="authorities-label"><g:each in="${userInstance.authorities.authority}" status="p" var="authority">${authority.substring(5,6) + authority.substring(6).toLowerCase()}, </g:each></span>
                    
                </li>
                </g:if>
			
			</ul>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${userInstance?.id}" />
					<g:link class="nice small blue radius button" action="edit" id="${userInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="nice small red radius button" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure you want to delete this user?')}');" />			
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
