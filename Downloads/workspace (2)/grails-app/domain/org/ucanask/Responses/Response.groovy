package org.ucanask.Responses

import org.ucanask.User;

/**
 *
 */
abstract class Response {
	Long userId
	
	boolean enabled = true
	
	static constraints = {
		userId(nullable:true)
	}
}
