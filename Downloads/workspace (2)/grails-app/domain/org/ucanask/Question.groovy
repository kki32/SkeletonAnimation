package org.ucanask

import org.ucanask.AnswerStrategy.*;

/**
 *
 */
class Question {

	List resources
	AnswerStrategy answerStrategy
	User owner
	
	/** Used to stop a question being edited/deleted when it has already been
	 *  asked or is included in two or more presentations. Call isLocked()	*/
	boolean qLocked = false
	String whyLocked
	static transients = ['locked']

	static hasMany = [resources : Resource]
	static belongsTo = User
	
	static fetchMode = [owner:'eager']

	/**
	 *
	 */
	static constraints = {
		resources(minSize:1, nullable:false)
		answerStrategy(nullable:false, validator: {return it.validate()})
		qLocked(nullable:false)
		owner(nullable:false)
		whyLocked(nullable:true)
	}

	/**
	 * @return the string of first resource, otherwise null 
	 */
	String toString() {
		return resources?.size()>0 ? resources.first().toString() : null
	}
	
	
	/**
	 * Tests if a question has been included in more than 1 presentation
	 * If so, it locks that question from being editable/deletable.
	 * @return true if locked
	 */
	def boolean isLocked() {
		if (qLocked) return true
		def quesOccs = QuestionOccurrence.findAllWhere(askedQuestion: this)
		if (quesOccs.size()>1) {
			whyLocked = "included in another presentation"
			qLocked = true
		}
		else if (quesOccs.find { it.responses?.size() > 0 }) {
			whyLocked = "asked before and has responses"
			qLocked = true
		}
		return qLocked
	}
}
