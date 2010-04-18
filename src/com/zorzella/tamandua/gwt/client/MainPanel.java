package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Skeleton that contains a message box in the top, and a collection 
 * of panels, only one of each visible at a time.
 * 
 * @author zorzella
 */
final class MainPanel extends Composite {

  private final Panel mainPanel = new FlowPanel();
  
  private final Label messageBox = buildMessageBox();

  final LendingPanel lendingPanel;
  private final NewMemberPanel newMemberPanel = buildNewMemberPanel();
  private final NewItemPanel newItemPanel = buildNewItemPanel();
  private final Panel menuPanel = buildMenu();
  final MemberServiceAsync memberService;

  public void showMessage(String message) {
    messageBox.setText(message);
  }

  public void clearMessage() {
    messageBox.setText("");
  }
  
  private NewMemberPanel buildNewMemberPanel () {
    return new NewMemberPanel(this);
  }

  private NewItemPanel buildNewItemPanel () {
    return new NewItemPanel(this);
  }
  
  private FlowPanel buildMenu() {
    FlowPanel result = new FlowPanel();
    result.add(buildLendingButton());
    result.add(buildNewMemberButton());
    result.add(buildReloadButton());
    return result;
  }

  private Label buildLendingButton() {
    Label lendingButton = new Label("Lending");
    lendingButton.setStyleName("menu-item");
    ClickHandler lendingHandler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        makeLendingVisible();
      }
    };
    lendingButton.addClickHandler(lendingHandler);
    return lendingButton;
  }

  private Label buildReloadButton() {
    Label reloadButton = new Label("Reload");
    reloadButton.setStyleName("menu-item");
    ClickHandler reloadHandler = new ClickHandler() {
      public void onClick(ClickEvent event) {
        lendingPanel.reloadMembers(null);
        makeLendingVisible();
      }
    };
    reloadButton.addClickHandler(reloadHandler);
    return reloadButton;
  }

  private Label buildNewMemberButton() {
    Label newMemberButton = new Label("New Member");
    newMemberButton.setStyleName("menu-item");
    ClickHandler newMemberHandler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        makeNewMemberVisibleAndClear();
      }
    };
    newMemberButton.addClickHandler(newMemberHandler);
    return newMemberButton;
  }

  private Label buildNewItemButton() {
    Label newItemButton = new Label("New Item");
    newItemButton.setStyleName("menu-item");
    ClickHandler newItemHandler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        makeNewItemVisibleAndClear();
      }
    };
    newItemButton.addClickHandler(newItemHandler);
    return newItemButton;
  }

  public MainPanel(MemberServiceAsync memberService) {
    initWidget(mainPanel);
    mainPanel.add(messageBox);
    lendingPanel = new LendingPanel(this, memberService, buildMenuActivator());
    mainPanel.add(lendingPanel);
    mainPanel.add(newMemberPanel);
    mainPanel.add(menuPanel);
    makeLendingVisible();
    this.memberService = memberService;
  }
  
  public LendingPanel getLendingPanel() {
    return lendingPanel;
  }

  private static Label buildMessageBox() {
    Label result = new Label();
    result.setStyleName("message");
    return result;
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
  
  public void makeNewItemVisibleAndClear() {
    makeAllInvisible();
    newItemPanel.clear();
    newItemPanel.setVisible(true);
  }
  
  private Widget buildMenuActivator() {
    Label result = new Label("menu");
    result.setStyleName("menu-button");
    
    ClickHandler handler = new ClickHandler() {
      public void onClick(ClickEvent event) {
        makeMenuVisible();
      }
    };
    result.addClickHandler(handler);
    return result;
  }
}