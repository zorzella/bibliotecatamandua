package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * A panel in the {@link MainPanel}.
 * 
 * @author zorzella
 */
public interface FullPanel {

  /**
   * Returns the payload widget, i.e. the the actual panel.
   */
  Widget payload();

  /**
   * A name to show as a menu item to invoke this panel.
   */
  String getName();

  /**
   * "Clears" the panel for future use.
   */
  void clear();
  
}
