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

  public void addItemWarning(Label item) {
    addItemInternal(item, Styles.ENTRY_ROW_PLUS_WARNING);
  }
  
  public void addItemFail(Label item) {
    addItemInternal(item, Styles.ENTRY_ROW_PLUS_ERROR);
  }
  
  public void addItemSuccess(Label item) {
    addItemInternal(item, Styles.ENTRY_ROW_PLUS_READ);
  }
  
  private void addItemInternal(Label item, String style) {
    item.setStyleName(style);
    add(item);
    dirty = true;
  }
}