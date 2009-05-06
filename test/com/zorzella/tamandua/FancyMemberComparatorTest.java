// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import junit.framework.TestCase;

public class FancyMemberComparatorTest extends TestCase {

	public void testComparator() throws Exception {
		
		assertLesser("a", "", "b", "");
		assertLesser("a", "a", "b", "");
		assertLesser("c", "a", "b", "");
		assertLesser("a", "a", "b", "a");
		assertLesser("b", "a", "c", "a");
		assertLesser("b", "", "a", "?");
	}

	private void assertLesser(
			String tituloOne, String paradeiroOne, 
			String tituloOther, String paradeiroOther) {
		Book one = book(tituloOne, paradeiroOne);
		Book other = book (tituloOther, paradeiroOther);
		assertTrue (new FancyMemberComparator().compare(
				one, other) < 0);
		assertTrue (new FancyMemberComparator().compare(
				other, one) > 0);
	}

	private Book book(String titulo, String paradeiro) {
		return new Book(paradeiro, "", "", titulo, "", false, "");
	}
	
}
