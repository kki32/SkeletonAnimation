package org.ucanask

import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.*

import org.junit.*
import org.ucanask.AnswerStrategy.*

/**
 *
 */
@TestFor(QuestionController)
@Mock([SpringSecurityService, Question, Room, Role, User, UserRole, MultiChoiceStrategy, FreeTextStrategy, TextResource, Presentation, QuestionOccurrence])
class QuestionControllerTests {

	
	Room r
	Presentation p
	User admin
	User am
	
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
//		ResponseService.metaClass.addResponse { -> return true}
	}
	
	
	/**
	 *
	 */
	def populateValidMultichoiceParams(params) {
		assert params != null
		params["questionText"] = "I am a question"
		params["questionType"] = "Multi-Choice"
		params["multiselect"] = "true"
		params["choice"] = ["choice1", "choice2"]
	}


	/**
	 *
	 */
	def populateValidFreetextParams(params) {
		assert params != null
		params["questionText"] = "I am a question"
		params["questionType"] = "FreeText"
	}

	/**
	 *
	 */
	def populateInvalidParams(params) {
		assert params != null
		params["questionText"] = "I am a question"
		params["questionType"] = "Multi-Choice"
		params["choice"] = []
	}

	/**
	 *
	 */
	def createValidMultichoiceQuestion() {
		def choices = [
			new TextResource(text:"choice1"),
			new TextResource(text: "choice2")
		]
		def resources = [
			new TextResource(text:"I am a question")
		]
		def multiChoiceStrategy =  new MultiChoiceStrategy(multiselect: false, choices: choices)
		return new Question(answerStrategy: multiChoiceStrategy,  resources: resources, owner: admin, room: r)
	}

	/**
	 *
	 */
	def createValidFreetextQuestion() {
		def resources = [
			new TextResource(text:"I am a question")
		]
		def freetextStrategy =  new FreeTextStrategy()
		return new Question(answerStrategy: freetextStrategy, resources: resources, owner: admin, room: r)
	}

	/**
	 *		Need to be changed to test save_JSON and update_JSON as this is the only actions in question controller ******************** 
	 */
	void testSaveMultichoiceQuestion() {		
		params["presId"] = p.id
		controller.save()
		response.reset()
		request.method = 'POST'
		controller.save()
		assert model.questionInstance != null
		assert view == '/question/create'
		response.reset()
		populateInvalidParams(params)
		controller.save()
		assert model.questionInstance != null
		assert response.redirectedUrl == null
		assert Question.count() == 0
		response.reset()
		populateValidMultichoiceParams(params)
		controller.save()
		assert response.redirectedUrl == '/presentation/show/1?roomId=1'
		assert controller.flash.message != null
		assert Question.count() == 1
		assert Question.get(1).answerStrategy.multiselect == true
	}

	/**
	 * 
	 */ 
	void testSaveFreetextQuestion() {
		params.presId = p.id
		controller.save()
		response.reset()
		request.method = 'POST'
		controller.save()
		assert model.questionInstance != null
		assert view == '/question/create'
		response.reset()
		populateInvalidParams(params)
		controller.save()
		assert model.questionInstance != null
		assert response.redirectedUrl == null
		assert Question.count() == 0
		response.reset()
		populateValidFreetextParams(params)
		controller.save()
		assert response.redirectedUrl == '/presentation/show/1?roomId=1'
		assert controller.flash.message != null
		assert Question.count() == 1
	}


	/**
	 *
	 */
	void testUpdateMultichoiceQuestion() {
		controller.update()
		response.reset()
		request.method = 'POST'
		controller.update()
		assert flash.message != null
		assert response.redirectedUrl == '/question/list'
		response.reset()
		def question = createValidMultichoiceQuestion()
		assert question.save(failOnError:true) != null
		// Test invalid parameters in update
		params.id = question.id
		populateInvalidParams(params)
		controller.update()
		assert view == "/question/edit"
		assert model.questionInstance != null
		question.clearErrors()
		populateValidMultichoiceParams(params)
		controller.update()
		assert response.redirectedUrl == "/question/show/$question.id"
		assert flash.message != null
		// Test outdated version number
		response.reset()
		question.clearErrors()
		populateValidMultichoiceParams(params)
		params.id = question.id
		params.version = -1
		controller.update()
		assert view == "/question/edit"
		assert model.questionInstance != null
		assert model.questionInstance.errors.getFieldError('version')
		assert flash.message != null
		// Test not update a locked question
		def question2 = createValidMultichoiceQuestion()
		populateValidMultichoiceParams(params)
		question2.qLocked = true
		params.id = question2.id
		controller.update()
		assert flash.message != null
		assert response.redirectedUrl == '/question/list'
	}

	/**
	 *
	 */
	void testUpdateFreetextQuestion() {
		controller.update()
		response.reset()
		request.method = 'POST'
		controller.update()
		assert flash.message != null
		assert response.redirectedUrl == '/question/list'
		response.reset()
		def question = createValidFreetextQuestion()
		assert question.save(failOnError:true) != null
		// Test invalid parameters in update
		params.id = question.id
		populateInvalidParams(params)
		controller.update()
		assert view == "/question/edit"
		assert model.questionInstance != null
		question.clearErrors()
		populateValidFreetextParams(params)
		controller.update()
		assert response.redirectedUrl == "/question/show/$question.id"
		assert flash.message != null
		// Test outdated version number
		response.reset()
		question.clearErrors()
		populateValidFreetextParams(params)
		params.id = question.id
		params.version = -1
		controller.update()
		assert view == "/question/edit"
		assert model.questionInstance != null
		assert model.questionInstance.errors.getFieldError('version')
		assert flash.message != null
	}

	
	void tearDown() {
		SpringSecurityService.metaClass = null
//		ResponseService.metaClass = null
	}
}
