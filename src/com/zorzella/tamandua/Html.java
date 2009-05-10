// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class Html {
  public static void htmlHeadBody(PrintWriter ps) {
    ps.println("<html>");
    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
    ps.println("<body>");
    a(ps, "/list", "Acervo");
    
    
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	if ((user != null) && (Constants.admins.contains(user.getNickname()))) {
		a(ps, "/modifyitems", "Items");
        a(ps, "/modifymembers", "Members");
        a(ps, "/modifyloans", "Loans");
        a(ps, "/member", "Borrow");
	}
	
	ps.println("<hr>");
}

  private static void a(PrintWriter ps, String url, String text) {
	  ps.println("<a href='" + url + "'>" + text + "</a>");
  }

  public static PrintWriter tdRight(PrintWriter ps, String content) {
    return ps.printf("<td align='right'>%s</td>", content);
  }

  public static PrintWriter td(PrintWriter ps, String content) {
    return ps.printf("<td>%s</td>", content);
  }

}
