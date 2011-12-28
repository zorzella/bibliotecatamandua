// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import com.google.common.base.Joiner;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.util.Date;

public class Dates {

  public static String dateToString(Date date) {
    if (date == null) {
      return "";
    }
    DateTimeZone pst = DateTimeZone.forID("America/Los_Angeles");
    Instant temp = new Instant(pst.convertUTCToLocal(date.getTime()));
    return Joiner.on("-").join(
      temp.get(DateTimeFieldType.year()),
      temp.get(DateTimeFieldType.monthOfYear()),
      temp.get(DateTimeFieldType.dayOfMonth()));
  }
}
