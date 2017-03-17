
<div align="center">
	<g:form>
		<g:hiddenField name="id" value="${roomInstance?.id}" />
		<label for="textUser">
			<g:message code="default.label.presentationSearch" default="Presentation Name:" />
		</label>
		<g:textField id="presentationName" name="presentationName"/>
		<g:link onClick="findPresentations(${roomInstance?.id})" name="findPresentations" class="button_normal marginBuffer">
			<g:message code="default.button.find.label" default="Find"/>
		</g:link>
		<g:link name="showAll" class="button_normal marginBuffer" value="${message(code: 'default.button.add.prations', default: 'Show All')}">
			<g:message code="default.butd.label" default="Show All"/>
		</g:link>
	</g:form>
</div>


<g:form>	
	<div style="overflow:auto; height:250px;width:100%;">
		<g:hiddenField name="id" value="${roomInstance?.id}" />
		<table class="smallTable">
			<th style="background:silver">
				<g:message code="default.label.add" default="Add" /></th>
			<th style="background:silver">
				<g:message code="default.label.presentationName" default="Presentation Name" /></th>
			<th style="background:silver">
				<g:message code="default.label.room" default="Room" /></th>	
			<th style="background:silver">
				<g:message code="default.label.date.created" default="Creation Date" /></th>	
			<tbody id="presentationList" style="text-align:center">
				<g:each in="${myPresentations}" var="p">
				<tr id="${p.id}">
					<td class="addPresentation"><img src="/images/ui/add.png" /></td>
					<td>${p.toString()}</td>
					<td>${p.room?.toString()}</td>
					<td>${p.created.getDateString()}</td>
				</tr>					
				</g:each>			
			</tbody>
		</table>
	</div>
</g:form>