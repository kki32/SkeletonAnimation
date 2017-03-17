
<div class="grid-8">
	<g:form>
		Course Codes to Link With
		<g:set var="courses" value="${allCourses}" />
		<%--<select>
			<g:each in="${courses}" >
				<option value="${it }">${it }</option>
			</g:each>
		</select>
		--%>
		<span id="courseCodeWrapper" class="ui-widget">
		<input id="courseCode" class="ui-autocomplete-input" type="text" name="presenter" size="10" placeholder='Course code' autocorrect="off" autocapitalize="off"/>
		</span>
		
		<span class="button_normal" id="courseAdd" >
			<g:message code="default.button.add.label" default="Add"/>
		</span>
		
	</g:form>
</div>

<div class="right italic" id="courseError"></div>

<g:form>	
	<g:hiddenField name="id" value="${roomInstance?.id}" />
	<table class="smallTable" style="float:left; margin-top: 10px;">
		<th style="background:silver">
			<g:message code="default.label.rom" default="Course Code" />
		</th>	
		<th style="background:silver">
			<g:message code="default.button.delete.label" default="Delete" /></th>
		<tbody id="courseList" style="text-align:center">
			<g:each in="${roomInstance.courses}" var="course">
			<tr>
				<td>${course}</td>
				<td id="${course}"><img class="deleteCourse" src="/images/ui/delete_black.png" /></</td>
			</tr>					
			</g:each>			
		</tbody>
	</table>
</g:form>