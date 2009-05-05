// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import com.zorzella.tamandua.UploadServlet;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class UploadServletTest extends TestCase {

  public void testParseShortLineWithLastItemQuoted() throws Exception {

    String lineToParse = "a,\"b\",\"c\"";
    List<String> parsedLine = UploadServlet.parseLine(lineToParse, 9);
    
    ArrayList<String> expected = 
      Lists.newArrayList("a", "b", "c", "", "", "", "", "", "");
    
    assertEquals(expected, parsedLine);
  }

  public void testParseLine() throws Exception {

    String lineToParse = "a,b,c,d,e,f,g,h,i";
    List<String> parsedLine = UploadServlet.parseLine(lineToParse, 9);
    
    ArrayList<String> expected = 
      Lists.newArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i");
    
    assertEquals(expected, parsedLine);
  }

  public void testParseShortLine() throws Exception {

    String lineToParse = "a,b,c,d";
    List<String> parsedLine = UploadServlet.parseLine(lineToParse, 9);
    
    ArrayList<String> expected = 
      Lists.newArrayList("a", "b", "c", "d", "", "", "", "", "");
    
    assertEquals(expected, parsedLine);
  }
  
  public void testParseLineWithQuote() throws Exception {

    String lineToParse = "a,\"b\",\"c\",d,e,f,g,h,i";
    List<String> parsedLine = UploadServlet.parseLine(lineToParse, 9);
    
    ArrayList<String> expected = 
      Lists.newArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i");
    
    assertEquals(expected, parsedLine);
  }
  
  public void testParseLineWithCommasInsideQuote() throws Exception {

    String lineToParse = "a,\"b\",\"c,0,1\",d,e,f,g,h,i";
    List<String> parsedLine = UploadServlet.parseLine(lineToParse, 9);
    
    ArrayList<String> expected = 
      Lists.newArrayList("a", "b", "c,0,1", "d", "e", "f", "g", "h", "i");
    
    assertEquals(expected, parsedLine);
  }
}
