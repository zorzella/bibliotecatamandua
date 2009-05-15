// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Date;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;

import com.google.appengine.repackaged.com.google.common.base.Join;

public class Dates {

  public static String dateToString(Date date) {
    if (date == null) {
      return "";
    }
    Instant temp = new Instant(date.getTime());
    return Join.join("-",
      temp.get(DateTimeFieldType.year()),
      temp.get(DateTimeFieldType.monthOfYear()),
      temp.get(DateTimeFieldType.dayOfMonth()));
  }
}
