<g:javascript>
<%--	alert("${rooms}");--%>
</g:javascript>
<div>
    <g:if test="${!rooms}">
	    <div class="listed">
        	No rooms have been created.	
        </div>
    </g:if>
	<g:each in="${rooms}" status="i" var="room">
		<div class="grids listed">
				<div class="grid-15 float-left">					
					<g:link action="show" id="${room.id}">
						<b>${room.name}</b></g:link>
						<span class="extra-info">
						 (${(room.invited.size() == 0) ? 'no' : room.invited.size()} 
						audience member${(room.invited.size() == 1) ? '' : 's'} invited)
						</span>
				</div>
				<div class="grid-1 float-left">
					<ucanask:bluebutton style="text-align:left" action="edit" id="${room.id}"><img src="/images/ui/edit.png" />
					</ucanask:bluebutton>
				</div>
		</div>
	</g:each>
</div>
