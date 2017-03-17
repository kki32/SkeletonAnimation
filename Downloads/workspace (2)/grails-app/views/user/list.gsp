
<%@ page import="org.ucanask.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<content tag="tree">
			<g:link controller="user" action="list">Users</g:link>
		</content>
			<br />
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
                        
                        <g:sortableColumn property="displayName" title="${message(code: 'user.displayName.label', default: 'Display Name')}" />

                        <g:sortableColumn property="accountExpired" title="Account Expired / Locked" />
					
						<g:sortableColumn property="email" title="${message(code: 'user.email.label', default: 'Email')}" />
                    
                        <td style="text-align:center; color: #000"><b><g:message code="user.roles.label" default="Roles" /></b></td>
                    
					</tr>
				</thead>
				<tbody>
				<g:each in="${userInstanceList}" status="i" var="userInstance">
				
				<g:if test="${userInstance.authorities.authority.contains('ROLE_ADMIN')}"><tr style="background:#fdd"></g:if>
				<g:else><tr class = "${(i % 2) == 0 ? 'even' : 'odd'}"></g:else>
				
						<td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
					
						<td>${fieldValue(bean: userInstance, field: "displayName")}</td>
					
						<td><g:formatBoolean boolean="${userInstance.accountExpired}" /> / <g:formatBoolean boolean="${userInstance.accountLocked}" /></td>
					
						<td>${fieldValue(bean: userInstance, field: "email")}</td>
                        
                        <td><g:each in="${userInstance.authorities.authority}" status="p" var="authority">${authority.substring(5,6) + authority.substring(6).toLowerCase()}, </g:each></td>
                    
					</tr>
				</g:each>
				</tbody>
			</table>
			<fieldset class="buttons">
			    <g:link class="nice small blue radius button" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link>
			</fieldset>
			<div class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</div>
	</body>
</html>
