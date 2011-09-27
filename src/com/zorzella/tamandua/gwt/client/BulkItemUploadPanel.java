package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

final class BulkItemUploadPanel extends Composite implements FullPanel {

  private final FlowPanel result = new FlowPanel();
  private final TextBox itemNameInput = new TextBox();
  private final TextBox authorNameInput = new TextBox();
  private final TextArea csvDataInput = new TextArea();

  public void clear() {
    itemNameInput.setText("");
    authorNameInput.setText("");
    csvDataInput.setText("");
  }
  
  public BulkItemUploadPanel(final MainPanel mainPanel) {
    initWidget(result);

//    result.add(new Label("Item name"));
//    result.add(itemNameInput);
//
//    result.add(new Label("Author Name"));
//    result.add(authorNameInput);

    result.add(new Label("csv-data"));
    result.add(csvDataInput);
    
    Label ok = new Label(Labels.OK);
    ok.setStyleName(Styles.PREV_PAGE);
    ok.addClickHandler(buildOkClickHandler(mainPanel));
    result.add(ok);

    Label cancel = new Label(Labels.CANCEL);
    cancel.setStyleName(Styles.NEXT_PAGE);
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
        mainPanel.memberService.bulkUpload(csvDataInput.getText(), callback);
      }
    };
    return result;
  }

  public String getName() {
    return "Bulk Item Upload (under dev)";
  }

  public Widget payload() {
    return this;
  }
}