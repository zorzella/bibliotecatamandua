// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Map;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

import junit.framework.TestCase;

public class FancyItemComparatorTest extends TestCase {

  /*
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
	*/
	private final Map<Long,String> paradeiroToCodeMap = Maps.newHashMap();
	 
	private void assertLesser(
			String tituloOne, Long paradeiroOne, 
			String tituloOther, Long paradeiroOther) {
		Item one = book(tituloOne, paradeiroOne);
		Item other = book (tituloOther, paradeiroOther);
		assertTrue (new FancyItemComparator(paradeiroToCodeMap).compare(
				one, other) < 0);
		assertTrue (new FancyItemComparator(paradeiroToCodeMap).compare(
				other, one) > 0);
	}

	private Item book(String titulo, Long paradeiro) {
		return new Item(paradeiro, "", "", titulo, "", false, "");
	}
	
}
