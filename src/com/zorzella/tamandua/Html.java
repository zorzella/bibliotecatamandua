// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.PrintWriter;

public class Html {
  
  public static void justHeadBody(PrintWriter ps) {
    ps.println("<html>");
    ps.println("<head>" +
            "<title>Bilioteca Tamandu&aacute; Bandeira</title>" +
            "<link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/>" +
            "<meta name='viewport' content='width=device-width'/>" +
            "</head>");
    ps.println("<body>");
  }
   
  public static void htmlHeadBody(PrintWriter ps) {
    justHeadBody(ps);
    a(ps, "/list", "Acervo");
    ps.println(
        "<a href='http://www.mensageirosdacultura.com/MDC_Biblioteca_const.html' target='_new'>" +
        "  Constitui&ccedil;&atilde;o</a>");

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    if ((user != null) && (Constants.admins.contains(user.getNickname()))) {
      a(ps, "/tamandua", "GWT");
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

  static String checkbox(boolean selected, String key, String label) {
    return "<input type='checkbox' name='" + key + "'" + (selected ? " checked" : "" ) + ">" + label + "</input>\n";
  }

  static String dropdown(Sort sortKey, String key, String label) {
    return "<option value='" + key + "'" + (sortKey.toString().equals(key) ? " selected" : "") + ">" + label + "</option>\n";
  }

  static void printOrderDropDown(PrintWriter ps, Sort sortKey) {
    ps.println("<br>Ordem: " +
        "<select name='sort'>" +
        dropdown(sortKey, "PARADEIRO", "Paradeiro") +
        dropdown(sortKey, "TOCA", "Toca") +
        dropdown(sortKey, "TYPE", "Tipo") +
        dropdown(sortKey, "TITULO", "Titulo") +
        dropdown(sortKey, "AUTOR", "Autor") +
        "</select>" +
    "");
  }
}
