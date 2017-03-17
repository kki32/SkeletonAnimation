package org.ucanask.springSecurityTests

import org.ucanask.Role
import org.ucanask.User
import org.ucanask.UserRole

abstract class SpringSecurityTestSetup {
	
	def springSecurityService
	User audience, presenter, administrator
	Role amRole, prRole, adminRole
	
	protected void setUp() {
		def numberOfUsers = User.list().size()
		// Create the roles used in UCanAsk
		amRole    = Role.findByAuthority('ROLE_AM') ?:
			new Role(authority: 'ROLE_AM').save(failOnError: true)
		prRole    = Role.findByAuthority('ROLE_PR') ?:
			new Role(authority: 'ROLE_PR').save(failOnError: true)
		adminRole = Role.findByAuthority('ROLE_ADMIN') ?:
			new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

		// Create 3 test users, one for each role
		administrator = new User(
			username:		'administrator',
			displayName:	'Administrator',
			email:			'administrator@ucanask.canterbury.ac.nz',
			password:		'password',
			enabled:		true,
			hideMyDetails:	true
				).save(failOnError: true)
			UserRole.create administrator, adminRole
	
		presenter = new User(
			username:		'presenter',
			displayName:	'Presenter',
			email:			'presenter@ucanask.canterbury.ac.nz',
			password:		'password',
			enabled:		true,
			hideMyDetails:	true
				).save(failOnError: true)
			UserRole.create presenter, prRole
	
		audience = new User(
			username:		'audience',
			displayName:	'Audience',
			email:			'audience@ucanask.canterbury.ac.nz',
			password:		'password',
			enabled:		true,
			hideMyDetails:	true
				).save(failOnError: true)
			UserRole.create audience, amRole
			
		// Check 3 users were created
		assert User.list().size() == (numberOfUsers + 3)
		
	
	}
	
	protected void tearDown() {
		audience = presenter = admin = null
		amRole = prRole = adminRole = null
	}
}
