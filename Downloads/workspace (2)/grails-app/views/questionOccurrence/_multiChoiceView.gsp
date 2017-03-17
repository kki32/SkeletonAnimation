<%--<r:require modules="chartMC" />--%>
<r:script disposition='defer'>

	require(["views/questionOccurrence/showMc"], function(showMc) {
		showMc.initMCChart("${questionOccurrenceInstance?.id}", ${questionOccurrenceInstance.version}, 'chart_div', "${questionOccurrenceInstance}");
	});
</r:script>


<%--Buttons--%>
<div>
<% chartVisTxt = hide == 'true' ? "Show" : "Hide" %>
	<span class="button_normal" id="chartVisibleBtn" title="<%= chartVisTxt %> this chart"><%= chartVisTxt %></span>
	<span class="button_normal" id="pieBtn" title="Show pie chart"><img src="/images/ui/pie.png" /></span>
	<span class="button_normal" id="columnBtn" title="Show bar chart"><img src="/images/ui/bar.png" /></span>
	<g:hiddenField name="id" value="${questionOccurrenceInstance?.id}" />
	<g:if test="${questionOccurrenceInstance.askedQuestion.answerStrategy.multiselect}">
		<%tooltip = "Toggle between single and grouped choices for this multi-select question"%>
		<input id="groupedOption" name="grouped" type="checkbox" value="Group Multiselect Choices" title="<%=tooltip%>" />
		<label for="groupedOption" title="<%=tooltip%>">Grouped</label>
	</g:if>
	<div class="right occurrenceControl">
		<g:set value="${questionOccurrenceInstance.presentation.open}" var="open"/>
		<div class="guestCode" style="display: <% open ? '' : out<<'none' %>">
			<g:render template="/presentation/qr_code" model="['guestAllowed': open, 'accesskey': questionOccurrenceInstance.presentation.accessKey, 'presentationId': questionOccurrenceInstance.presentation.id]"/>
		</div>
	</div>
	<br/><br/>
</div> 

<%--Title--%>
<g:render template="title" value="${questionOccurrenceInstance.id}" var="questionOccurrenceInstance" />

<%--Where bar/pie chart gets drawn--%>
<div id="chart_div" class="chartDiv" style="width: 100%; height: 450px; text-align:center; margin: 0 auto; overflow:visible; opacity: <%= opacity %>;"></div>