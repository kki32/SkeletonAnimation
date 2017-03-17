package org.ucanask.AnswerStrategy

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import org.ucanask.Responses.*
import org.ucanask.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(FreeTextStrategy)
@Mock([FreeTextStrategy, FreeTextResponse, QuestionOccurrence,
		Question, TextResource, MultiChoiceStrategy])
class FreeTextStrategyTests {

	void setUp() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
	}
	
	void testConstraints() {
		mockForConstraintsTests(FreeTextStrategy)
		def strategy = new FreeTextStrategy()
		assert strategy.validate()
	}
	
	void testMakeResponse() {
		mockForConstraintsTests(FreeTextStrategy)
		def strategy = new FreeTextStrategy()
		def map = [text: "Here is my response"]
		FreeTextResponse response = strategy.makeResponse(map)
		assert "Here is my response" == response.textResponse
	}
	
	void testValidResponseMap() {
		def strategy = new FreeTextStrategy();
		def question = new Question(resource: new TextResource(text: "Here is my question"),
				answerStrategy: strategy)	
		def occurrence = new QuestionOccurrence(askedQuestion: question)
		occurrence.addToResponses(new FreeTextResponse(textResponse: "This is very easy"))
		occurrence.addToResponses(new FreeTextResponse(textResponse: "Another response"))
		occurrence.addToResponses(new FreeTextResponse(textResponse: "How boring"))
		occurrence.addToResponses(new FreeTextResponse(textResponse: "Beer time yet?"))
		occurrence.addToResponses(new FreeTextResponse(textResponse: "This is going to work first time"))
		occurrence.addToResponses(new FreeTextResponse(textResponse: "See Steve..."))
		def collection = strategy.responseCollection([occurrence: occurrence, displayType: "rawFT"])
		
		assert 6 == collection.size()
		assert true == collection.contains("This is very easy") 
		assert true == collection.contains("Another response") 
		assert true == collection.contains("How boring") 
		assert true == collection.contains("Beer time yet?") 
		assert true == collection.contains("This is going to work first time") 
		assert true == collection.contains("See Steve...") 
	}
	
	void testInvalidResponseMap() {
		
		def res1 = new TextResource(text:"Yes")
		def res2 = new TextResource(text:"No")
		def strategy = new MultiChoiceStrategy(multiselect: false, choices: [res1, res2])
		def question = new Question(resource: new TextResource(text: "Here is my question"),
				answerStrategy: strategy)	
		def occurrence = new QuestionOccurrence(askedQuestion: question)
		
		occurrence.addToResponses(new MultiChoiceResponse(choices: res1))
		occurrence.addToResponses(new MultiChoiceResponse(choices: res1))
		occurrence.addToResponses(new MultiChoiceResponse(choices: res2))
		occurrence.addToResponses(new MultiChoiceResponse(choices: res2))
		occurrence.addToResponses(new MultiChoiceResponse(choices: res1))
		
		def collection = strategy.responseCollection(occurrence: occurrence, displayType: "rawFT")
		assert 0 == collection.size()
	}
}
