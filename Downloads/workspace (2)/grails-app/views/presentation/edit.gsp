<%@ page import="org.ucanask.Presentation"
		 import="org.ucanask.User" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:set var="entityName" value="${message(code: 'presentation.label', default: 'Presentation')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
    	<%-- Breadcrumb trail	--%>
		<content tag="tree">
			<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
			<g:link controller="room" action="show" id="${presentationInstance.room.id}">${presentationInstance.room.name}</g:link><sep/>
			<g:link controller="presentation" action="show" id="${presentationInstance.id}">${presentationInstance.name}</g:link><sep/>
			<g:link controller="presentation" action="edit" id="${presentationInstance.id}">Edit</g:link>
		</content>
        <div>
            <form method="post" class="resetForm" id="presName">
                <g:hiddenField name="id" value="${presentationInstance?.id}" />
                <g:hiddenField name="version" value="${presentationInstance?.version}" />
                <g:render template="form"/>
            </form>
            <br />
            <div class="grids">
            	<div class="grid-16">
	            	<span class="button_normal" id="create">Create question</span>
	            	<a href="/presentation/add_questions/${presentationInstance?.id}" class="button_normal" id="addExisting">Add existing</a>
	            	<span class="button_normal right" id="saveAll">Save changes</span>
	            </div>
            </div>
            <br />
            <div id="questionList">
				<g:render template="questionEdit" collection="${presentationInstance?.questions}" var="qo"/>
			</div>
        </div>
        <r:script>
        	require(["views/presentation/edit"], function(createQuestion){
    			createQuestion.presid = ${presentationInstance?.id};
		    });    
        </r:script>
    </body>
</html>
