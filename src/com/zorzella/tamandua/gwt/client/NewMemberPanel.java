package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

final class NewMemberPanel extends Composite {

  private final FlowPanel result = new FlowPanel();
  private final TextBox parentNameInput = new TextBox();
  private final TextBox childFirstNameInput = new TextBox();
  private final TextBox childLastNameInput = new TextBox();
  private final TextBox codeInput = new TextBox();
  private final TextBox emailInput = new TextBox();

  void clear() {
    parentNameInput.setText("");
    childFirstNameInput.setText("");
    childLastNameInput.setText("");
    codeInput.setText("");
    emailInput.setText("");
  }
  
  public NewMemberPanel(final MainPanel mainPanel) {
    initWidget(result);

    result.add(new Label("Parent name"));
    result.add(parentNameInput);

    result.add(new Label("Child First Name"));
    result.add(childFirstNameInput);

    result.add(new Label("Child Last Name"));
    result.add(childLastNameInput);

    result.add(new Label("Code"));
    result.add(codeInput);

    result.add(new Label("Email"));
    result.add(emailInput);

    final ListBox commonEmails = new ListBox();
    commonEmails.addItem("");
    commonEmails.addItem("@gmail.com");
    commonEmails.addItem("@hotmail.com");
    commonEmails.addItem("@yahoo.com");
    {
      ChangeHandler handler = new ChangeHandler() {

        public void onChange(ChangeEvent event) {
          String value = emailInput.getValue();
          value = value + commonEmails.getValue(commonEmails.getSelectedIndex());
          emailInput.setValue(value);
        }
      };
      commonEmails.addChangeHandler(handler);
    }
    result.add(commonEmails);

    Label ok = new Label("Ok");
    ok.setStyleName("prev-page");
    {
      ClickHandler handler = new ClickHandler() {

        public void onClick(ClickEvent event) {
          final String code = codeInput.getValue().trim();
          if (code.equals("")) {
            mainPanel.message.setText("Code is required.");
            return;
          }
          if (mainPanel.lendingPanel.memberExistsWithCode(code)) {
            mainPanel.message.setText("Code '" + code + "' is already in use.");
            return;
          }
          
          AsyncCallback<Void> callback = new AsyncCallback<Void>() {

            public void onSuccess(Void result) {
              mainPanel.lendingPanel.reloadMembers(code);
              mainPanel.makeLendingVisible();
              mainPanel.message.setText("");
            }

            public void onFailure(Throwable caught) {
              mainPanel.message.setText("Failed!");
            }
          };
          mainPanel.memberService.createNew(
              parentNameInput.getValue(), 
              childFirstNameInput.getValue(), 
              childLastNameInput.getValue(), 
              code, 
              emailInput.getValue(), 
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