package org.ucanask.AnswerStrategy

import grails.test.mixin.*
import org.junit.*
import org.ucanask.*
import org.ucanask.Responses.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(MultiChoiceStrategy)
@Mock([TextResource, MultiChoiceResponse, QuestionOccurrence, 
		FreeTextStrategy, TextResource, Question, MultiChoiceStrategy])
class MultiChoiceStrategyTests {
	
	void setUp() {
		mockCodec(org.codehaus.groovy.grails.plugins.codecs.HTMLCodec)
	}

	/**
	 * 
	 */
	void testConstraints() {
		mockForConstraintsTests(MultiChoiceStrategy)
		def multichoice = new MultiChoiceStrategy()
		println multichoice.multiselect
		assert !multichoice.validate()
		assert "nullable" == multichoice.errors["choices"]
		def multichoice2 = new MultiChoiceStrategy(multiselect: true, choices:
				[new TextResource(text:"Yes")])
		assert !multichoice2.validate()
		assert "nullable" != multichoice2.errors["choices"]
		assert "minSize" == multichoice2.errors["choices"]
		def multichoice3 = new MultiChoiceStrategy(multiselect: false, choices:
				[new TextResource(text:"Yes"), new TextResource(text: "No")])
		assert multichoice3.validate()
		assert "nullable" != multichoice3.errors["choices"]
		assert "minSize" != multichoice3.errors["choices"]
	}

	/**
	 * 
	 */
	void testMakeResponse() {
		def res1 = new TextResource(text:"Yes")
		res1.save()
		def res2 = new TextResource(text:"Yes")
		res2.save()
		def mcs = new MultiChoiceStrategy(multiselect: false, choices: [res1, res2])
		assert mcs.makeResponse(null) == null
		assert mcs.makeResponse([random:"what?"]) == null
		assert mcs.makeResponse([choice:[res1.id.toString(), res2.id.toString()]]) == null
		assert mcs.makeResponse([choice:[res1.id.toString()]]) != null
		mcs = new MultiChoiceStrategy(multiselect: true, choices: [res1, res2,
			new TextResource(text: "No")])
		def map = [choice:[res1.id.toString(), res2.id.toString()]]
		def resp = mcs.makeResponse(map)
		assert resp != null
		assert resp.choices[0].id == res1.id
		assert resp.choices.size() == 2
	}
	
	void testValidResponseMapIndividual() {
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
		
		Map map = strategy.responseCollection([occurrence: occurrence, displayType: ""])
		assert 2 == map.size()
		assert 3 == map.get(res1.text)
		assert 2 == map.get(res2.text)
	}
	
	void testInvalidResponseMapIndividual() {
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
		
		def res1 = new TextResource(text:"Yes")
		def res2 = new TextResource(text:"No")
		def mcs = new MultiChoiceStrategy(multiselect: false, choices: [res1, res2])
		Map map = mcs.responseCollection([occurrence: occurrence, displayType: "IndividualChoices"])
		assert 0 == map.size()
	}
	
	void testValidResponseMapMultiselect() {
		def res1 = new TextResource(text:"Yes")
		def res2 = new TextResource(text:"No")
		def res3 = new TextResource(text:"Haha")
		def res4 = new TextResource(text:"Roger")
		def strategy = new MultiChoiceStrategy(multiselect: true, choices: [res1, res2, res3, res4])
		def question = new Question(resource: new TextResource(text: "Here is my question"),
				answerStrategy: strategy)
		def occurrence = new QuestionOccurrence(askedQuestion: question)
		
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res1, res3]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res1, res2, res4]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res2]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res2, res4]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res1, res2]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res1, res2, res3, res4]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res1]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res3, res4]))
		occurrence.addToResponses(new MultiChoiceResponse(choices: [res1, res4]))
		
		Map map = strategy.responseCollection([occurrence: occurrence, displayType: ""])
		assert 4 == map.size()
		assert 6 == map.get(res1.text)
		assert 5 == map.get(res2.text)
		assert 3 == map.get(res3.text)
		assert 5 == map.get(res4.text)
		
		map = strategy.responseCollection([occurrence: occurrence, displayType: "Grouped"])
		assert 8 == map.size()
		assert 1 == map.get(new ResponseCombinations("Yes, Roger"), 1)
		assert 1 == map.get(new ResponseCombinations("Haha, Roger"), 1)
		assert 1 == map.get(new ResponseCombinations("No"), 1)
		assert 1 == map.get(new ResponseCombinations("No, Roger"), 1)
		assert 1 == map.get(new ResponseCombinations("Yes, No, Haha, Roger"), 1)
		assert 1 == map.get(new ResponseCombinations("Yes"), 1)
		assert 1 == map.get(new ResponseCombinations("Yes, No"), 1)
		assert 2 == map.get(new ResponseCombinations("Other"), 2)
	}
}
