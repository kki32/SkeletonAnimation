
<div class="grid-8">
	<g:form>
		Add Lecturer With Usercode
		<input id="presenterUsercode" type="text" name="presenter" size="10" placeholder='Usercode' autocorrect="off" autocapitalize="off"/>
		<span class="button_normal" id="presenterAdd" >
			<g:message code="default.button.add.label" default="Add"/>
		</span>
	</g:form>
</div>

<div class="right italic" id="presenterError"></div>

<g:form action="importExistingPresentation">	
	<g:hiddenField name="id" value="${roomInstance?.id}" />
	<table class="smallTable" style="float:left; margin-top: 10px;">
		<th style="background:silver">
			<g:message code="default.label.rom" default="Usercode" />
		</th>	
		<th style="background:silver">
			<g:message code="default.label.presentaonName" default="Full Name" />
		</th>
		<th style="background:silver">
			<g:message code="default.label.user.email" default="Email" />
		</th>			
		<th style="background:silver">
			<g:message code="default.button.delete.label" default="Delete" /></th>
		<tbody id="presenterList" style="text-align:center">
			<g:each in="${owners}" var="presenter">
			<tr>
				<td>${presenter.username}</td>
				<td>${presenter.toString()}</td>
				<td>${presenter.email}</td>
				<td id="${presenter.id}"><img class="deletePresenter" src="/images/ui/delete_black.png" /></</td>
			</tr>					
			</g:each>			
		</tbody>
	</table>
</g:form>