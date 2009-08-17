package com.zorzella.tamandua;

import java.util.Comparator;
import java.util.Map;

public class FancyMemberComparator implements Comparator<Item> {

	private final Map<Long,String> paradeiroToCodeMap;
	
	public FancyMemberComparator(Map<Long,String> paradeiroToCodeMap) {
		this.paradeiroToCodeMap = paradeiroToCodeMap;
	}
	
  @Override
  public int compare(Item one, Item other) {
    
    // Unknown paradeiro books group at the end
    if ((paradeiro(one).equals("?")) && (!paradeiro(other).equals("?"))) {
    	return 1;
    }
    if ((paradeiro(other).equals("?")) && (!paradeiro(one).equals("?"))) {
    	return -1;
    }

    // Known paradeiro books group in the beginning
    if ((paradeiro(one).length() > 0) && (paradeiro(other).length() == 0)) {
      return -1;
    }    
    if ((paradeiro(one).length() == 0) && (paradeiro(other).length() > 0)) {
      return 1;
    }
//    // Known paradeiro books group in the beginning
//    if ((paradeiro(one) != null) && (paradeiro(other) == null)) {
//      return -1;
//    }    
//    if ((paradeiro(one) == null) && (paradeiro(other) != null)) {
//      return 1;
//    }
    
    if ((paradeiro(one) == null) && (paradeiro(other) == null)) {
      return one.compareTo(other);
    }
    // Group the books with same paradeiro
    int result = paradeiro(one).compareTo(paradeiro(other));
    if (result != 0) {
      return result;
    }
    
    // All things being equal, alphabetical order
    return one.compareTo(other);
  }

  private String paradeiro(Item item) {
    Long temp = item.getParadeiro();
    if (temp == null) {
      return "";
    }
    return paradeiroToCodeMap.get(temp);
  }
}
