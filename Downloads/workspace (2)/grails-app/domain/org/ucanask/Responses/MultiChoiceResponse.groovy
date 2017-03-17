package org.ucanask.Responses

import org.ucanask.*

/**
 *
 */
class MultiChoiceResponse extends Response {
	
	Long userId
	List choices
	static hasMany = [choices : Resource]

	static constraints = { 
		choices(minSize:1, nullable:false)
		userId(nullable:true)
	}

	/**
	* @return the strings of each choice made separated by commas
	*/
	String toString() {
		String str = choices[0].toString()
		if (choices.size() > 1) {
			for (int i=1; i<choices.size(); i++) {
				str += ", ${choices[i].toString()}"
			}
		}
		return str
	}
}
