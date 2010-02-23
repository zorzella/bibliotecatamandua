// Copyright 2010 Google Inc. All Rights Reserved.

package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public final class ActivityTable extends FlowPanel {
  private boolean dirty;

  /**
   * Test and set, relying on single-threadness
   */
  public boolean clearDirty() {
    boolean result = dirty;
    dirty = false;
    return result;
  }

  public void addItem(Label item) {
    item.setStyleName("entry-row read");
    add(item);
    dirty = true;
  }
}