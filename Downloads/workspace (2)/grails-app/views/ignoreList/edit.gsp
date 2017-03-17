<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="Ignore List" />
<title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>
	<%-- Breadcrumb trail	--%>
	
	
	<content tag="tree">
            <g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>     
            <g:link controller="ignoreList" action="edit"><g:message code="default.label.ignoreList" default="Ignore List" /></g:link>
        </content>   
	

	<div class="page-controls">
		<img src="/images/courses.png" />
		<h2 style="margin-bottom: 0">Ignore List</h2>
		<p>Add words you want to remove from word clouds for free text
			responses to this list.</p>
	</div>


	<div>
		<div class="grids">
			<div class="grid-16">

				<br />


				<g:render template="form" bean="${ignoreListInstance}"
					var="listInstance" />
				<br />
				<br />
				<g:if test="${adminListInstance }">
					<g:render template="form" bean="${adminListInstance}"
						var="listInstance" />
				</g:if>

			</div>




		</div>

	</div>
	<r:script>
        	require(["views/ignoreList/edit"]);
        </r:script>
</body>
</html>
