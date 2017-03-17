<%@ page import="org.ucanask.Room" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'room.label', default: 'Room')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	
	<body>		
	<content tag="tree">
		<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
		<g:link controller="room" action="create">Create</g:link>
	</content>
			
		<g:hasErrors bean="${roomInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${roomInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
		</g:hasErrors>
		
		<br/>
         <div class="grids">
         	<div class="grid-16">
	         <g:form class="offset-1">
				<label for="name"> <g:message code="default.rooom.name.label"
					default="Room Name" /> <span class="required-indicator">*</span>
				</label>
				<g:textField id="name" placeholder="Course Name" name="name" required="" size="60" value="${roomInstance?.name}" />
				<g:actionSubmit class="button_normal" action="save" value="Create"/>
			</g:form>        
          	</div>
         </div>
         
	</body>
</html>
