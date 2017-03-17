
<%@ page import="org.ucanask.Room"%>
<!doctype html>
<html>
<head>
<g:set var="entityName"
	value="${message(code: 'default.label.rooms', default: 'Room')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<content tag="tree"> <g:link controller="room" action="list">
		<g:message code="default.label.rooms" default="Rooms" /></g:link>
	</content>

	<div class="grids">
		<div class="grid-16">
			<div class="grids">
				<div class="grid-16">
					<img src="/images/courses.png" class="extra-info" style="padding:5px;"/>
					<div class="right mobile-no-float">
						<g:form action="goToPresentation" class="qForm">
							<label for="presentationCode">Open a <g:message code="default.label.presentation" default="presentation" /> using a provided code: </label>
							<g:textField name="presentationCode" required="" value="" />
							<span id="join-button" class="button_normal">Join</span>
						</g:form>
					</div>
				</div>
				
			</div>
			
			<g:if test="${myRooms != null && myRooms.size() > 0}">
				<div id="show-room" class="group" role="main">
					<div class="label" onclick="toggleExpandItem('1')">
						<g:img id="imgDown1" file="ui/arrow_dropdown.png" />
						<g:img id="imgUp1" file="ui/arrow_dropup.png" style="display:none" />
						<span class="label bold">My <g:message code="default.label.rooms" default="Rooms" /></span>
					</div>
					<div id="dropdownDiv1">
						<g:render template="presenterRoomList" bean="${myRooms}"
							var="rooms" />
					</div>
				</div>
			</g:if>
			
			<g:if test="${invitedRooms.size() != 0}">
				<div id="show-room" class="group" role="main">
					<div class="label" onclick="toggleExpandItem('2')">
						<g:img id="imgDown2" file="ui/arrow_dropdown.png" />
						<g:img id="imgUp2" file="ui/arrow_dropup.png" style="display:none" />
						<span class="bold">Invited <g:message code="default.label.rooms" default="Rooms" /></span>
					</div>
					<div id="dropdownDiv2">
						<g:render template="audienceRoomList" bean="${invitedRooms}"
							var="rooms" />
					</div>
				</div>
			</g:if>
			
			<g:if test="${isAdmin}">
				<div id="show-room" class="group" role="main">
					<div class="label" onclick="toggleExpandItem('3')">
						<g:img id="imgDown3" file="ui/arrow_dropdown.png" />
						<g:img id="imgUp3" file="ui/arrow_dropup.png" style="display:none" />
						<span class="bold">Administered <g:message code="default.label.rooms" default="Rooms" /></span>
						<div class="right">
							<ucanask:bluebutton name="createRoom"
								action="create">Create New Course
							</ucanask:bluebutton>
						</div>
					</div>
					<div id="dropdownDiv3">
						<g:render template="adminRoomList" bean="${allRooms}" var="rooms" />
					</div>
					<br />
				</div>
			</g:if>
		</div>
	</div>
	
	<r:script disposition="defer">
		require(['views/room/list']);
	</r:script>
	
</body>
</html>
