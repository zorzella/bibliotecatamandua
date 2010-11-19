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
  private final BulkItemUploadPanel bulkItemUploadPanel = new BulkItemUploadPanel(this);
  private final Panel menuPanel;
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
    result.add(buildButton(this.lendingPanel));
    result.add(buildButton(this.newMemberPanel));
    result.add(buildButton(this.newItemPanel));
    result.add(buildButton(this.bulkItemUploadPanel));
    result.add(buildReloadButton());
    return result;
  }

  private Label buildButton(final FullPanel fullPanel) {
    Label result = new Label(fullPanel.getName());
    result.setStyleName("menu-item");
    ClickHandler clickHandler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        makePanelVisible(fullPanel);
      }
    };
    result.addClickHandler(clickHandler);
    return result;
  }

  private Label buildReloadButton() {
    Label reloadButton = new Label("Reload");
    reloadButton.setStyleName("menu-item");
    ClickHandler reloadHandler = new ClickHandler() {
      public void onClick(ClickEvent event) {
        lendingPanel.reloadMembers(null);
        makePanelVisible(lendingPanel);
      }
    };
    reloadButton.addClickHandler(reloadHandler);
    return reloadButton;
  }

  public MainPanel(MemberServiceAsync memberService) {
    initWidget(mainPanel);
    mainPanel.add(messageBox);
    lendingPanel = new LendingPanel(this, memberService, buildMenuActivator());
    mainPanel.add(lendingPanel);
    mainPanel.add(newMemberPanel);
    mainPanel.add(newItemPanel);
    mainPanel.add(bulkItemUploadPanel);

    this.menuPanel = buildMenu();
    mainPanel.add(menuPanel);
    
    makePanelVisible(this.lendingPanel);
    this.memberService = memberService;
  }
  
  private static Label buildMessageBox() {
    Label result = new Label();
    result.setStyleName("message");
    return result;
  }
  
  private void makeAllInvisible() {
    lendingPanel.setVisible(false);
    newMemberPanel.setVisible(false);
    newItemPanel.setVisible(false);
    menuPanel.setVisible(false);
    bulkItemUploadPanel.setVisible(false);
  }

  private void makeMenuVisible() {
    makeAllInvisible();
    menuPanel.setVisible(true);
  }

  private void makePanelVisible(FullPanel fullPanel) {
    makeAllInvisible();
    fullPanel.payload().setVisible(true);
    fullPanel.clear();
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

  public void makeLendingVisible() {
    makePanelVisible(this.lendingPanel);
  }
}