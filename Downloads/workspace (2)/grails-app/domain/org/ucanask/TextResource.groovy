package org.ucanask

/**
 *
 */
class TextResource extends Resource {

	String text

	/**
	 *
	 */
	String toString() {
		text
	}
	
	/**
	 *
	 */
	static constraints = {
		text(blank:false, nullable:false)
	}
}
