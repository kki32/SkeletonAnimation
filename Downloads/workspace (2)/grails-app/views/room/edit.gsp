<%@ page import="org.ucanask.Room" import="org.ucanask.User" %>
<!doctype html>
<html>
	<head>

		<g:set var="entityName" value="${message(code: 'room.label', default: 'Room')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
	<%-- Breadcrumb trail	--%>
		<content tag="tree">
			<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
			<g:link controller="room" action="show" id="${roomInstance?.id}">${roomInstance?.name}</g:link><sep/>
			<g:link controller="room" action="edit" id="${roomInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
		</content>
		
		<g:hasErrors bean="${roomInstance}">
		<ul class="errors" role="alert">
			<g:eachError bean="${roomInstance}" var="error">
			<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
			</g:eachError>
		</ul>
		</g:hasErrors>
		<br/>
         
         <g:render template="form"/>
        
	</body>

<r:script>
	require(["views/room/edit"], function(edit){
		edit.courses = <%=coursesJson %>;
		edit.setup(${roomInstance.id});
	});
</r:script>

<g:if test="${request.xhr}">
  <r:layoutResources disposition="defer"/>
</g:if>
</html>