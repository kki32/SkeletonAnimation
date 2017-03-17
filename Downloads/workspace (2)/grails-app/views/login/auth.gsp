<html>
<head>
<meta name='layout' content='main' />
<title><g:message code="springSecurity.login.title" /></title>
<style type='text/css' media='screen'>
#login .inner {
	padding: 10px;
	margin: 60px 10px;
	border: 1px solid #aab;
	-moz-box-shadow: 2px 2px 2px #eee;
	-webkit-box-shadow: 2px 2px 2px #eee;
	-khtml-box-shadow: 2px 2px 2px #eee;
	box-shadow: 2px 2px 2px #eee;
}

#login .inner .fheader {
	text-align: left;
	padding: 10px 0;
	font-size: 18px;
	font-weight: bold;
}

#login input {
	width: 100%;
}

@media ( max-width : 720px) {
	#login .inner {
		margin: 10px 10px;
	}
	#login input {
		width: 60%;
	}
}

#login .inner .login_message {
	color: #c33;
}
</style>
</head>

<body>
	<content tag="tree"> <g:link controller="home">UCanAsk</g:link>
	</content>
	<div id='login'>
		<div class="grids">
			<div class="grid-5">
				<br />
			</div>
			<div class="grid-5 inner">
				<form action='${postUrl}' method='POST' id='loginForm'
					class='cssform' autocomplete='off'>

					<div class="grids">
						<div class="grid-14">
							<div class='fheader'>Please Login</div>
						</div>
						<div class="grid-14">
							<label for="Usercode" class="show-on-ie">Username</label> <input
								type='text' name='j_username' id='Usercode'
								placeholder='Username' autocorrect="off" autocapitalize="off" />
							<label for="Password" class="show-on-mobile show-on-ie">Password</label>
							<input type='password' name='j_password' id='Password'
								placeholder='Password' autocorrect="off" autocapitalize="off" />
							<span class="button_normal home" id="login-button">Login</span>
						</div>
					</div>
				</form>
			</div>
			<div class="grid-5">
				<br />
			</div>
		</div>
	</div>
	<script type='text/javascript'>    require(["views/home/index"]);
	</script>
</body>
</html>
