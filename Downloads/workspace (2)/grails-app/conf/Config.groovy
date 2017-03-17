import org.apache.log4j.RollingFileAppender

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = 'org.ucanask' // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.debug = true

// The default codec used to encode data with ${}
grails.views.default.codec = "html" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
grails.sitemesh.default.layout = 'main'
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
		grails.app.context="/"
    }
	test {
		grails.logging.jul.usebridge = true
		grails.app.context="/"
	}
    production {
        grails.logging.jul.usebridge = false
		grails.app.context="/"
    }
}



log4j = {

	appenders {
		environments {
			production {
				appender new RollingFileAppender(name: "myAppender", maxFileSize: 2048, file: "/tmp/logs/UCanAsk.log")
			}
			development {
				console name:'stdout', layout:pattern(conversionPattern: '%d{yyyyMMdd.HHmmss.SSS} %r [%t] %-5p %c %x - %m%n')
			}
			test {
				console name:'stdout', layout:pattern(conversionPattern: '%d{yyyyMMdd.HHmmss.SSS} %r [%t] %-5p %c %x - %m%n')
			}
		}
	}
	
	environments {
		production {
			warn	'org.mortbay.log',
					'groovyx.net.ws',                    
					'org.apache.cxf.endpoint.dynamic' 
				
		}
	}
	
	error   'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'net.sf.ehcache.hibernate',
			'org.springframework',
			'org.springframework.security',
			'org.hibernate',
			'org.apache',
			'grails.util.GrailsUtil',
			'grails.app.service.NavigationService',
			'org.quartz',
			'net.sf.ehcache',
			'org.codehaus.groovy.grails.plugins.qrcode'

	fatal   'NotificationJob'

	info    'grails.app.BootStrap'

	debug   'grails.app.controller.TroublesomeController'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'org.ucanask.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.ucanask.UserRole'
grails.plugins.springsecurity.authority.className = 'org.ucanask.Role'
grails.plugins.springsecurity.ldap.authorities.defaultRole = 'ROLE_AM'

// Authentication Order
grails.plugins.springsecurity.providerNames = ['ldapAuthProvider','daoAuthenticationProvider']//,'daoAuthenticationProvider','anonymousAuthenticationProvider'] //,'daoAuthenticationProvider',,'rememberMeAuthenticationProvider'] // 'daoAuthenticationProvider']

// Where to go after authenticating
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/room/list'
// These lines stop the flash.message from showing on failure, so not using, seems to work without them.
//grails.plugins.springsecurity.auth.loginFormUrl = '/login/auth'
//grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/login/auth'

// Added for LDAP authentication
grails.plugins.springsecurity.ldap.context.server = 'ldap://ldap.canterbury.ac.nz:389'
grails.plugins.springsecurity.ldap.authenticator.useBind=true
 grails.plugins.springsecurity.ldap.authorities. retrieveDatabaseRoles = true