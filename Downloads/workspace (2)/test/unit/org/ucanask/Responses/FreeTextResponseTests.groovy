package org.ucanask.Responses



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(FreeTextResponse)
class FreeTextResponseTests {
	
	void setUp() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
	}

	void testConstraints() {
		mockForConstraintsTests(FreeTextResponse)
		def response = new FreeTextResponse()
		assert !response.validate()
		assert "nullable" == response.errors["textResponse"]
		response = new FreeTextResponse(textResponse: "This is very easy")
		assert response.validate()
    }
	
	void testToString() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
		def response = new FreeTextResponse(textResponse: "This is very easy")
		assert "This is very easy" == response.toString()
	}
}
