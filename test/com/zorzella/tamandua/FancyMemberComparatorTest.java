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
        assertLesser("anjo", "", "a barco", "");
        assertLesser("o anjo", "", "barco", "");
        assertLesser("um anjo", "", "barco", "");
        assertLesser("uma anjo", "", "barco", "");
        assertLesser("anjo", "", "as barco", "");
        assertLesser("os anjo", "", "barco", "");
        assertLesser("uns anjo", "", "barco", "");
        assertLesser("umas anjo", "", "barco", "");
        assertLesser("anjo", "", "A barco", "");
        assertLesser("O anjo", "", "barco", "");
        assertLesser("Um anjo", "", "barco", "");
        assertLesser("Uma anjo", "", "barco", "");
	}

	private void assertLesser(
			String tituloOne, String paradeiroOne, 
			String tituloOther, String paradeiroOther) {
		Item one = book(tituloOne, paradeiroOne);
		Item other = book (tituloOther, paradeiroOther);
		assertTrue (new FancyMemberComparator().compare(
				one, other) < 0);
		assertTrue (new FancyMemberComparator().compare(
				other, one) > 0);
	}

	private Item book(String titulo, String paradeiro) {
		return new Item(paradeiro, "", "", titulo, "", false, "");
	}
	
}
