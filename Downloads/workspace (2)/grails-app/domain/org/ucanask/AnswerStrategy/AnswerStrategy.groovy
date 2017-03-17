package org.ucanask.AnswerStrategy

import org.ucanask.*
import org.ucanask.Responses.*

/**
 *
 */
abstract class AnswerStrategy {

	static belongsTo = Question

	/**
	 * Factory method to return a response of the correct type when
	 * given the user (eventually) and the required params 
	 */
	abstract Response makeResponse(dict)

	
	/**
	 * Create a collection of all of the responses for the question occurrence
	 * with the child strategy for a specific display type
	 */
	abstract def responseCollection(dict)
	
	/**
	 * Practically toString except forcing implementations to override. Used in
	 * select of question type. 
	 */
	abstract String typeName()
}
