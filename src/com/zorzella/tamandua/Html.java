// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.PrintWriter;

public class Html {
  public static void htmlHeadBody(PrintWriter ps) {
    ps.println("<html>");
    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
    ps.println("<body onkeydown='if (event.keyCode == 13) { event.keyCode = 9; /*return event.keyCode;*/ return false; }'>");
    a(ps, "/list", "Acervo");

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    if ((user != null) && (Constants.admins.contains(user.getNickname()))) {
      a(ps, "/modifyitems", "Itens");
      a(ps, "/modifymembers", "Members");
      a(ps, "/modifyloans", "Loans");
      a(ps, "/member", "Borrow");
      a(ps, "/email", "Email");
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
