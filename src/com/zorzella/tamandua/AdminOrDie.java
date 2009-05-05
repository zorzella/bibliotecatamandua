// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AdminOrDie {

  public static void adminOrDie() {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

      if (!Constants.admins.contains(user.getNickname())) {
        throw new RuntimeException();
      }
  }
}
