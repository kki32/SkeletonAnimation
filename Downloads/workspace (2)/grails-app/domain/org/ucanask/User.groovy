package org.ucanask


class User {

	transient springSecurityService

	String username
	String password
	String displayName
	String email
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static constraints = {
		username blank: false, unique: true
		password blank: false
		displayName blank: false
		email: email: true
	}

	static mapping = { 
		table 'users' // for postgresql compatibility
		password column: '`password`' 
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		if (springSecurityService) {
			password = springSecurityService.encodePassword(password)
		}
	}
	
	String toString() {
		return displayName
	}
}

