package org.ucanask

import java.util.List

import org.hibernate.StaleObjectStateException
import org.ucanask.Responses.*

/**
 * Represents an instance of a Question which has been asked in a specific presentation
 * @author 
 */
class QuestionOccurrence {
	boolean active = false
	Question askedQuestion
	List<Response> responses
	Presentation presentation

	static belongsTo = Presentation
	static hasMany = [responses : Response, ignoredWords : String]
	long vers = 0
	
	List ignoredWords
	boolean presIgnoreList
	boolean globalIgnoreList

	static constraints = { askedQuestion(nullable:false) }	
	static fetchMode = [presentation: "eager"]

	String toString() {
		return askedQuestion.toString()
	}
	

	/**  
	 * Add a response to this questionOccurrence 
	 * @param dict map containing the response to add, and id of user who responded
	 */
	void addResponse(dict) {
		if (!active) { // Checked by controller but here too anyway
			return
		}
		// Over-writes previous submissions
		def prev
		if (dict?.userid) {
			prev = responses.find { it.userId == dict.userid }
		}
		vers++
		def resp = askedQuestion.answerStrategy.makeResponse(dict)
		if (resp != null) {
			if (prev != null) {
				responses.remove(prev)
			}
			resp.userId = dict.userid
			this.addToResponses(resp)
		}
	}
	
	String getIgnoredWordsList(){
		def ignWords = ""
		ignoredWords.each() { it ->
			ignWords += (it + ' ')
		}
		return ignWords
	}
}



