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
import com.google.gwt.user.client.ui.Widget;

final class NewMemberPanel extends Composite implements FullPanel {

  private final FlowPanel result = new FlowPanel();
  private final TextBox parentNameInput = new TextBox();
  private final TextBox childFirstNameInput = new TextBox();
  private final TextBox childLastNameInput = new TextBox();
  private final TextBox codeInput = new TextBox();
  private final TextBox emailInput = new TextBox();
  private final ListBox commonEmails = new ListBox();

  public void clear() {
    parentNameInput.setText("");
    childFirstNameInput.setText("");
    childLastNameInput.setText("");
    codeInput.setText("");
    emailInput.setText("");
    commonEmails.setSelectedIndex(0);
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

    commonEmails.addItem("");
    commonEmails.addItem("@gmail.com");
    commonEmails.addItem("@hotmail.com");
    commonEmails.addItem("@yahoo.com");
    commonEmails.addChangeHandler(buildCommonEmailsChangeHandler());
    result.add(commonEmails);

    Label ok = new Label(Labels.OK);
    ok.setStyleName(Styles.PREV_PAGE);
    ok.addClickHandler(buildOkClickHandler(mainPanel));
    result.add(ok);

    Label cancel = new Label(Labels.CANCEL);
    cancel.setStyleName(Styles.NEXT_PAGE);
    cancel.addClickHandler(buildCancelClickHandler(mainPanel));
    result.add(cancel);
  }

  private ChangeHandler buildCommonEmailsChangeHandler() {
    ChangeHandler handler = new ChangeHandler() {

      public void onChange(ChangeEvent event) {
        String value = emailInput.getValue();
        int indexOfAtSymbol = value.indexOf('@');
if (indexOfAtSymbol > -1) {
          value = value.substring(0, indexOfAtSymbol);
        }
        value = value + commonEmails.getValue(commonEmails.getSelectedIndex());
        emailInput.setValue(value);
      }
    };
    return handler;
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
    ClickHandler handler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        final String code = codeInput.getValue().trim();
        if (code.equals("")) {
          mainPanel.showMessage("Code is required.");
          return;
        }
        if (mainPanel.lendingPanel.memberExistsWithCode(code)) {
          mainPanel.showMessage("Code '" + code + "' is already in use.");
          return;
        }
        
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {

          public void onSuccess(Void result) {
            mainPanel.lendingPanel.reloadMembers(code);
            mainPanel.makeLendingVisible();
            mainPanel.clearMessage();
          }

          public void onFailure(Throwable caught) {
            mainPanel.showMessage("Failed!");
          }
        };
        mainPanel.memberService.createNewMember(
            parentNameInput.getValue(), 
            childFirstNameInput.getValue(), 
            childLastNameInput.getValue(), 
            code, 
            emailInput.getValue(), 
            callback);
      }
    };
    return handler;
  }

  public String getName() {
    return "New Member";
  }

  public Widget payload() {
    return this;
  }
}