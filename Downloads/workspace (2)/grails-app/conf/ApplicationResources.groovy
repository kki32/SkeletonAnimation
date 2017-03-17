modules = {
	application {		
		resource url: 'js/siteScripts.js', disposition: 'defer'
		resource url: 'css/inuit.css'
		resource url: 'css/grid.inuit.css'
		resource url: 'css/main.css'
		resource url: 'css/button_style.css'
		resource url: 'css/autocomplete.css'
	}
	
//	chartUtils {
//		dependsOn 'jquery'
//		resource url: 'js/charts/chartUtils.js'
//	}
//	chartMC {
//		dependsOn 'jquery'
//		resource url: 'js/charts/chartMC.js', disposition: 'defer'
//		resource url: 'js/charts/highcharts.js'
//		resource url: 'js/charts/exporting.js'		
//	}
//	chartFT {
//		dependsOn 'jquery'
//		resource url: 'js/charts/tagcanvas.js', disposition: 'defer'		
//		resource url: 'js/charts/chartFT.js', disposition: 'defer'	
//	}
	ieSupport {		
		resource url: 'js/html5shiv.js', disposition: 'head'
		resource url: 'js/excanvas.js', disposition: 'defer'
	}

}
