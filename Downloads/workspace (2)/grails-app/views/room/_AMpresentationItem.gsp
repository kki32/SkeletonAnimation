
	<g:if test="${p.active}">	
		<div class="grids listed" id="pres${p.id}">
			<div class="grid-14 float-left">
				<div class="bold"><img src="/images/ui/diamond_red.png" style="padding-bottom:2px"/>
					<g:link controller="presentation" action="show" 
							params='[roomId: "${id}"]' id="${p.id}">${p}
					</g:link>
				</div>
			</div>
		</div>				
	</g:if>