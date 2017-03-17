define(['jquery'], function(jquery){
	var qrCode = {
			accessKey: null,
			presentationId: null
	};
	qrCode.setup = function() {
		if (qrCode.accessKey) {
			var isShowingModal = false;
			function setModalCss(){
				var width = $(window).width();
				var height = $(window).height();
				var style = "";
				if(height > width) {
					style = "style='height:"+width+"px'";
				} else {
					style = "style='height:"+height+"px'";
				}
				$("#unwrapped").html("<img " + style + " src='/presentation/qrCode?id=" + qrCode.presentationId + "&presentationCode="+ qrCode.accessKey +"' />");
				
				
				$("#unwrapped").css({
					'z-index': 2000,
					position: "absolute",
					"text-align": "center",
					top: 0,
					left: 0,
					width: "100%",
					"background-color": "rgba(255, 255, 255, 0.7)",
					height: $(document).height()		
				});
			}
			function modal(){
				if(!isShowingModal ){
					isShowingModal = true;
					setModalCss();
					$("#unwrapped").fadeIn();
					
				}
			}
			function hideModal(){
				isShowingModal = false;
				$("#unwrapped").hide();
			};
			$(window).on("keydown", function(){
				if(isShowingModal){
					hideModal();
				}
			});
			$(window).resize(function(){
				if(isShowingModal){
					setModalCss();
				}
			});
			$("#qrCode").on("click", function(){
				modal();
			});
			$("#unwrapped").on("click", function(){
				hideModal();
			});			
		}	
	}
	return qrCode;
});