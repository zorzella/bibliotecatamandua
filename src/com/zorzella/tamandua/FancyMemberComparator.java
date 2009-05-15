package com.zorzella.tamandua;

import java.util.Comparator;

public class FancyMemberComparator implements Comparator<Item> {

  @Override
  public int compare(Item one, Item other) {
    
//    // Unknown paradeiro books group at the end
//    if ((one.getParadeiro().equals("?")) && (!other.getParadeiro().equals("?"))) {
//    	return 1;
//    }
//    if ((other.getParadeiro().equals("?")) && (!one.getParadeiro().equals("?"))) {
//    	return -1;
//    }

//    // Known paradeiro books group in the beginning
//    if ((one.getParadeiro().length() > 0) && (other.getParadeiro().length() == 0)) {
//      return -1;
//    }    
//    if ((one.getParadeiro().length() == 0) && (other.getParadeiro().length() > 0)) {
//      return 1;
//    }
    // Known paradeiro books group in the beginning
    if ((one.getParadeiro() != null) && (other.getParadeiro() == null)) {
      return -1;
    }    
    if ((one.getParadeiro() == null) && (other.getParadeiro() != null)) {
      return 1;
    }
    
    if ((one.getParadeiro() == null) && (other.getParadeiro() == null)) {
      return one.compareTo(other);
    }
    // Group the books with same paradeiro
    int result = one.getParadeiro().compareTo(other.getParadeiro());
    if (result != 0) {
      return result;
    }
    
    // All things being equal, alphabetical order
    return one.compareTo(other);
  }
}
