package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

final class NewItemPanel extends Composite {

  private final FlowPanel result = new FlowPanel();
  private final TextBox itemNameInput = new TextBox();
  private final TextBox authorNameInput = new TextBox();
  private final TextBox isbnInput = new TextBox();

  void clear() {
    itemNameInput.setText("");
    authorNameInput.setText("");
    isbnInput.setText("");
  }
  
  public NewItemPanel(final MainPanel mainPanel) {
    initWidget(result);

    result.add(new Label("Item name"));
    result.add(itemNameInput);

    result.add(new Label("Author Name"));
    result.add(authorNameInput);

    result.add(new Label("ISBN"));
    result.add(isbnInput);
    
    Label ok = new Label("Ok");
    ok.setStyleName("prev-page");
    
    {
      ClickHandler handler = new ClickHandler() {

        public void onClick(ClickEvent event) {
          
          AsyncCallback<Void> callback = new AsyncCallback<Void>() {

            public void onSuccess(Void result) {
              mainPanel.lendingPanel.reloadItems();
              mainPanel.makeLendingVisible();
              mainPanel.clearMessage();
            }

            public void onFailure(Throwable caught) {
              mainPanel.showMessage("Failed!");
            }
          };
          mainPanel.memberService.createNewItem(
              itemNameInput.getValue(), 
              authorNameInput.getValue(), 
              isbnInput.getValue(),
              callback);
        }
      };
      ok.addClickHandler(handler);
    }
    result.add(ok);

    Label cancel = new Label("Cancel");
    cancel.setStyleName("next-page");
    {
      ClickHandler handler = new ClickHandler() {

        public void onClick(ClickEvent event) {
          mainPanel.makeLendingVisible();
        }
      };
      cancel.addClickHandler(handler);
    }
    result.add(cancel);
  }
}