package org.ucanask.chart

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import org.junit.rules.ExpectedException;

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class WordTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none()	
	
	void testCreateValidWord() {
		String s = "booyah"
		Word w = new Word(s)
		assert w != null && w.toString().equals(s)	
		
		String spaced = " abc "
		w = new Word(spaced)
		assert w!= null && w.toString().equals("abc")		
	}
	
	/**
	* Create a word with empty text
	*/
	void testCreateInvalidWord1() {
		thrown.expect(IllegalArgumentException.class)
		thrown.expectMessage("Word text length must be > 0")
		String blank = ""
		Word w = new Word(blank)
		assert w == null
	}
	
	/**
	 * Create a word with only whitespace characters
	 */
	void testCreateInvalidWord2() {	
		thrown.expect(IllegalArgumentException.class)	
		thrown.expectMessage("Word text length must be > 0")
		String ws = " "		
		Word w = new Word(ws)	
		assert w == null
	}
	
	void testEquals() {
		Word w1 = new Word("equal")
		Word w2 = new Word("equal")
		Word w3 = new Word("notEqual")
		Object obj = new Object()
		assert w1.equals(w2) && w2.equals(w1)
		assert !w1.equals(w3) && !w3.equals(w1)
		assert !w2.equals(w3) && !w3.equals(w2)		
		assert !w1.equals(obj) && !obj.equals(w1)		
	}
	
	void testCompareTo() {
		Word w1 = new Word("w1")
		Word w2 = new Word("w2")		
		assert w1.compareTo(w2) == 0
		w1.setWeight(5.0)
		assert w1.compareTo(w2) < 0
		assert w2.compareTo(w1) > 0		
		w2.setWeight(5)
		assert w1.compareTo(w2) == 0				
	}
	
	
	
	
}




