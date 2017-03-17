<html>
<head>

<title><g:message code="springSecurity.login.title" /></title>

</head>

<body>
	<content tag="tree"> <g:link controller="home">UCanAsk</g:link>
	</content>
	<br />
	<div id="homeDiv" class="grids">
		<div class="grid-7">
			<form action='${postUrl}' method='POST' id='loginForm'>
				
				<div class="grids">
					<div class="grid-10">
						<label for="Usercode" class="show-on-ie">Username</label>
						<input class='home' type='text' name='j_username' id='Usercode' placeholder='Username' autocapitalize="off" />
						<label for="Password" class="show-on-mobile show-on-ie">Password</label>
						<input class='home' type='password' name='j_password' id='Password' placeholder='Password'
							autocorrect="off" autocapitalize="off" />
						<span class="button_normal home" id="login-button">Login</span>
					</div>
					
				</div>
				
			</form>
			<br />
			<hr>

				
			<div class="grids">
				<form action="${createLink(controller: "room", action: "goToPresentation")}" id="go-to-presentation-form" class="qForm">
					<div class="grid-10">
						<label for="PresentationCode" class="show-on-mobile show-on-ie"><g:message code="default.label.enterPresentationCode" default="Presentation Code" /></label>
						<g:textField class='home' id="PresentationCode" name="presentationCode" required="" placeholder="Enter Lecture Code" />
						<span class="button_normal home" id='join-button'>Join</span>
					</div>
				</form>
			</div>
			
		</div>
		<div id="divider" class="grid-1 hide-mobile">
		</div>
		<div class="grid-8 hide-mobile">
			<div class="grids">
				<div class="grid-16">
					<h2>Welcome</h2>
					<p>Login to see your courses, or enter the room code provided
						by your lecturer to get in on the action!</p>
				</div>
			</div>
		</div>
	</div>
	<script type='text/javascript'>
        require(["views/home/index"]);
    </script>
</body>
</html>
