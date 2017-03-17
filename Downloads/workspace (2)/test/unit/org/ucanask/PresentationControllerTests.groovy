package org.ucanask

import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.*

import org.ucanask.AnswerStrategy.MultiChoiceStrategy
import org.ucanask.Responses.MultiChoiceResponse

import presentation.ResponseService

/**
 *
 */
@TestFor(PresentationController)
@Mock([ResponseService, SpringSecurityService, Presentation, User, UserRole, Role, Room, Question, TextResource, MultiChoiceStrategy, QuestionOccurrence, MultiChoiceResponse])
class PresentationControllerTests {

	Room r
	User admin
	User am	
	
	void setUp() {
		admin = new User(username: "usr12345", password: "password", displayName:"presenter1", email: "ab2@xyq.com").save(flush: true, failOnError:true)
		assert admin != null
		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?:new Role(authority: 'ROLE_ADMIN').save(flush: true, failOnError: true)
		UserRole.create admin, adminRole
		
		am = new User(username: "am123", password: "password", displayName:"am1", email: "ab1@xyq.com").save(flush: true, failOnError:true)
		assert am != null
		def amRole = Role.findByAuthority('ROLE_AM') ?:new Role(authority: 'ROLE_AM').save(flush: true, failOnError: true)
		UserRole.create am, amRole
		
		r = new Room(name: "roomz", owners: [admin], invited:[am]).save(flush: true, failOnError:true)

		SpringSecurityService.metaClass.getCurrentUser { -> return admin}
		ResponseService.metaClass.addResponse { -> return true}
	}
			
	/**
	 *
	 */
	def populateValidParams(params) {
		assert params != null
		params["name"] = 'presentationName'
		params["active"] = 'false'
		params["roomId"] = r.id
	}
	
	/**
	 *
	 */
	void testIndex() {
		controller.index()
		assert "/room/list" == response.redirectedUrl
	}

	/**
	 *
	 */
	void testCreate() {
		params["roomId"] = r.id		
		def model = controller.create()	
		assert model != null && model.presentationInstance != null
	}

	/**
	 *
	 */
	void testSave() {
		populateValidParams(params)
		controller.save()
		assert response.redirectedUrl.contains('/presentation/edit/1')
		//assert controller.flash.message != null
		assert Presentation.count() == 1
	}
	
	/**
	 * Check that a non-authorized user (eg guest, AM) cannot save a presentation
	 */
	void testUnauthorizedSave() {
		populateValidParams(params)
		SpringSecurityService.metaClass.getCurrentUser { -> return am}
		controller.save()
		assert response.redirectedUrl == '/room/show/1'  // should redirect back
		assert Presentation.count() == 0  // without creating the presentation
		SpringSecurityService.metaClass.getCurrentUser { -> return admin}  // reset meta-overwrite
	}

	/**
	 * Cannot currently delete a presentation
	 */
	void testDelete() {
		//assert false
//		controller.delete()
//		assert flash.message != null
//		assert response.redirectedUrl == '/presentation/list'
//		response.reset()
//		populateValidParams(params)
//		def presentation = new Presentation(params)
//		assert presentation.save() != null
//		assert Presentation.count() == 1
//		params.id = presentation.id
//		controller.delete()
//		assert Presentation.count() == 0
//		assert Presentation.get(presentation.id) == null
//		assert response.redirectedUrl == '/presentation/list'
	}

	/**
	 *
	 */
	void testAddQuestions() {
		def presentation = new Presentation(name: "presentation", owner: admin, room: r).save(flush: true, failOnError:true)
		params["id"] = presentation.id
		def model = controller.add_questions()
		assert model.presentationInstance != null
		assert model.allQuestions.size() == 0 // != null
	}

//	/**
//	 *
//	 */
//	void testAsk() {
//		 No presentation or question id
//		controller.ask()
//		assert flash.message != null
//		assert response.redirectedUrl == '/presentation/list'
//		// No question id
//		response.reset()
//		def question = new Question(resources:new TextResource(text:"What?"),
//				answerStrategy: new MultiChoiceStrategy(multiselect: false, choices:
//				[
//					new TextResource(text:"Yes"),
//					new TextResource(text:"No")
//				]))
//		assert question.save() != null
//		def quesOcc = new QuestionOccurrence(askedQuestion: question)
//		assert quesOcc.save() != null
//		populateValidParams(params)
//		def presentation = new Presentation(params)
//		assert presentation.save() != null
//		params.presid = presentation.id
//		flash.message = null
//		controller.ask()
//		assert flash.message != null
//		assert response.redirectedUrl ==
//		"/presentation/show_presenter/${presentation.id}"
//		// Valid presentation and question id's
//		response.reset()
//		populateValidParams(params)
//		def presentation2 = new Presentation(params)
//		assert presentation2.save() != null
//		presentation2.addToQuestions quesOcc
//		params.presid = presentation2.id
//		params.id = quesOcc.id
//		assert presentation2.questions.size() == 1
//		flash.message = null
//		controller.ask()
//		assert flash.message == null
//		assert response.redirectedUrl ==
//		"/presentation/show_presenter/${presentation2.id}"
//		assert presentation2.questions.size() == 1
//		assert presentation2.questions[0].active == true
//	}
	
	/**
	 *
	 */
//	void testEnd_question() {
//		// No presentation or question id
//		controller.end_question()
//		assert flash.message != null
//		assert response.redirectedUrl == '/presentation/list'
//		// No question id
//		response.reset()
//		def question = new Question(resources:new TextResource(text:"What?"),
//				answerStrategy: new MultiChoiceStrategy(multiselect: false, choices:
//				[
//					new TextResource(text:"Yes"),
//					new TextResource(text:"No")
//				]))
//		assert question.save() != null
//		def quesOcc = new QuestionOccurrence(askedQuestion: question, active: true)
//		assert quesOcc.save() != null
//		populateValidParams(params)
//		def presentation = new Presentation(params)
//		assert presentation.save() != null
//		params.presid = presentation.id
//		flash.message = null
//		controller.end_question()
//		assert flash.message != null
//		assert response.redirectedUrl ==
//		"/presentation/show_presenter/${presentation.id}"
//		// Valid presentation and question id's
//		response.reset()
//		populateValidParams(params)
//		def presentation2 = new Presentation(params)
//		assert presentation2.save() != null
//		presentation2.addToQuestions quesOcc
//		assert presentation2.questions[0].active == true
//		params.presid = presentation2.id
//		params.id = quesOcc.id
//		assert presentation2.questions.size() == 1
//		flash.message = null
//		controller.end_question()
//		assert flash.message == null
//		assert response.redirectedUrl ==
//		"/presentation/show_presenter/${presentation2.id}"
//		assert presentation2.questions.size() == 1
//		assert presentation2.questions[0].active == false
//	}

	/**
	 * These are all split up because it seems 'render as json' doesn't work more
	 * than once in one test. Would probably work if it were an integration test.
	 */
	void testRespondNoPresId() {
		def controller = new PresentationController()
		controller.respond()
		assert controller.response.getJson()['message'] == 'default.not.found.message'
	}
	
	void testRespondNoQuesId() {
		populateValidParams(controller.params)
		def presentation = new Presentation(controller.params)
		presentation.room = r
		presentation.owner = admin
		assert presentation.save(flush: true, failOnError:true) != null
		controller.params.presid = presentation.id
		controller.respond()
		assert controller.response.getJson()['message'] == 'default.not.found.message'
	}
	
	void testRespondInactiveQuestion() {
		def choice1 = new TextResource(text:"Yes")
		assert choice1.save(flush: true, failOnError:true) != null
		def choice2 = new TextResource(text:"No")
		assert choice2.save(flush: true, failOnError:true) != null
		def ans = new MultiChoiceStrategy(multiselect: true, choices: [choice1, choice2])
		assert ans.save(flush: true, failOnError:true) != null
		def question = new Question(resources: new TextResource(text:"What?"), answerStrategy: ans, room: r, owner: admin)
		assert question.save(flush: true, failOnError:true) != null		
		populateValidParams(controller.params)
		def presentation = new Presentation(controller.params)
		presentation.room = r
		presentation.owner = admin
		assert presentation.save(flush: true, failOnError:true) != null
		def questionOcc = new QuestionOccurrence(askedQuestion: question, presentation: presentation, active:false)
		assert questionOcc.save(flush: true, failOnError:true) != null
		presentation.addToQuestions(questionOcc)
		controller.params.presid = presentation.id
		controller.params.quesid = questionOcc.id
		controller.params['choice'+questionOcc.id] = [
			choice1.id.toString(),
			choice2.id.toString()
		]
		controller.respond()
		assert controller.response.getJson()['message'] == 'default.question.inactive.message'
	}
	
	void testRespondValid() {
		SpringSecurityService.metaClass.getCurrentUser { -> return am}
		def choice1 = new TextResource(text:"Yes")
		assert choice1.save(flush: true, failOnError:true) != null
		def choice2 = new TextResource(text:"No")
		assert choice2.save(flush: true, failOnError:true) != null
		def ans = new MultiChoiceStrategy(multiselect: true, choices: [choice1, choice2])
		assert ans.save(flush: true, failOnError:true) != null
		def question = new Question(resources: new TextResource(text:"What?"), answerStrategy: ans, room: r, owner: admin)
		assert question.save(flush: true, failOnError:true) != null		
		populateValidParams(controller.params)
		def presentation = new Presentation(controller.params)		
		presentation.room = r
		presentation.owner = admin
		presentation.open = true
		assert presentation.save(flush: true, failOnError:true) != null
		def questionOcc = new QuestionOccurrence(askedQuestion: question, active: true, presentation: presentation)
		assert questionOcc.save(flush: true, failOnError:true) != null
		presentation.addToQuestions questionOcc
		controller.params.presid = presentation.id
		controller.params.quesid = questionOcc.id
		controller.params['choice'+questionOcc.id] = [
			choice1.id.toString(),
			choice2.id.toString()
		]

//		SpringSecurityService.metaClass.getCurrentUser { -> return am}
//		ResponseService.metaClass.addResponse {-> return true }
		controller.respond()
		assert controller.response.getJson()['message'] == 'default.respond.message'
		assert questionOcc.responses.size() == 1
		assert questionOcc.responses.first().choices.size() == 2
		assert questionOcc.responses.first().choices[0].id == choice1.id
		SpringSecurityService.metaClass.getCurrentUser { -> return admin}
	}
	
	void testGetTotalResponsesJSON() {
		populateValidParams(params)
		def presentation = new Presentation(params)
		params.id = presentation.id
		presentation.room = r
		presentation.owner = admin
		assert presentation.save(flush: true, failOnError:true) != null

		TextResource t1 = new TextResource(text:"Yes")
		TextResource t2 = new TextResource(text:"No")
		
		def question = new Question(
				resources:new TextResource(text:"What?"),
				room: r,
				owner: admin,
				answerStrategy: new MultiChoiceStrategy(multiselect: false,
				choices: [
					t1,
					t2					
				]))
		assert question.save(flush: true, failOnError:true) != null
		QuestionOccurrence qOcc = new QuestionOccurrence(askedQuestion: question, active: true, presentation:presentation)
		assert qOcc.save(flush: true, failOnError:true)
		params.id = presentation.id	
		def m = new MultiChoiceResponse(choices:[t1]).save(flush: true, failOnError:true)
		assert presentation.questions != null
		qOcc.addToResponses(m)
		controller.get_total_responses_json()
		assertEquals('{"responseCounts":{"1":1},"feedbackCount":null}', response.text)
	}

	void testNoResponsesGetTotalResponsesJSON() {
		populateValidParams(params)
		def presentation = new Presentation(params)
		params.id = presentation.id
		presentation.room = r
		presentation.owner = admin				
		assert presentation.save(flush: true, failOnError:true) != null
		def question = new Question(
				resources:new TextResource(text:"What?"),
				room: r,
				owner: admin,
				answerStrategy: new MultiChoiceStrategy(multiselect: false,
				choices: [
					new TextResource(text:"Yes"),
					new TextResource(text:"No")
				]))
		assert question.save(flush: true, failOnError:true) != null
		presentation.addToQuestions(new QuestionOccurrence(askedQuestion: question, active: true))
		params.id = presentation.id

		assert controller.get_total_responses_json() == null
	}
	
	void testGetQuestionById() {
		populateValidParams(params)
		def presentation = new Presentation(params)
		presentation.room = r
		presentation.owner = admin
		assert presentation.save(flush: true, failOnError:true) != null
		def question = new Question(
				resources:new TextResource(text:"What?"),
				room: r,
				owner: admin,
				answerStrategy: new MultiChoiceStrategy(multiselect: false,
				choices: [
					new TextResource(text:"Yes"),
					new TextResource(text:"No")
				]))
		assert question.save(flush: true, failOnError:true) != null
		def qo = new QuestionOccurrence(askedQuestion: question, active: true, presentation: presentation)
		assert qo.save(flush: true, failOnError:true) != null
		presentation.addToQuestions(qo)
		params.quesid = qo.id
		params.id = presentation.id
		presentation.save(flush: true, failOnError:true)
		controller.get_question_by_id()
		assert response.text != null
	}

	/**
	 * Tests that the edit action returns the edit page
	 */
	void testEdit() {
		populateValidParams(params)
		def presentation = new Presentation(params)
		presentation.room = r
		presentation.owner = admin
		assert presentation.save(flush: true, failOnError:true) != null
		params.id = presentation.id
		
		def resp = controller.edit()
		assert resp.presentationInstance == presentation
	}
	
	//Tests that changes to a presentation are succesfully saved
	void testUpdate() {
		populateValidParams(params)
		def presentation = new Presentation(params)
		presentation.room = r
		presentation.owner = admin
		assert presentation.save(flush: true, failOnError:true) != null
		params.id = presentation.id
		
		params.name = 'hi'
		controller.update()
		assert presentation.name == 'hi'
	}
	
	
	void tearDown() {
		SpringSecurityService.metaClass = null
		ResponseService.metaClass = null
	}
}
