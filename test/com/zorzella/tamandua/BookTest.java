// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.zorzella.tamandua.Item;

import junit.framework.TestCase;

public class BookTest extends TestCase {

  public void testToString() throws Exception {
    Item book = new Item("a", "b", "c", "d", "e", false, "g");
    assertEquals("null,a,b,c,d,e,false,g,[]", book.toString());
  }

  public void testToStringWithComma() throws Exception {
    Item book = new Item("a", "b", "c", "d,0", "e,0", false, "g");
    assertEquals("null,a,b,c,\"d,0\",\"e,0\",false,g,[]", book.toString());
  }

  
}
