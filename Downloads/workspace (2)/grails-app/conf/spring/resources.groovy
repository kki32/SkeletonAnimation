import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder


// Place your Spring DSL code here
beans = {

	// LDAP base config for spring security
	contextSource(org.springframework.ldap.core.support.LdapContextSource) {
		url = "ldap://ldap.canterbury.ac.nz:389"
		userDn = "cn=reader, dc=canterbury,dc=ac,dc=nz"
		password = "boxer"
		base = "ou=useraccounts,dc=canterbury,dc=ac,dc=nz"
	}

	// LDAP search template
	ldapTemplate(org.springframework.ldap.core.LdapTemplate, ref(contextSource))

	ldapSearch(org.ucanask.LdapSearchService) {
		myLdapTemplate=ref(ldapTemplate)
	}
	
	
	authenticationSuccessHandler(org.ucanask.PostAuthHandlerService) {
		myLdapSearch=ref(ldapSearch)
	}

	// THANKS: http://jamesjefferies.com/2011/01/06/grails-spring-security-ldap/
	// (NO THANKS: Grails and Spring for crap documentation.)
	
	// this gives the pattern for authenticating the user, as the user doesn't want to enter the 
	// whole thing, just their unique id
	myLdapAuthenticator(org.springframework.security.ldap.authentication.BindAuthenticator, 
			ref("contextSource")) {
		userDnPatterns = ['uid={0}']
	}

	// this overrides the default Authentication Provider with our authenticator and our user
	// details service
	ldapAuthProvider(org.springframework.security.ldap.authentication.LdapAuthenticationProvider,
			ref("myLdapAuthenticator"))
	
	corpusService(org.ucanask.chart.CorpusService){		
		try {
			corpus = org.ucanask.chart.Corpus.deserialize(new File("webapps/ROOT/corpus/corpus.dat"));   //TODO hardcoded to make it fucking work with tomcat
		} catch (Exception e) {
			corpus = org.ucanask.chart.Corpus.deserialize(new File("web-app/corpus/corpus.dat"));
		}
	}
}


