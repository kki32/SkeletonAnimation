<%@ page import="org.ucanask.Presentation" %>
<div class="grids">
	<div class="grid-10 offset-1">
		<g:message code="default.label.presentationName" default="Lecture Name" />: <span class="right hidden" id="presNameError"></span>
		<g:textField class='disableEnterTextField home' name="name" id="name" required="" value="${presentationInstance.name != null ? presentationInstance.name : ''}"/>
	</div>
</div>

