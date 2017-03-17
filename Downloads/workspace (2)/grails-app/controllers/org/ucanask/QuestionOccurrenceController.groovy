package org.ucanask

import grails.converters.*
import org.ucanask.AnswerStrategy.FreeTextStrategy
import org.ucanask.Responses.Response

//import javax.mail.internet.MimeUtility;
import grails.plugins.springsecurity.Secured


/**
 * Supplies data for displaying a questionOccurrence, handles ajax calls for updates.
 * @author Jon Rutherford
 */
class QuestionOccurrenceController {
	
	def springSecurityService

	/**
	 * Show the questionoccurrence with title and chart
	 * @return the questionOccurrenceInstance with supplied id
	 */
	@Secured(["ROLE_PR"])
	def show() {
		def questionOccurrenceInstance = QuestionOccurrence.get(params.id)
		if (!questionOccurrenceInstance) {
			flash.message = message(code: 'default.not.found.message', 
				args: [message(code:'questionOccurence.label', default: 'Question Occurrence')])
			return redirect(controller: "room", action: "list")
			
		}
		if (!params.displayType) {
			params.displayType = "cloud"
		}
		if (!params.filters) {
			params.filters = true
		}
		if (!params.hide) {
			params.hide = false
		}

		def user = springSecurityService?.currentUser
		switch(questionOccurrenceInstance?.presentation?.room?.userAccess(user?.id)) {
			case AccessLevel.Admin:
			case AccessLevel.Presenter:				
				return [questionOccurrenceInstance: questionOccurrenceInstance, displayType: params.displayType, filters: params.filters, hide: params.hide]
			default:
				return redirect(controller: "presentation", action: "show", id: questionOccurrenceInstance?.presentation?.id,
					params: [roomId: questionOccurrenceInstance?.presentation?.room?.id])
		} 

	}

	/**new ResponseCombinations(
	 * @return a map of all responses from a given question occurrence.
	 */
	private def getResponses() {	
		def occurrence = QuestionOccurrence.get(params.id)
		params["occurrence"] = occurrence
		if ( occurrence?.askedQuestion?.answerStrategy instanceof FreeTextStrategy) {
			params["presId"] = springSecurityService.getCurrentUser().id
			
		}
		return occurrence?.askedQuestion?.answerStrategy?.responseCollection(params)
	}

	/**
	 * Render the map of responses for a questionOccurrence as JSON
	 * @return a JSON string containing the map of responses
	 */
	def getResponsesJSON() {
		render getResponses() as JSON
	}
	
	def getResponsesImage() {
		response.contentType="image/png"
		response.outputStream << getResponses()
		response.outputStream.flush()		
	}
	
	/**
	 * Get the version of this questionOccurrence. Used for checking whether
	 * a chart needs to be updated or not.
	 * @return the current version number of this question instance
	 */
	def getVersion() {
		render QuestionOccurrence.get(params.id)?.vers
	}
	
	/**
	 * Get the previous question
	 * @return
	 */
	def prevQuestion() {
		def qOcc = QuestionOccurrence.get(params.id)
		if (!qOcc) {
			//TODO error handling
		}
		def pres = qOcc.presentation
		def index = pres.questions.indexOf(qOcc)
		def sz = pres.questions.size()
		def prevIndex = index > 0 ? index -1 : sz -1
		def prevQ = pres.questions.get(prevIndex)
		redirect(action: 'show', id: prevQ.id, params: [hide:params.hide])
	}
	
	/**
	 * Get the next question
	 * @return
	 */
	def nextQuestion() {
		def qOcc = QuestionOccurrence.get(params.id)
		if (!qOcc) {
			//TODO error handling
		}
		def pres = qOcc.presentation
		def index = pres.questions.indexOf(qOcc)
		def sz = pres.questions.size()
		def nextIndex = sz - 1 == index ? 0 : index + 1
		def nextQ = pres.questions.get(nextIndex)
		redirect(action: 'show', id: nextQ.id, params: [hide:params.hide])
	}
	
	/**
	 * Ajax method to update the ignore list for a freetext question
	 */
	def updateFiltersAjax()		
	{
		def questionOccurrenceInstance = QuestionOccurrence.get(params.qoccId)
		def respJson
		if(questionOccurrenceInstance){
			respJson = [success:true]
			
			def wordList = params.textList.split(" +")
			//println wordList
			
			questionOccurrenceInstance.ignoredWords = wordList
			
			questionOccurrenceInstance.globalIgnoreList = (params.globalFilter == 'true' ? true : false)
			questionOccurrenceInstance.presIgnoreList = (params.ownerFilter == 'true' ? true : false)
			//println questionOccurrenceInstance.getIgnoredWordsList()
			questionOccurrenceInstance.vers++
			
			
			questionOccurrenceInstance.save(flush: true)
			
			
			
			
		} else {
			respJson = [success:false]
		}
		render respJson as JSON
	}
	
	def removeResponseAjax()
	{
		def questionOccurrenceInstance = QuestionOccurrence.get(params.qoccId)
		def responseInstance = Response.get(params.respId)
		def respJson = [success:false]
		if(questionOccurrenceInstance?.responses.contains(responseInstance)){
			responseInstance.enabled = false
			if(questionOccurrenceInstance.save(flush: true))
				respJson = [success:true]
		}
		render respJson as JSON
	}
}





