package org.ucanask.Responses

import org.ucanask.User;

class FreeTextResponse extends Response {
	
	Long userId
	String textResponse
	boolean anonymous = true
	

    static constraints = {
		textResponse(nullable:false, blank:false)
		userId(nullable:true)
    }
	
	static mapping = {
		textResponse type:"text"
	}
	
	String toString() {
		textResponse
	}
}
