// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.zorzella.tamandua.Book;

import junit.framework.TestCase;

public class BookTest extends TestCase {

  public void testToString() throws Exception {
    Book book = new Book("a", "b", "c", "d", true, true, "g", "h", "i");
    assertEquals("null,a,b,c,d,true,true,g,h,i", book.toString());
  }

  public void testToStringWithComma() throws Exception {
    Book book = new Book("a", "b", "c,0", "d", true, true, "g", "h", "i");
    assertEquals("null,a,b,\"c,0\",d,true,true,g,h,i", book.toString());
  }
  
}
