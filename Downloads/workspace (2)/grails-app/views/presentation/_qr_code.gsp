<div class="guestCode" id="guestCode" style="padding-top: 10px; margin-right: 0px; display:<% guestAllowed ? '' : out<<'none' %>">
		<h3 style="background-color: #EEEEEE; border-radius: 5px; box-shadow:rgba(0, 0, 0, 0.296875) 0 0 4px"><g:message code="default.presentation.amQuestion.code" default="Access Code"/> <span style="font-weight: bold">${accesskey}</span>
		<span id="qrCode" class="clickable"><img src="\images\qrcode_icon.png" /></span></h3>
</div>
<r:script disposition="defer">
	require(["views/presentation/qrCode"], function(qrCode) {
		qrCode.accessKey = ${accesskey || 'null'};
		qrCode.presentationId = ${presentationId || 'null'};
		qrCode.setup();
	})
</r:script>