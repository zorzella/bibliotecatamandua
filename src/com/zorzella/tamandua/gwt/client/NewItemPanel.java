package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

final class NewItemPanel extends Composite implements FullPanel {

  private final FlowPanel result = new FlowPanel();
  private final TextBox itemNameInput = new TextBox();
  private final TextBox authorNameInput = new TextBox();
  private final TextBox isbnInput = new TextBox();

  public void clear() {
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
    ok.addClickHandler(buildOkClickHandler(mainPanel));
    result.add(ok);

    Label cancel = new Label("Cancel");
    cancel.setStyleName("next-page");
    cancel.addClickHandler(buildCancelClickHandler(mainPanel));
    result.add(cancel);
  }

  private ClickHandler buildCancelClickHandler(final MainPanel mainPanel) {
    ClickHandler handler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        mainPanel.makeLendingVisible();
      }
    };
    return handler;
  }

  private ClickHandler buildOkClickHandler(final MainPanel mainPanel) {
    ClickHandler result = new ClickHandler() {

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
    return result;
  }

  public String getName() {
    return "New Item";
  }

  public Widget payload() {
    return this;
  }
}