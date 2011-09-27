package com.zorzella.tamandua;

import com.zorzella.tamandua.Item;

import junit.framework.TestCase;

public class BookTest extends TestCase {

  public void testToString() throws Exception {
    Item book = new Item(null, "b", "c", "d", "e", "f", false, "g");
    assertEquals("null,null,b,c,d,e,f,false,g,[]", book.toString());
  }

  public void testToStringWithComma() throws Exception {
    Item book = new Item(null, "b", "c", "d,0", "e,0", "f,0", false, "g");
    assertEquals("null,null,b,c,\"d,0\",\"e,0\",\"f,0\",false,g,[]", book.toString());
  }
}
