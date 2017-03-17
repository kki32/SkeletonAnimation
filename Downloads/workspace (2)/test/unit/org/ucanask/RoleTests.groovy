package org.ucanask



import grails.test.mixin.*
import org.junit.*
import org.ucanask.AnswerStrategy.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Role)
class RoleTests {
	
	void testConstraints() {
		def existingRole = new Role(authority:"Presenter")
		
		mockForConstraintsTests(Role, [existingRole])
		
		// Empty role is not valid, testing blank constraint
		def role = new Role()
		assert false == role.validate()
		assertEquals "nullable", role.errors["authority"]
		
		// Testing unique constraint
		role = new Role(authority:"Presenter")
		assert false == role.validate()
		
		// Should be valid
		role = new Role(authority: "audience member")
		assertTrue role.validate()	
	}	
}
