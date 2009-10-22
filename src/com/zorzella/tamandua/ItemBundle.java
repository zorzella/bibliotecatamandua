// Copyright 2009 Google Inc. All Rights Reserved.

package com.zorzella.tamandua;

import java.io.Serializable;
import java.util.Collection;

public final class ItemBundle implements Serializable {

  private Collection<Item> available;
  private Collection<Item> borrowed;

  /**
   * For GWT
   */
  ItemBundle() {}
  
  public ItemBundle(
      Collection<Item> available, 
      Collection<Item> borrowed) {
    this.available = available;
    this.borrowed = borrowed;
  }

  public Collection<Item> getAvailable() {
    return available;
  }
  
  public Collection<Item> getBorrowed() {
    return borrowed;
  }
}