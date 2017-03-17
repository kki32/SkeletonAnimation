package org.ucanask

import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} 
 * for usage instructions
 */
@TestFor(TextResource)
class TextResourceTests {

	/**
	 *
	 */
	void testConstraints() {
		mockForConstraintsTests(TextResource)
		def txt = new TextResource()
		assert !txt.validate()
		assert "nullable" == txt.errors["text"]
		txt = new TextResource(text:"")
		assert !txt.validate()
		assert "blank" == txt.errors["text"]
		txt = new TextResource(text:"Example text")
		assert txt.validate()
	}
}
