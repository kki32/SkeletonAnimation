<!--[if lt IE 9]><r:require module="ieSupport" /><![endif]-->

<%--Buttons--%>
<div id="ftChartButtons">
	<% chartVisTxt = hide == 'true' ? "Show" : "Hide" %>
	<span id="chartVisibleBtn" class="button_normal" title="<%=chartVisTxt%> this chart"><%=chartVisTxt%></span>
	<span id="showRawFTBtn" class="button_normal" title="Show unfiltered responses"><img src="/images/ui/raw_list.png"/></span>
	<span id="showCloudFTBtn" class="button_normal" title="Show basic word cloud"><img src="/images/ui/cloud.png"/></span>
	<span id="showAnimCloudFTBtn" class="button_normal" title="Show animated word cloud"><img src="/images/ui/animated_first.png"/></span>
	<span id="showFilter" class="button_normal" title="Show filtering options"><img src="/images/ui/cog.png">Word Filter</span>
	<g:hiddenField name="id" value="${questionOccurrenceInstance?.id}" />
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

<%--Imports--%>
<r:script disposition='defer'>
	require(["views/questionOccurrence/showFtRaw", "views/questionOccurrence/showFt"], function(showFtRaw, showFt) {
		showFt.questionOccurrenceId = ${questionOccurrenceInstance.id};
		showFt.filters = ${filters};
		showFtRaw.operationWinterWolf(${questionOccurrenceInstance.id});
		showFtRaw.showFilter();
	});   	
</r:script>

<%--Raw list--%>
<g:if test="${displayType.equals("rawFT")}">
	<div class="row chartDiv" id="rawFT" style="padding-bottom:10px; overflow:hidden; opacity: <%= opacity %>;">
		<div class="five columns">
			<ul id='textResponseList'>
			    <g:if test="${!questionOccurrenceInstance.responses}">
			         <li>
			             No responses have been received.
			         </li>
			    </g:if>
				<g:each in="${questionOccurrenceInstance.responses}" status="i" var="response">
					<g:if test="${response.enabled}">
						<li id="responseItem${response.id }">
						   <span class="button_normal removeResponseButton noPadding" id="removeResponse${response.id}"><img class="noPadding" src="/images/ui/delete_white_small.png" alt="Remove Response" /></span>
	                        <r:script disposition='defer'>
	                          require(["views/questionOccurrence/showFtRaw"], function(showFtRaw){
	                              showFtRaw.bindRemoveResponseButton(${questionOccurrenceInstance.id},${response.id});
	                          });
	                        </r:script>
							${response}
						</li>
					</g:if>
				</g:each>
			</ul>
		</div>
	</div>
</g:if>

<%--Animated cloud--%>
<g:elseif test="${displayType.equals("animCloud")}">	
	<div id="animCloudDiv" class="chartDiv" style="padding-bottom:10px; width:800px; height:500px; text-align: center; margin: 0 auto; overflow: hidden; opacity: <%= opacity %>;">
		<canvas id="cloudCanvas" width="800px" height="500px"></canvas>		
		<div id="tags"></div>
	</div>
	<r:script disposition='defer'>
    	require(['views/questionOccurrence/showFt'], function(showFt){
    	showFt.initCloud(${questionOccurrenceInstance.id}, ${questionOccurrenceInstance.version}, "animCloud", ${filters});
    	
    	});
	</r:script>
</g:elseif>

<%--Static cloud--%>
<g:else>	
	<div class="chartDiv" style="padding-bottom:10px; width: 800px; height: 500px; position: relative;  text-align: center; margin: 0 auto; overflow: hidden; opacity: <%= opacity %>;">
		<img id="cloud" src="" style="margin-top: 10px; margin-bottom: 10px"/>
		<r:script disposition='defer'>
		require(['views/questionOccurrence/showFt'], function(showFt){
    		showFt.initCloud(${questionOccurrenceInstance.id}, ${questionOccurrenceInstance.version}, "cloud", ${filters});
    	
    	});
		</r:script>	
	</div>
</g:else>

<%--Word filter--%>
<div id="filter" style="display:none">
	<g:if test="${!displayType.equals("rawFT")}">
		<div id="filter-form">
			<h3>Filters</h3>
			<p>
				Remove specific words by typing them here (space separated):
			</p>
			<input class="filter-list" id='filter-list-text' type="text" value="${questionOccurrenceInstance.getIgnoredWordsList()}"></input>
			<p>
				Toggle known filter lists:
			</p>
			<div class="filter-check-group">
				<input title="Remove all words that are in your filter list" id="myList" class="filter-check" id="owner-filter" type="checkbox" ${questionOccurrenceInstance?.presIgnoreList == true ? 'checked="checked"' : ''}" />
				<label for="myList" title="Remove all words that are in your filter list">My List</label>&nbsp;
				<g:link class="button_normal" title="Edit my filter list" controller="ignoreList" action="edit"><img src="/images/ui/edit.png" /> <g:message code="default.label.ignoreList" default="Edit" /></g:link>
			 	<br />
			 	<input title="Remove all words that are in the global filter list" id="globalList" class="filter-check" id="global-filter" type="checkbox"  ${questionOccurrenceInstance?.globalIgnoreList == true ? 'checked="checked"' : ''}"/>
			  	<label for="globalList" title="Remove all words that are in the global filter list">Global List</label>
		  	</div>
		</div>		
	</g:if>
</div>