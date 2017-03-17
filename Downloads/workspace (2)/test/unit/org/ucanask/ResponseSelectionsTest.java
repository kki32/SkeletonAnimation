package org.ucanask;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResponseSelectionsTest {

	@Test
	public void testConstructors() {
		ResponseCombinations rc = new ResponseCombinations("A, B, C");
		assertEquals("A, B, C", rc.toString());
		rc = new ResponseCombinations("He, Ha, Car", 5);
		assertEquals("He, Ha, Car", rc.toString());
		assertEquals(new Integer(5), rc.getCount());
	}

	@Test
	public void testHashcode() {
		ResponseCombinations rc = new ResponseCombinations("A, B, C");
		ResponseCombinations rc1 = new ResponseCombinations("A, B, C", 5);
		assertEquals(rc.hashCode(), rc1.hashCode());
		rc1 = new ResponseCombinations("A, C", 5);
		assertNotSame(rc.hashCode(), rc1.hashCode());		
	}

	@Test
	public void testEquals() {
		ResponseCombinations rc = new ResponseCombinations("A, B, C");
		ResponseCombinations rc1 = new ResponseCombinations("A, B, C", 5);
		assertEquals(rc, rc1);
		rc1 = new ResponseCombinations("A, C", 5);
		assertNotSame(rc, rc1);		
	}
	
	@Test
	public void testCompareTo() {
		ResponseCombinations rc = new ResponseCombinations("A, B, C", 3);
		ResponseCombinations rc1 = new ResponseCombinations("A, B, C", 5);
		assertEquals(0, rc.compareTo(rc1));
		rc1 = new ResponseCombinations("A, C", 5);
		assertEquals(1, rc.compareTo(rc1));
		rc1 = new ResponseCombinations("A, C", 1);
		assertEquals(-1, rc.compareTo(rc1));
		rc1 = new ResponseCombinations("A, C", 3);
		assertEquals(-1, rc.compareTo(rc1));
	}	
		
}
