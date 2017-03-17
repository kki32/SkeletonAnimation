package org.ucanask



import grails.converters.*
import grails.test.mixin.*

import java.util.HashMap

import org.junit.*
import org.ucanask.AnswerStrategy.*
import org.ucanask.Responses.*

import grails.plugins.springsecurity.SpringSecurityService


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(QuestionOccurrenceController)
@Mock([SpringSecurityService, Question, Room, Role, User, UserRole, MultiChoiceStrategy, TextResource, QuestionOccurrence, Presentation, MultiChoiceResponse, FreeTextStrategy, FreeTextResponse])
class QuestionOccurrenceControllerTests {
	
	TextResource c1 = new TextResource(text:"choice1")
	TextResource c2 = new TextResource(text:"choice2")
		
	Room r
	Presentation p
	User admin
	User am
	
	/**
	 * 
	 */
	void setUp() {
		admin = new User(username: "usr12345", password: "password", displayName:"presenter1", email: "ab2@xyq.com").save(failOnError:true)
		assert admin != null
		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?:new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
		UserRole.create admin, adminRole
		
		am = new User(username: "am123", password: "password", displayName:"am1", email: "ab1@xyq.com").save(failOnError:true)
		assert am != null
		def amRole = Role.findByAuthority('ROLE_AM') ?:new Role(authority: 'ROLE_AM').save(failOnError: true)
		UserRole.create am, amRole
		
		r = new Room(name: "roomz", owners: [admin], invited:[], open:true).save(failOnError:true)
		assert r != null
		
		p = new Presentation(name: "Presentation1", active: true, room: r, owner: admin).save(failOnError:true)
		assert p.save(failOnError:true) != null
		
		SpringSecurityService.metaClass.getCurrentUser { -> return admin}
		//ResponseService.metaClass.addResponse { -> return true}
	}
	
	/**
	 * 
	 */
	def createValidQuestion() {
		def choices = [c1, c2]
		def resources = [new TextResource(text:"I am a question")]
		return new Question(answerStrategy:
			 new MultiChoiceStrategy(multiselect: true, choices: choices), resources: resources, room: r, owner: admin)
	}
	
	/**
	 * Check the version number increases by 1 only when new responses are received
	 */
	void testVersionUpdates() {	
		def q = createValidQuestion()
		assert q.save(failOnError:true) != null
		    
		QuestionOccurrence qo = new QuestionOccurrence(askedQuestion: q, active: true, presentation: p)
		assert qo.save(failOnError:true) != null
	
		params["id"] = qo.id
		controller.getVersion()
		assert response.contentAsString == "0"
		
		params["choice"] = ["choice1"]
		qo.addResponse(params)
		response.reset()
		controller.getVersion()
		assert response.contentAsString == "1"
				
		response.reset()
		controller.getVersion()
		assert response.contentAsString == "1"
	}
	
	/**
	 * Check that new responses are made available for graphing
	 */
	void testAddValidResponse() {
		def q = createValidQuestion()
		assert q.save(failOnError:true) != null
		
		QuestionOccurrence qo = new QuestionOccurrence(askedQuestion: q, active: true, presentation: p)
		assert qo.save(failOnError:true) != null
		params["id"] = qo.id
		params["displayType"] = ""
		
		params["choice"] = [c1.id.toString()]
		qo.addResponse(params)
		params["choice"] = [c1.id.toString(),c2.id.toString()]
		qo.addResponse(params)
		
		controller.getResponsesJSON()
		assert controller.response.json == [choice2:1, choice1:2]
	}
	
	/**
	 * Check that after adding an invalid choice for a MC question
	 * the valid values are not affected
	 */
	void testAddInvalidMCResponse() {
		def q = createValidQuestion()
		assert q.save(flush: true, failOnError:true) != null
		
		QuestionOccurrence qo = new QuestionOccurrence(askedQuestion: q, active: true, presentation: p)
		assert qo.save(flush: true, failOnError:true) != null
		params["id"] = qo.id
		
		params["choice"] = ["Swimming is good for you, especially if you are drowning"]
		qo.addResponse(params)
		params["choice"] = [c1.id.toString(),"I'm a crazy choice!"]
		qo.addResponse(params)
		
		params["displayType"] = ""
		controller.getResponsesJSON()
		assert controller.response.json == [choice1:1]
	}
	
	/**
	 * Tests that getResponseStringsJSON returns an empty list
	 */
	void testGetResponseStringsJSONEmpty(){
		def resource = [new TextResource(text:"I am a question")]
		def q = new Question(answerStrategy: new FreeTextStrategy(), resources: resource, owner: admin, room: r)
		
		assert q.save(failOnError:true) != null
		
		QuestionOccurrence qo = new QuestionOccurrence(askedQuestion: q, active: true, presentation: p)
		assert qo.save(failOnError:true) != null
		params["id"] = qo.id
		controller.getResponsesJSON()
		
		assert controller.response.json == []
		
		

	}
	
	/**
	* Tests that getResponsesJSON returns a list of responses
	*/
	void testGetResponsesJSONNotEmpty(){
		
		def resource = [new TextResource(text:"I am a question")]
		def q = new Question(answerStrategy: new FreeTextStrategy(), resources: resource, room: r, owner: admin)
		
		assert q.save(failOnError:true) != null
		
		QuestionOccurrence qo = new QuestionOccurrence(askedQuestion: q, active: true, presentation: p)
		assert qo.save(failOnError:true) != null
		params["id"] = qo.id
		
		qo.addToResponses(new FreeTextResponse(textResponse: 'a response'));
		qo.save(failOnError:true)
		qo.addToResponses(new FreeTextResponse(textResponse: 'asdg'));
		qo.save(failOnError:true)
		
		params["displayType"] = "rawFT"
		controller.getResponsesJSON()
		
		
		assert controller.response.json != []
		assert controller.response.json == ['a response','asdg']
	}
	
}




