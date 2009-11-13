package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class GwtUtil {

	static Label label(String text, String styleName) {
	  Label result = new Label(text);
	  result.setStyleName(styleName);
	  return result;
	}

	static Panel div(String styleName) {
	  Panel result = new FlowPanel();
	  result.setStyleName(styleName);
	  return result;
	}

}
