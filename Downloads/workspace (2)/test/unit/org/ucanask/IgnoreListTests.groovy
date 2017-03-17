package org.ucanask



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(IgnoreList)
@Mock([User])
class IgnoreListTests {
	
    void testSave() {
		User u = new User(username: "usr12345", password: "password", displayName:"uUuUuUuU", email: "uUuU@uUuU.com").save(flush:true, failOnError: true)
        def ignoredWords = ["hello", "muffins", "burtReynolds"]
		IgnoreList l = new IgnoreList(owner: u, global: false, ignoredWords: ignoredWords).save(flush:true, failOnError: true)
		assert(l != null)
		assert(l.ignoredWords.contains("burtReynolds"))
		assert IgnoreList.findByOwner(u) == l
    }
}
