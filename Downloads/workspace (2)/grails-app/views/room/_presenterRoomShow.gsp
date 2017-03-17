<r:script disposition="defer">
	require(["views/common/ui"], function(ui){
		ui.setupToggle(".presToggle", "/presentation/togglePresentationStatus/", {});
	});
</r:script>
<div class="label"><span class="bold"><g:message code="default.label.presentations" default="Presentations" /></span>
	<div class="right">
		<g:link class="button_normal" style="margin-right:6px;" name="create" params='[roomId: "${roomInstance?.id}"]' controller="presentation" action="create">
			<img alt="Add New Lecture" src="/images/ui/add_white.png"/>
		</g:link>
	</div>
</div>

<div> <!-- Used for listed formatting -->
<g:if test="${roomInstance?.presentations}">
	<g:each in="${roomInstance.presentations}" var="p">
		<div class="grids listed">
			<div class="grid-16">
				<g:link controller="presentation" action="show" params='[roomId: "${roomInstance?.id}"]' id="${p.id}">${p} </g:link>
				<span class="extra-info">${p.created.getDateString()}</span>
		 		<div class="right">
		 			<ucanask:toggle name="presToggle" active="${p.active}" id="${p.id}" />
	 			</div>
			</div>
		</div>
	</g:each>
</g:if>
</div>
