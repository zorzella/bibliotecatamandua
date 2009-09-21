// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.base.Join;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.util.Date;

public class Dates {

  public static String dateToString(Date date) {
    if (date == null) {
      return "";
    }
    DateTimeZone pst = DateTimeZone.forOffsetHours(-8);
    Instant temp = new Instant(pst.convertUTCToLocal(date.getTime()));
    return Join.join("-",
      temp.get(DateTimeFieldType.year()),
      temp.get(DateTimeFieldType.monthOfYear()),
      temp.get(DateTimeFieldType.dayOfMonth()));
  }
}
