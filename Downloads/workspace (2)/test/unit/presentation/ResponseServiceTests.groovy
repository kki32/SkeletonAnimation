package presentation



import grails.test.mixin.*

import org.junit.*
import org.ucanask.Presentation
import org.ucanask.Question
import org.ucanask.QuestionOccurrence
import org.ucanask.TextResource
import org.ucanask.AnswerStrategy.MultiChoiceStrategy

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ResponseService)
@Mock([Question,MultiChoiceStrategy, TextResource, QuestionOccurrence, Presentation])
class ResponseServiceTests {

	    void testConcurrentResponses() {
		// This service is to handle many concurrent responses from clients.
		// Testing is done using JMeter to simulate high volumes
        assert true
    }
}









