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
			<g:link controller="room" action="edit" id="${roomInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link><sep/>
			<g:link controller="room" action="students_manage" id="${roomInstance?.id}">Students</g:link>
		</content>
		
		<g:hasErrors bean="${roomInstance}">
		<ul class="errors" role="alert">
			<g:eachError bean="${roomInstance}" var="error">
			<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
			</g:eachError>
		</ul>
		</g:hasErrors>
		<br/>
		
		<h2>Student Management</h2>
         	
		<%-- Manage Students --%>	
		<div class="toggleDiv">
			<form class="resetForm qForm">
			<div class="label">
				<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
				<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
				<span id="studentTotal" class="qLabel bold">Automatic Enrolled Students<%--<g:message code="default.label.presenters" default="Students" />
					 (--%> (${roomInstance.automaticEnrolled.size()} in total)
				</span>
			</div>
		   
			<div class="dropdownDiv">
				<div id="autoList" class="grids listed" style="max-height: 200px; overflow: auto;">
					<ucanask:getAutomatic room="${roomInstance}" />
				</div>
			</div>
			</form>
		</div>
         	
		<%-- Manage Students --%>	
		<div class="toggleDiv">
			<form class="resetForm qForm">
			<div class="label">
				<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
				<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
				<span id="inviteTotal" class="qLabel bold">Invited Users<%--<g:message code="default.label.presenters" default="Students" />
					 (--%> (${roomInstance.invited.size()} in total)
				</span>
			</div>
		   
			<div class="dropdownDiv">
				<div id="inviteList" class="grids listed" style="max-height: 200px; overflow: auto;">
				</div>
			</div>
			</form>
		</div>
         	
		<%-- Manage Students --%>	
		<div class="toggleDiv">
			<form class="resetForm qForm">
			<div class="label">
				<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
				<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
				<span id="studentTotal" class="qLabel bold">Blocked Users<%--<g:message code="default.label.presenters" default="Students" />
					 (--%> (${roomInstance.blocked.size()} in total)
				</span>
			</div>
		   
			<div class="dropdownDiv">
				<div id="banList" class="grids listed" style="max-height: 200px; overflow: auto;">
				</div>
			</div>
			</form>
		</div>
        
	</body>
<r:script>
	require(["views/room/students"], function(student){
		student.setup(${roomInstance.id});
	});
</r:script>
</html>