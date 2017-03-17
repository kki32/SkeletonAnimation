

<%-- Name --%>
<g:form class="offset-1"> 
	<g:hiddenField name="id" value="${roomInstance?.id}" />
	<g:hiddenField name="version" value="${roomInstance?.version}" />
		<label for="name"> <g:message code="default.rooom.name.label"
			default="Room Name" /> <span class="required-indicator">*</span>
		</label>
		<g:textField id="name" placeholder="Course Name" name="name" required="" size="60" value="${roomInstance?.name}" />
</g:form>

<%-- Save Changes --%>
	<div class="grids">
		<div class="grid-16">
       		<span class="button_normal right" id="save">Save changes</span>
       	</div>
	</div>           
<br/>        
        
<%-- Add / Remove Lecturers --%>
<div class="toggleDiv">
	<form class="resetForm qForm">
	<div class="label">
		<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
		<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
		<span class="qLabel bold"><g:message code="default.label.presenters" default="Lecturers" /></span>
	</div>
   
     		<div id="lectureList" class="dropdownDiv">
		<div class="grids listed">
		<g:render template="import_presenters"></g:render>
		</div>
	</div>
	</form>
</div>

<%-- Manage Course Codes --%>
<div class="toggleDiv">
	<form class="resetForm qForm">
	<div class="label">
		<g:img class="imgDown link" file="ui/arrow_dropdown.png"/>
		<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
		<span class="qLabel bold"><g:message code="default.enters" default="Linked Course Codes" /></span>
	</div>
   
	<div id="courses" class="dropdownDiv">
		<div class="grids listed">
		<g:render template="add_courses"></g:render>
		</div>
	</div>
	</form>
</div>	

<%-- Enrolled Students --%>	
<div class="toggleDiv">
	<form class="resetForm qForm">
	<div class="label">
		<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
		<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
		<span id="studentTotal" class="qLabel bold"><%--<g:message code="default.label.presenters" default="Students" />
			 (--%>
			 ${automatic.size()} Automatically Enrolled Students, ${blockedList.size()} Blocked
		</span>
	</div>
   
	<div class="dropdownDiv">
		<div id="studentList" class="grids listed" style="max-height: 200px; overflow: auto; list-style:disc">	
			<g:render template="automatic_enrolled"></g:render>
		</div>
	</div>
	</form>
</div>

<%-- Invited Students --%>	
<div class="toggleDiv">
	<form class="resetForm qForm">
	<div class="label">
		<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
		<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
		<span id="inviteTotal" class="qLabel bold"><%--<g:message code="default.label.presenters" default="Students" />
			 (--%>
			${invited.size()} Invited Students
		</span>
	</div>
   
	<div class="dropdownDiv">
		<div id="inviteList" class="grids listed" style="max-height: 200px; overflow: auto; list-style:disc">
			
			<div class="grid-8">
				<g:form>
					<g:hiddenField name="id" value="${roomInstance?.id}" />
					<label for="textUser">
						<g:message code="default.label.userInvite" default="Invite from Usercode" />:
					</label>
					<g:textField id="inviteUsercode" name="inviteUsercode"/>
					<span id="addInvited" class="button_normal marginBuffer">
						<g:message code="default.button.add.label" default="Add"/>
					</span>
				</g:form>
			</div>
			<div class="right italic" id="inviteError"></div>
			<g:if test="${invited.size() == 0}">
				<div id="emptyInvited" align="center" class="italic">There have currently been no users invited</div>
			</g:if>
			<div class="grid-16">
				<ul id="invitedList">
					<g:each in="${invited}" var="user">
						<li class="listColumn">
						<span class="deleteInvited"><img src="/images/ui/delete_red.png"></span>
							${user.toString()}
						</li>
					</g:each>	
				</ul>
			</div>
		</div>
	</div>
	</form>
</div>

<%-- Reuse Lectures / Presentations --%>
<div class="toggleDiv">
	<form class="resetForm qForm">
	<div class="label">
		<g:img class="imgDown link" file="ui/arrow_dropdown.png" />
		<g:img class="imgUp link" file="ui/arrow_dropup.png" style="display:none;"/>
		<span class="qLabel bold">Reuse My <g:message code="default.label.presentations" default="Presentations" /></span>
	</div>
   
	<div id="lectureList" class="dropdownDiv">
		<div class="grids listed">
		<g:render template="import_presentations"></g:render>
		</div>
	</div>
	</form>
</div>



<g:if test="${request.xhr}">
  <r:layoutResources disposition="defer"/>
</g:if>