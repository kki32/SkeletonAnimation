package org.ucanask

import org.ucanask.AnswerStrategy.FreeTextStrategy
import org.ucanask.QrCodeService.QrCode
import org.ucanask.Responses.*
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.apache.commons.lang.RandomStringUtils


/**
 * A Presentation represents a single session of questions e.g a lecture.
 */
class Presentation implements Comparable {
	
	boolean active = false	
	boolean open = false
	String name
	List questions
	Room room
	User owner
	String accessKey
	Date created = new Date()
	
	
		// Audience member questions properties
	List audienceQuestions
	boolean allowUserQuestions = true
	int audienceQuestionVersion = 0
	
	static belongsTo = [User, Room]
	static hasMany = [questions: QuestionOccurrence, audienceQuestions: FreeTextResponse]
	static fetchMode = [room: "eager", questions: "eager"]

	/**
	 *
	 */
	static constraints = {
		questions(nullable: true)
		name(blank: false)
		//owner(nullable: true) // TODO: to allow bootstrap to work this is true, change back to false
		active(nullable:false)
		accessKey (nullable:true, unique:true)
	}
	/**
	 * Enables a disabled question and vice versa. Returns the active status of the question.
	 */
	boolean toggleQuestion(QuestionOccurrence) {
		active = true
		if(questions?.contains(QuestionOccurrence)) {
			QuestionOccurrence.active = (QuestionOccurrence.active) ?  false : true
			return QuestionOccurrence.active
		}
		return false // Not necessarily true but hey
	}
	
	void askAllQuestions() {
		active = true
		questions.each { it.active = true }
	}
	/**
	 * Makes all questions inactive
	 */
	void endPresentation() {
		active = false
		allowUserQuestions = false
		questions?.each { it.active = false }
	}

	
	/**
	 * @return the name of the presentation
	 */
	String toString() {
		name
	}
	
	/**
	 * Tests whether a question is used within the presentations
	 * @param Question the question to check
	 * @return true if the question is not in this presentation 
	 */
	boolean questionNotUsed(Question) {
		return !(questions?.find { it.id == Question.id } as boolean) 
	}

	@Override
	public int compareTo(Object o) {
		return created.compareTo(o.created)
	}
	
	/**
	 * Enable the ability for audience members to ask questions
	 * @return
	 */
	def toggleAudienceQuestion() {
		allowUserQuestions = allowUserQuestions ? false : true 
	}
	
	/**
	 * Enable the ability for guests to enter a presentation
	 * @return
	 */
	def toggleOpen() {
		open = open ? false : true
	}
	
}
