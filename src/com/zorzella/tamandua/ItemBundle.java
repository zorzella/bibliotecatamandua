// Copyright 2009 Google Inc. All Rights Reserved.

package com.zorzella.tamandua;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemBundle implements Serializable {

  private Collection<Item> available;
  private Collection<Item> borrowed;
  private Map<Long, List<Item>> borrowMap;

  /**
   * For GWT
   */
  ItemBundle() {}
  
  public ItemBundle(
      Collection<Item> available, 
      Collection<Item> borrowed) {
    this.available = available;
    this.borrowed = borrowed;
    Map<Long, List<Item>> temp = new HashMap<Long, List<Item>>();
    for (Item item : borrowed) {
      Long paradeiro = item.getParadeiro();
      List<Item> list = temp.get(paradeiro);
      if (list == null) {
        //TODO: make immutable
        list = new ArrayList<Item>();
        temp.put(paradeiro, list);
      }
      list.add(item);
    }
    // TODO: make immutable
    this.borrowMap = temp;
  }

  public Collection<Item> getAvailable() {
    return available;
  }
  
  public Collection<Item> getBorrowed() {
    return borrowed;
  }
  
  public Collection<Item> getBorrowed(Long paradeiro) {
    return borrowMap.get(paradeiro);
  }

  @SuppressWarnings("unchecked")
  public Collection<Item> getBorrowed(Member member) {
    if (member == null) {
      return borrowed;
    }
    Collection<Item> result = borrowMap.get(member.getId());
    if (result == null) {
      return Collections.EMPTY_LIST;
    }
    return result;
  }
}