
<%@ page import="org.ucanask.Presentation" %>
<!doctype html>
<html>
	<head>
		<g:set var="entityName" value="${presentationInstance.name}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>

		

	<%-- This shows the edit / delete / on/off menu buttons if the user is a presenter --%>
	<g:if test="${view == 'Presenter'}">
		<div class="bob" style="float:right">
			<g:set var="guestAllowed" value="${presentationInstance.open}" />
			<ucanask:bluebutton title="Edit Lecture Questions" class="controlButtons" name="editPres"
				params='[id: "${presentationInstance?.id}"]'
				controller="presentation" action="edit">
				<img src="/images/ui/edit.png" style="padding-left: 2px;"/>
			</ucanask:bluebutton>&nbsp;
			<ucanask:bluebutton title="Delete Lecture" class="controlButtons button_normal" id="delete"><img
				src="/images/ui/delete_white_small.png" style="vertical-align: -5px; padding-left: 2px;"/>
			</ucanask:bluebutton>&nbsp;
			<ucanask:toggle name="presentationToggle"
				active="${presentationInstance.active}"
				id="${presentationInstance.id}" />
		</div>
	</g:if>

	<div id="show-presentation">
			<g:if test="${view == 'Audience'}">
				<g:if test="${isGuest}">
					<content tag="tree">
						<g:link controller="presentation" action="show" id="${presentationInstance.id}" params='["presentationCode": presentationInstance.accessKey]'>${presentationInstance.name}</g:link>
					</content>
				</g:if>
				<g:else>
				<content tag="tree">
					<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
					<g:link controller="room" action="show" id="${presentationInstance.room.id}">${presentationInstance.room.name}</g:link><sep/>
					<g:link controller="presentation" action="show" id="${presentationInstance.id}">${presentationInstance.name}</g:link>
				</content>
				</g:else>
	              <g:render template="audience" value="${presentationInstance}" var="presentationInstance"/>
	        </g:if>
			<g:if test="${view == 'Presenter'}"> 
				<%-- Breadcrumb trail	--%>
				<content tag="tree">
					<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
					<g:link controller="room" action="show" id="${presentationInstance.room.id}">${presentationInstance.room.name}</g:link><sep/>
					<g:link controller="presentation" action="show" id="${presentationInstance.id}">${presentationInstance.name}</g:link>
				</content>       
	              <g:render template="presenter" value="${presentationInstance}" var="presentationInstance"/>
	        </g:if>
        </div>
	</body>
</html>