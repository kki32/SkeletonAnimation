<!doctype html>
<html>
	<head>
		<title>Oops</title>
		<meta name="layout" content="main">
	</head>
	<body>
		Oops, something has gone wrong.
		<g:if env="development">
		<g:renderException exception="${exception}" />
		</g:if>
		
	</body>
</html>