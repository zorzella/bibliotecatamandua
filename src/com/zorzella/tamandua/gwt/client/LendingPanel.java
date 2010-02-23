package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.gwt.client.Tamandua.CurrentMemberChangeHandler;
import com.zorzella.tamandua.gwt.client.Tamandua.MembersDropDownCallback;
import com.zorzella.tamandua.gwt.client.Tamandua.SortedItemsCallback;

import java.util.Collection;

public final class LendingPanel extends Composite {

  public static final int SCROLL_AMOUNT = Tamandua.SCROLL_PANEL_HEIGH - 30;
  private final MemberServiceAsync memberService;
  private final Widget menuButton;

  private final Panel lendingPanel = new FlowPanel();
  private final MembersDropDown membersDropDown = new MembersDropDown();
  private final Panel borrowedItemsTable = new FlowPanel();
  private final Panel availableItemsTable = new FlowPanel();
  private final ScrollPanel scrollPanel = buildScrollPanel();
  private final ActivityTable activityTable = new ActivityTable();
  private final SortedItemsCallback sortedItemsCallback;


  public LendingPanel(MemberServiceAsync memberService, Widget menuButton) {
    initWidget(lendingPanel);
    this.memberService = memberService;
    this.menuButton = menuButton;

    sortedItemsCallback =
      new SortedItemsCallback(
          availableItemsTable, 
          borrowedItemsTable, 
          membersDropDown, 
          activityTable, 
          memberService,
          lendingPanel);

    final CurrentMemberChangeHandler memberChangeHandler = 
      new CurrentMemberChangeHandler(sortedItemsCallback, memberService, activityTable);
    membersDropDown.addChangeHandler(memberChangeHandler);
  }

  public boolean memberExistsWithCode(String code) {
    return membersDropDown.memberExistsWithCode(code);
  }

  void adminOk(MemberServiceAsync memberService, MainPanel mainPanel) {
    reloadMembers(null);

    lendingPanel.add(activityTable);
    lendingPanel.add(membersDropDown);
    lendingPanel.add(borrowedItemsTable);

    FlowPanel paginationBar = new FlowPanel();
    paginationBar.setStyleName("pagination");

    ValueChangeHandler<Double> handler = new ValueChangeHandler<Double>() {
      public void onValueChange(ValueChangeEvent<Double> event) {
        scrollPanel.setScrollPosition(event.getValue().intValue());
      }
    };

    Label prevPageButton = new Label("<");
    prevPageButton.setStyleName("prev-page");
    ClickHandler prevPageHandler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        int scrollPosition = scrollPanel.getScrollPosition();
        scrollPosition -= SCROLL_AMOUNT;
        scrollPanel.setScrollPosition(scrollPosition);
      }
    };
    prevPageButton.addClickHandler(prevPageHandler);

    Label nextPageButton = new Label(">");
    nextPageButton.setStyleName("next-page");
    ClickHandler nextPageHandler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        int scrollPosition = scrollPanel.getScrollPosition();
        scrollPosition += SCROLL_AMOUNT;
        scrollPanel.setScrollPosition(scrollPosition);
      }
    };
    nextPageButton.addClickHandler(nextPageHandler);

    paginationBar.add(prevPageButton);
    //      paginationBar.add(sliderBar);
    menuButton.setStyleName("menu-button");
    paginationBar.add(menuButton);
    paginationBar.add(nextPageButton);

    lendingPanel.add(paginationBar);

    lendingPanel.add(scrollPanel);
  }

  private ScrollPanel buildScrollPanel() {
    final ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setHeight(Tamandua.SCROLL_PANEL_HEIGH + "px");
    scrollPanel.add(availableItemsTable);
    return scrollPanel;
  }

  public void reloadMembers(String code) {
    final AsyncCallback<Collection<Member>> sortedMembersCallback = 
      new MembersDropDownCallback(membersDropDown, memberService, sortedItemsCallback, code);
    memberService.getSortedMembers(sortedMembersCallback);
  }
}