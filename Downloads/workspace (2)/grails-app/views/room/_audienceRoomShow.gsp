<div class="label bold"><g:message code="default.label.presentations" default="Presentations" /></div>
<r:script disposition="defer">
    require(["views/room/show"], function(show){
    	show.operationSneakyMonkey(${roomInstance.id});
    });
</r:script>
<div id="presList"> <!-- Used for listed formatting -->
<g:each var="p" in="${roomInstance.presentations}">
	<g:render template="AMpresentationItem" model="['roomInstance':roomInstance.id, 'p':p]" />
</g:each>
</div>
<r:script disposition="defer"></r:script>