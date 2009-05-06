// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.io.PrintWriter;

public class Html {

  public static void htmlHeadBody(PrintWriter ps) {
    ps.println("<html>");
    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
    ps.println("<body>");
  }

}
