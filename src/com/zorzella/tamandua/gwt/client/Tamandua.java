package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

import java.util.Collection;

public class Tamandua implements EntryPoint {

  public static final int SCROLL_PANEL_HEIGH = 380;

  private MainPanel mainPanel;

  private static final class AdminOrDieCallback implements AsyncCallback<Void> {
    private final MemberServiceAsync memberService;
    private final MainPanel mainPanel;

    private AdminOrDieCallback(
        MemberServiceAsync memberService,
        MainPanel mainPanel) {
      this.memberService = memberService;
      this.mainPanel = mainPanel;
    }

    //      @Override
    public void onSuccess(Void result) {
      mainPanel.getLendingPanel().adminOk(memberService, mainPanel);
    }

    //      @Override
    public void onFailure(Throwable caught) {
      throw new IllegalArgumentException(caught);
    }
  
  }
  
  static final class MembersDropDownCallback extends 
      NaiveAsyncCallback<Collection<Member>> {
    private final MembersDropDown membersDropDown;
    private final MemberServiceAsync memberService;
    private final SortedItemsCallback sortedItemsCallback;
    private final String selectedMemberCode;

    /**
     * If {@code selectedMemberCode} is not null, that member will be made the 
     * selected member at the end of the callback.
     */
    MembersDropDownCallback(
        MembersDropDown membersDropDown, 
        MemberServiceAsync memberService, 
        SortedItemsCallback sortedItemsCallback, 
        String selectedMemberCode) {
      this.membersDropDown = membersDropDown;
      this.memberService = memberService;
      this.sortedItemsCallback = sortedItemsCallback;
      this.selectedMemberCode = selectedMemberCode;
    }

    //      @Override
    public void onSuccess(Collection<Member> members) {
      membersDropDown.setMembers(members);
      membersDropDown.refresh();
      if (selectedMemberCode != null) {
        membersDropDown.setSelectedMemberByCode(selectedMemberCode);
      }
      memberService.getFancySortedItems(sortedItemsCallback);
    }
  }
  
  static final class CurrentMemberChangeHandler implements ChangeHandler {
    private final SortedItemsCallback sortedItemsCallback;
    private final MemberServiceAsync memberService;
    private final ActivityTable activityTable;

    CurrentMemberChangeHandler(
        SortedItemsCallback sortedItemsCallback, 
        MemberServiceAsync memberService, 
        ActivityTable activityTable) {
      this.sortedItemsCallback = sortedItemsCallback;
      this.memberService = memberService;
      this.activityTable = activityTable;
    }

    public void onChange(ChangeEvent event) {
      if (activityTable.clearDirty()) {
        memberService.getFancySortedItems(sortedItemsCallback);
      } else {
        sortedItemsCallback.refresh();
      }
    }
  }

  public static final class SortedItemsCallback 
      extends NaiveAsyncCallback<ItemBundle> {
    
    private final Panel availableItemListWidget;
    private final Panel borrowedItemListWidget;
    private final MembersDropDown membersDropDown;
    private final ActivityTable activityTable;
    private final MemberServiceAsync memberService;

    private ItemWidgetBundle itemWidgetBundle;
    private ItemBundle itemBundle;
    private final Panel lendingPanel;

    SortedItemsCallback(
        Panel availableItemListWidget, 
        Panel borrowedItemListWidget, 
        MembersDropDown membersDropDown, 
        ActivityTable activityTable, 
        MemberServiceAsync memberService, 
        Panel lendingPanel) {
      this.availableItemListWidget = availableItemListWidget;
      this.borrowedItemListWidget = borrowedItemListWidget;
      this.membersDropDown = membersDropDown;
      this.activityTable = activityTable;
      this.memberService = memberService;
      this.lendingPanel = lendingPanel;
    }

    public void onSuccess(ItemBundle itemBundle) {
      this.itemBundle = itemBundle;
      this.itemWidgetBundle = new ItemWidgetBundle(
          membersDropDown, 
          memberService, 
          activityTable, 
          itemBundle,
          lendingPanel);
      this.refresh();
      Window.scrollTo(0, 1);
    }
    
    public void refresh() {
      borrowedItemListWidget.clear();
      availableItemListWidget.clear();
      Member selectedMember = membersDropDown.getSelectedMember();
      for (Item item : itemBundle.getBorrowed(selectedMember)) {
        borrowedItemListWidget.add(itemWidgetBundle.getWidgetForBorrowed(item, selectedMember));
      }
      for (Item item : itemBundle.getAvailable()) {
        availableItemListWidget.add(itemWidgetBundle.getWidgetForAvailable(item));
      }
    }
  }

  //  @Override
  public void onModuleLoad() {
    final MemberServiceAsync memberService = GWT.create(MemberService.class);
    
    mainPanel = new MainPanel(memberService);
    RootPanel.get("list").add(mainPanel);

    AsyncCallback<Void> callback = new AdminOrDieCallback(memberService, mainPanel);
    
    memberService.adminOrDie(callback);
  }
}
