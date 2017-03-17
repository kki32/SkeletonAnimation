package org.ucanask

import grails.plugins.springsecurity.SpringSecurityService
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.core.LdapTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

import org.apache.commons.lang.RandomStringUtils

/**
 * This class handles LDAP logins by checking to see if there is a local user with 
 * the same name as the LDAP one.  If not, it goes about creating one and populating it
 * with details from the LDAP server - and if they do exist then it updates the local
 * users details from LDAP.
 * 
 * @author Steve Dunford - sjd150@uclive.ac.nz
 * 
 */
class PostAuthHandlerService implements AuthenticationSuccessHandler {

	private SpringSecurityService springSecurityService
	def myLdapSearch
	private int passwordLength = 128
	
	private AuthenticationSuccessHandler target = 
			new SavedRequestAwareAuthenticationSuccessHandler();
	private List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]

	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication auth) {
			
		// Force springSecurityService to work - http://burtbeckwith.com/blog/?p=993
		def appContext = new User().domainClass.grailsApplication.mainContext
		this.springSecurityService = appContext.springSecurityService
		
		// Get the username of the login
		def username = auth.principal.getAt("username")
		// Then check to see if they exist in the User database (they may have just auth'd via LDAP)
		def user = User.findByUsername(username)
		// Create a default audience member role in case its needed
		def amRole = Role.findByAuthority('ROLE_AM') ?:
				new Role(authority: 'ROLE_AM').save(failOnError: true)
				println "User Found: $user"

		User.withTransaction { status ->
			// Check to see if this user exists in the local db
			if (!user) { // Local version of the user doesn't exist yet
				// Get or create a password
				def password = (auth.getCredentials()) ?: getPassword(passwordLength)
				// Make a new local user
				user = new User(
					username: username,
					password: password,
					displayName: myLdapSearch.lookup("uid", username, "givenName")[0],
					email: myLdapSearch.lookup("uid", username, "mail")[0],
					enabled: true).save(failOnError: true)
				// And make the user an audience member. For higher privileges, see an admin 
				UserRole.create user, amRole
			}
			else {
				// User exists already, just update their details
				user.displayName = (user.displayName = username)? 
						myLdapSearch.lookup("uid", username, "givenName")[0] ?: "$username" : user.displayName
				user.email = myLdapSearch.lookup("uid", username, "mail")[0] ?: ""
				user.enabled = true // For users who had an account auto-generated in room/edit
				UserRole.create user, amRole  // Just to be sure they have audience rights
				user.merge(flush: true) // Update the user
			}
			springSecurityService.reauthenticate username
			target.onAuthenticationSuccess(request, response, auth)
		}
	}
	
	
	/**
	 * Because users could log in locally, we need some basic security.  Normally the password
	 * a user authenticates to LDAP will be available (auth.getCredentials()), but if its not
	 * then we create a long random one.
	 * 
	 * Note: Users created locally (ie: when the user is added automatically due to a UC course 
	 * being linked to a UCanAsk Room) should be disabled until they log in the first time using LDAP
	 * anyway, but this is just in case.
	 *
	 * @param password (if null or empty returns new password, otherwise just returns itself
	 * @return the password
	 */
	private String getPassword (int length) {
		return RandomStringUtils.randomAlphanumeric(length)
	}
	
	
	public void proceed(HttpServletRequest request,
	HttpServletResponse response, Authentication auth) {
		target.onAuthenticationSuccess(request, response, auth)
	}
}