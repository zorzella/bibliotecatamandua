// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.zorzella.tamandua.gwt.client.NotAnAdminException;

public class AdminOrDie {

  public static String adminOrLogin(
      HttpServletRequest req, HttpServletResponse resp) throws NotAnAdminException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    if (user == null) {
      try {
        resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return null;
    } else {
      if (!Constants.admins.contains(user.getNickname())) {
        throw new NotAnAdminException(String.format(
            "User %s not an admin", user.getNickname()));
      }
      return user.getNickname();
    }
  }

  public static User softAdminOrDie() throws NotAnAdminException {
    return adminOrDie(Constants.softAdmins);
  }
  
  public static User adminOrDie() throws NotAnAdminException {
    return adminOrDie(Constants.admins);    
  }
  
  private static User adminOrDie(Collection<String> admins) throws NotAnAdminException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    if ((user == null) || (!admins.contains(user.getNickname()))) {
        throw new NotAnAdminException(String.format(
            "User %s not an admin", user == null ? null : user.getNickname()));
    }
    return user;
  }
}
