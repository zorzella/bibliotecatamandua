// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Comparator;

public class FancyMemberComparator implements Comparator<Book> {

  @Override
  public int compare(Book one, Book other) {
    
    if ((one.getParadeiro().length() == 0) && (other.getParadeiro().length() == 0)) {
      return one.compareTo(other);
    }
    
    if ((one.getParadeiro().length() > 0) && (other.getParadeiro().length() == 0)) {
      return -1;
    }
    
    if ((one.getParadeiro().length() == 0) && (other.getParadeiro().length() < 0)) {
      return 1;
    }
    
    int result = one.getParadeiro().compareTo(other.getParadeiro());
    
    if (result != 0) {
      return result;
    }
    return one.compareTo(other);
    
  }
}
