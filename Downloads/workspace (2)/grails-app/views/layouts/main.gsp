<!DOCTYPE html>

<html lang="en">


<head>

<!-- Set the viewport width to device width for mobile -->
<meta name="viewport" content="width=device-width" />

<meta charset="UTF-8" />
<meta name="viewport"
	content="width=device-width, minimum-scale=1.0, maximum-scale=1.0" />
<!-- Make IE do what it should -->
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<title><g:layoutTitle default="Grails" /></title>
<link rel="icon" type="image/png" href="/images/favicon.png">
<r:require module="application" />

<r:layoutResources />
<g:layoutHead />
<!--[if lte IE 9]>  
  <link rel="stylesheet" type="text/css" href="/css/ie.css"></style>  
<![endif]--> 
<script src="/js/libs/require/require.js"></script>
<script src="/js/config.js"></script>
</head>
<body>
	<div id="unwrapped" class="hidden"><g:pageProperty name="page.unwrapped" /></div>
	<div id="header">
		<div class="wrapper">
			<div class="grids">
				<div class="grid-16">

					<div class="left" style="clear: both; display:block;">
						<a href="/room/list/">
							<img class="hide-mobile logo" src="/images/logo.png" alt="UCanAsk" />
							<img class="show-mobile logo" src="/images/logo_small.png" alt="UCanAsk" />
						</a>
					</div>
					
					<div class="center-nav">
						<sec:ifAllGranted roles="ROLE_ADMIN">
							<a href="/user/list" class="button_normal" style="text-align: center">
								<g:message code="default.label.editUsers" default="Edit Users" />
							</a>
						</sec:ifAllGranted>
						
						<div class="right">
						
							<sec:ifLoggedIn>
								<g:pageProperty name="page.mail" />
								
								<span class="hide-mobile">Welcome, <ucanask:userDisplayName />&nbsp;</span>
								<span class="button_normal infront" id="button_logout"><a href="j_spring_security_logout">Sign Out</a></span>
							</sec:ifLoggedIn>
						</div>
					</div>
				</div>
			</div>
		</div>

	</div>

	<div id="content" class="wrapper">
		<ucanask:tree>
			<g:pageProperty name="page.tree" />
		</ucanask:tree>
			
		<g:if test="${flash.message}">			
		  	<r:script disposition="defer">	   
		      require(['mediator', 'notificationHandler'], function(mediator) {
	              mediator.publish("notification-alert", {
	                  text: "${flash.message }"                    
	              });
	          });
		   	</r:script>		
		</g:if>
		
		<div class="grids">
			<div class="grid-16">
				<noscript><h3>Javscript is required in order to use this system.</h3></noscript>
				<g:layoutBody />
			</div>
		</div>
	</div>

	<!-- Included JS Files -->
	<r:layoutResources />
</body>
</html>
