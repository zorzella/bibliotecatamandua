// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AdminOrDie {

  public static String adminOrLogin(HttpServletRequest req, HttpServletResponse resp) {
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
        throw new RuntimeException(String.format(
            "User %s not an admin", user.getNickname()));
      }
      return user.getNickname();
    }
  }
}
