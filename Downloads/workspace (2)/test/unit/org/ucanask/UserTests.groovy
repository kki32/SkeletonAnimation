package org.ucanask

import grails.plugins.springsecurity.*
import grails.test.mixin.*

import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} 
 * for usage instructions
 */
@TestFor(User)
class UserTests {

	/**
	 *
	 */
	void testConstraints() {
		mockForConstraintsTests(User)
		def u = new User()
		assert !u.validate()
		assert "nullable" == u.errors["username"]
		assert "nullable" == u.errors["password"]
		assert "nullable" == u.errors["displayName"]

		u = new User(username:"", password:"", displayName: "")
		assert !u.validate()
		assert "blank" == u.errors["username"]
		assert "blank" == u.errors["password"]
		assert "blank" == u.errors["displayName"]

		u = new User(username:"zark", password:"magrathea", displayName:"Slartibartfast",
			email:"orders@customplanets.zurg", enabled:true, accountExpired:false,
			accountLocked:false, passwordExpired:false)
		assert u.validate()
		assert u.save() != null

		def u2 = new User(username:"zark", password:"magrathea", displayName:"Slartibartfast", 
			email:"orders@customplanets.zurg", enabled:true, accountExpired:false, 
			accountLocked:false, passwordExpired:false)
		assert !u2.validate()
		assert "unique" == u2.errors["username"]
	}
}
