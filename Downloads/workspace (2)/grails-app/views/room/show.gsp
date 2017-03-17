<%@ page import="org.ucanask.Room" %>
<!doctype html>
<html>
	<head>
		<g:set var="entityName" value="${roomInstance.name}" />
		<title><g:message code="default.site.label" args="[entityName]" /></title>
	</head>
	<body>
	
<%--	BreadCrumb--%>
		<content tag="tree">
			<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>		
			<g:link controller="room" action="show" id="${roomInstance.id}">${roomInstance.name }</g:link>
		</content>   
	
<%--	Edit button--%>
		<g:if test="${view == 'Presenter'}"> 
			<div class="right" style="margin: 30px 13px 0px 0px;">
				<ucanask:bluebutton class="controlButtons" name="createPres" id="${roomInstance?.id}" controller="room" action="edit">
					<img src="/images/ui/edit.png" />
				</ucanask:bluebutton>
			</div>
		</g:if>
	
<%--	Title--%>
		<div class="page-controls">
			<img src="/images/presentations.png"/>
			<h2 style="margin-bottom:0">${roomInstance.name}</h2>
		</div>
		<br/>
				
<%--	Lecture List--%>
		<div id="show-room" class="group" role="main">			
			<g:if test="${view == 'Audience'}">
	        	<g:render template="audienceRoomShow" value="${roomInstance}" var="roomInstance"/>
	        </g:if>
			<g:if test="${view == 'Presenter'}">        
	        	<g:render template="presenterRoomShow" value="${roomInstance}" var="roomInstance"/>
	        </g:if>
		</div>
	</body>
</html>
 