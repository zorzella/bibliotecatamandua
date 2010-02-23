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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


final class MainPanel extends Composite {

  private final Panel mainPanel = new FlowPanel();
  
  private final Label message = new Label();
  private final LendingPanel lendingPanel;
  private final NewMemberPanel newMemberPanel = buildNewMemberPanel();
  private final Panel menuPanel = buildMenu();
  private final MemberServiceAsync memberService;

  
  private NewMemberPanel buildNewMemberPanel () {
    return new NewMemberPanel();
  }
  
  private final class NewMemberPanel extends Composite {

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
    
    public NewMemberPanel() {
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
            String code = codeInput.getValue().trim();
            if (code.equals("")) {
              message.setText("Code is required.");
              return;
            }
            if (lendingPanel.memberExistsWithCode(code)) {
              message.setText("Code is already in use.");
              return;
            }
            
            AsyncCallback<Void> callback = new AsyncCallback<Void>() {

              public void onSuccess(Void result) {
                lendingPanel.reloadMembers();
                makeLendingVisible();
              }

              public void onFailure(Throwable caught) {
                message.setText("Failed!");
              }
            };
            memberService.createNew(
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
            makeLendingVisible();
          }
        };
        cancel.addClickHandler(handler);
      }
      result.add(cancel);
    }
  }

  private FlowPanel buildMenu() {
    FlowPanel result = new FlowPanel();
    Label lendingButton = new Label("Lending");
    lendingButton.setStyleName("menu-item");
    {
      ClickHandler lendingHandler = new ClickHandler() {
        
        public void onClick(ClickEvent event) {
          makeLendingVisible();
        }
      };
      lendingButton.addClickHandler(lendingHandler);
    }
    result.add(lendingButton);
    
    Label newMemberButton = new Label("New Member");
    newMemberButton.setStyleName("menu-item");
    {
      ClickHandler newMemberHandler = new ClickHandler() {
        
        public void onClick(ClickEvent event) {
          makeNewMemberVisibleAndClear();
        }
      };
      newMemberButton.addClickHandler(newMemberHandler);
    }
    result.add(newMemberButton);
    return result;
  }

  public MainPanel(MemberServiceAsync memberService) {
    initWidget(mainPanel);
    Widget menuButton = buildMenuActivator();
//    mainPanel.add(menuButton);
    lendingPanel = new LendingPanel(memberService, menuButton);
    mainPanel.add(message);
    message.setStyleName("message");
    mainPanel.add(lendingPanel);
    mainPanel.add(newMemberPanel);
    mainPanel.add(menuPanel);
    makeLendingVisible();
    this.memberService = memberService;
  }
  
  public LendingPanel getLendingPanel() {
    return lendingPanel;
  }

  private void makeAllInvisible() {
    lendingPanel.setVisible(false);
    newMemberPanel.setVisible(false);
    menuPanel.setVisible(false);
  }

  public void makeMenuVisible() {
    makeAllInvisible();
    menuPanel.setVisible(true);
  }

  public void makeLendingVisible() {
    makeAllInvisible();
    lendingPanel.setVisible(true);
  }

  public void makeNewMemberVisibleAndClear() {
    makeAllInvisible();
    newMemberPanel.clear();
    newMemberPanel.setVisible(true);
  }
  
  private Widget buildMenuActivator() {
    Label result = new Label("menu");
    ClickHandler handler = new ClickHandler() {
      
      public void onClick(ClickEvent event) {
        makeMenuVisible();
      }
    };
    result.addClickHandler(handler);
    return result;
  }
}