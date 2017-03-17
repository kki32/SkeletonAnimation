package org.ucanask

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import org.springframework.dao.DataIntegrityViolationException
import org.ucanask.AnswerStrategy.AnswerStrategy
import org.ucanask.AnswerStrategy.FreeTextStrategy
import org.ucanask.AnswerStrategy.MultiChoiceStrategy
/**
 *
 */
class QuestionController {

	def springSecurityService

	static allowedMethods = [save_JSON: "POST", update_JSON: "POST"]


	@Secured(["ROLE_PR"])
	def save_JSON() {
		def pres = Presentation.get(params.presid)
		if (!pres) {
			flash.message = "Presentation not found!"
			return makeJSONResponse(false)
		}
		def access = pres.room.userAccess(springSecurityService.currentUser?.id)
		if(!(access == AccessLevel.Admin || access == AccessLevel.Presenter)) {
			flash.message = "Unauthorised"
			return makeJSONResponse(false)
		}
		params.owner = springSecurityService.currentUser
		params.qLocked = false
		if (params.questionText) // Hack to not crash when text > VARCHAR size
			if (params.questionText.trim().length()>254)
				params.questionText = params.questionText.substring(0, 254)
		def questionInstance = makeQuestion(params)
		if (!questionInstance.save()) { 
			flash.message = message(code: 'default.button.save.error',
						args: [message(code: 'question.label', default: 'Question')])
			return makeJSONResponse(false)
		}
		def questionOcc = new QuestionOccurrence(askedQuestion: questionInstance)
		pres.addToQuestions(questionOcc)
		questionOcc.save()
		flash.message = message(code: 'default.created.message',
				args: [message(code: 'question.label', default: 'Question'), questionInstance.id])
		def map = ['message': flash.message, 'success': true, 'colour':'green','id':questionOcc.id]
		render map as JSON
	}
	/**
	* Renders a JSON response, with a message and a colour for the message.
	* @param success true will display in green, false red.
	* @return A JSON response message.
	*/
	private def makeJSONResponse(boolean success) {
		def map = ["message": flash.message]
		map['success'] = success
		map['colour'] = success ? "green" : "red"
		render map as JSON
	}
	
	@Secured(["ROLE_PR"])
	def update_JSON() {
		def pres = Presentation.get(params.presid)
		if (!pres) {
			flash.message = "Presentation not found!"
			return makeJSONResponse(false)
		}
		def access = pres.room.userAccess(springSecurityService.currentUser?.id)
		if(!(access == AccessLevel.Admin || access == AccessLevel.Presenter)) {
			flash.message = "Unauthorised"
			return makeJSONResponse(false)
		}
		def questionOcc = QuestionOccurrence.get(params.id)
		if (!questionOcc) {
			flash.message = message(code: 'default.not.found.message',
					args: [message(code: 'question.label', default: 'Question'), params.id])
			return makeJSONResponse(false)
		}
		def questionInstance = questionOcc.askedQuestion
		if (questionInstance.isLocked()) {
			//pres.questions.remove(questionOcc)
			return save_JSON()
		}
		String qText = params.questionText
		if (qText) // Hack to not crash when text > VARCHAR size
			if (qText.trim().length()>254)
				qText = qText.substring(0, 254)
		switch(params.questionType) {
			case "Multi-Choice" :
				questionInstance.resources = [new TextResource(text: qText)]
				questionInstance.answerStrategy = extractMCStrategy()
				break
			case "FreeText" :
				questionInstance.resources = [new TextResource(text: qText)]
				questionInstance.answerStrategy = new FreeTextStrategy()
				break
		}
		if (!questionInstance.save()) {
			flash.message = message(code: 'default.button.save.error',
				args: [message(code: 'question.label', default: 'Question')])
			return makeJSONResponse(false)
		}
		flash.message = message(code: 'default.updated.message',
				args: [message(code: 'question.label', default: 'Question'), questionInstance.id])
		def map = ['message': flash.message, 'success': true, 'colour':'green','id':questionOcc.id]
		render map as JSON
		return
	}

	/**
	 * Creates a MultiChoiceStrategy from the details given when creating
	 * or updating a multi-choice question.
	 * @return a new MultiChoiceStrategy
	 */
	private MultiChoiceStrategy extractMCStrategy() {
		List resources = []
		params.list("choice").each {
			if (it.trim().length()>0) {
				if (it.trim().length()>254) it = it.substring(0, 254)
				resources.add(new TextResource(text: it))
			}
		}
		// "on" if checked which == true, otherwise wont exist (null) which == false
		boolean multiSelect = params.multiselect
		return new MultiChoiceStrategy(choices: resources, multiselect: multiSelect);
	}

 
	/**
	 * A private method to create a question, whether it has a presentation or not
	 * @param params question details
	 * @return a new Question
	 */
	private Question makeQuestion(Map params) {
		AnswerStrategy strategy = null
		switch(params.questionType) {
			case "Multi-Choice" :
				strategy = extractMCStrategy()
				break;
			case "FreeText" :
				strategy = new FreeTextStrategy()
				break;
		}
		return new Question(resources: new TextResource(text: params.questionText), answerStrategy: strategy, 
			owner: springSecurityService.getCurrentUser())
	}
	
	def createQuestion() {
		render(template: "/presentation/questionEdit")
	}
}
