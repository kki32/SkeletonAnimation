package org.ucanask.Responses

import grails.test.mixin.*
import org.junit.*
import org.ucanask.TextResource

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin}
 * for usage instructions
 */
@TestFor(MultiChoiceResponse)
class MultiChoiceResponseTests {
	
	void setUp() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
	}

	/**
	 *
	 */
	void testConstraints() {
		mockForConstraintsTests(MultiChoiceResponse)
		def mcr = new MultiChoiceResponse()
		assert !mcr.validate()
		assert "nullable" == mcr.errors["choices"]
		def text = new TextResource(text:"bleugh")
		def mcr2 = new MultiChoiceResponse(choices:[text])
		assert mcr2.validate()
	}
	
	/**
	*
	*/
	void testToString() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
		def text = new TextResource(text:"a")
		def text2 = new TextResource(text:"b")
		def mcr = new MultiChoiceResponse(choices:[text, text2])
		assert mcr.toString() == "a, b"
	}
}
