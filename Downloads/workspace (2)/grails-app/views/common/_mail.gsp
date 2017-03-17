<content tag="mail">
	<span name="showam" id="mailButton" class="clickable${presentationInstance?.audienceQuestions?.size() < 0 ? ' hidden': ''} ">
		
		<span title="You've Got Mail!" style="position: relative"><img src="\images\mail.png" class="extra-info"/></span>
		<span id="feedbackCount" title="You've Got Mail!" style="position: relative; top: -5px; left: -30px; color: black; font-weight: bold; font-size: 80%;">
			${presentationInstance?.audienceQuestions?.size()}
		</span>
	</span>
	<r:script disposition='defer'>
		require(['views/presentation/mail'], function(mail){
			mail.presentationId = ${presentationInstance.id};
			mail.setup();
		});
	</r:script>
</content>