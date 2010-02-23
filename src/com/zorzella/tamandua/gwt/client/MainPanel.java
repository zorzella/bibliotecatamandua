package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


final class MainPanel extends Composite {

  private final Panel mainPanel = new FlowPanel();
  
  final Label message = new Label();
  final LendingPanel lendingPanel;
  private final NewMemberPanel newMemberPanel = buildNewMemberPanel();
  private final Panel menuPanel = buildMenu();
  final MemberServiceAsync memberService;

  
  private NewMemberPanel buildNewMemberPanel () {
    return new NewMemberPanel(this);
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

    Label reloadButton = new Label("Reload");
    reloadButton.setStyleName("menu-item");
    {
      ClickHandler reloadHandler = new ClickHandler() {
        
        public void onClick(ClickEvent event) {
          lendingPanel.reloadMembers(null);
          makeLendingVisible();
        }
      };
      reloadButton.addClickHandler(reloadHandler);
    }
    result.add(reloadButton);

    
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