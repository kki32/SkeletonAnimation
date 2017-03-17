
package org.ucanask

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class HomeController {
	
	/**
	 * Dependency injection for the springSecurityService.
	 */
	def springSecurityService

    def index() {
		

		def config = SpringSecurityUtils.securityConfig

		if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}

		String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
		return [postUrl: postUrl, rememberMeParameter: config.rememberMe.parameter]
	}
}
