<%@ page import="org.ucanask.Presentation" %>
<%@ page import="org.ucanask.Room" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'presentation.label', default: 'Presentation')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body> 
	
		<content tag="tree">
			<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
			<g:link controller="room" action="show" id="${roomId}">${roomName }</g:link><sep/>
			<g:link controller="presentation" action="create" params="[roomId:roomId]">Create</g:link>			
		</content>
		
		<br/>
         <div class="grids">
         	<div class="grid-16">
	         <g:form action="save" class="offset-1">
				<g:hiddenField name="roomId" value="${roomId}"/>
				<label for="name"> <g:message code="default.presentation.name.label"
					default="Lecture Title" /> <span class="required-indicator">*</span>
				</label>
				<g:textField id="name" placeholder="Lecture Title" name="name" required="" size="60" />
				<g:actionSubmit class="button_normal" action="save" value="Create"/>
			</g:form>        
          	</div>
         </div> 
	</body>
</html>
