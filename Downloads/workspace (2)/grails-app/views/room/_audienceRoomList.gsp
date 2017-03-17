<g:javascript>
<%--	alert("${rooms}");--%>
</g:javascript>
<div>
	<g:each in="${rooms}" status="i" var="room">
		<div class="grids listed">
			<div class="grid-14 float-left">
				<img src="/images/ui/diamond_red.png" style="padding-bottom:2px"/>
				<g:link action="show" id="${room.id}">
					<b>${room.name}</b>
				</g:link>
			</div>
		</div>
	</g:each>
</div>
 