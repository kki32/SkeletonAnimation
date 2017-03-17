<%@ page import="org.ucanask.Presentation" import="org.ucanask.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'presentation.label', default: 'Presentation')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<%-- Breadcrumb trail	--%>
		<content tag="tree">
			<g:link controller="room" action="list"><g:message code="default.label.rooms" default="Rooms" /></g:link><sep/>
			<g:link controller="room" action="show" id="${presentationInstance.room.id}">${presentationInstance.room.name}</g:link><sep/>
			<g:link controller="presentation" action="show" id="${presentationInstance.id}">${presentationInstance.name}</g:link><sep/>
			<g:link controller="presentation" action="show_am_questions" id="${presentationInstance.id}">Feedback</g:link>
		</content>

		<%--Title--%>
		<div class="page-controls">
			<img src="/images/questions.png"/>
			<h2>${presentationInstance?.name}</h2>
			<div class="right">
				<g:set value="${presentationInstance.open}" var="open"/>
				<div class="guestCode" id="guestCode" style="display: <% open ? '' : out<<'none' %>">
					<h3 style="background-color: #EEEEEE; border-radius: 5px; box-shadow:rgba(0, 0, 0, 0.296875) 0 0 10px"><g:message code="default.presentation.amQuestion.code" default="Access Code"/> <span style="font-weight: bold">${presentationInstance.accessKey}</span></h3>
			 	</div>
			</div>
		</div>
		<br/>
		
		<div class="group">
			<div class="label">
				<span class="bold">
					<g:message code='default.label.amQuestions' code='Questions from Audience Members'/>
					<span class="right">
						<ucanask:toggle class="right" name="feedbackToggle" active="${presentationInstance?.allowUserQuestions}" id="${presentationInstance?.id}" />
					</span>
				</span>
			</div>
			<div>
				<div class="grids listed">
					<div class="grid-16 left">
						<div class="row" id="rawFT">
							<div class="five columns">
								<ul id='questionList'>
									<g:if test="${!presentationInstance.audienceQuestions}">
										<li>
											No Questions have been received.
										</li>
									</g:if>
									<g:each in="${presentationInstance.audienceQuestions}" status="i" var="question">
										<li>
											${question.toString()} -
											<g:if test="${question.anonymous}">
												 <i>Anonymous</i>
											</g:if>
											<g:else>
												<i>${User.get(question.userId).displayName}</i>
											</g:else>
										</li>
									</g:each>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	
	<r:script disposition='defer'>
		require(["views/presentation/amquestions", "views/common/guestcode", "views/common/ui"], function(showam, guestcode, ui) {
	   		showam.manageAudienceQuestions(${presentationInstance?.id});
	   		guestcode.setup(${presentationInstance.id});   		
	    	ui.setupToggle(".feedbackToggle", "/presentation/enable_am_questions/", {});
	    });  	
	</r:script>
	
</html>