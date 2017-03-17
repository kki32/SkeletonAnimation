<g:javascript>
<%--	alert("${rooms}");--%>
</g:javascript>
<div>
	<g:each in="${rooms}" status="i" var="room">
		<div class="grids listed">
			<div class="grid-16">
				<img src="/images/ui/diamond_red.png" style="padding-bottom:2px"/>
				<g:link action="show" id="${room.id}">
				<b>${room.name}</b></g:link> - 
				<i>(${(room.invited.size() == 0) ? 'No' : room.invited.size()} 
					participant${(room.invited.size() == 1) ? '' : 's'} and 
					${room.presentations.size()} <g:message code="default.label.presentation" default="presentation" />${(room.presentations.size() == 1)? '' : 's'}<g:if test="${room.openPresentationCount() > 0}">, 
					of which ${room.openPresentationCount()} 
					${room.openPresentationCount() == 1 ? 'is' : 'are'} open</g:if>.)
				</i>						
			</div>
		</div>
	</g:each>
</div>
