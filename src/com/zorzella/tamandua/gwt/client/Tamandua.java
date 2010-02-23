package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.gen2.picker.client.SliderBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

public class Tamandua implements EntryPoint {

  public static final int SCROLL_PANEL_HEIGH = 380;

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
      NaiveAsyncCallback<SortedSet<Member>> {
    private final MembersDropDown membersDropDown;
    private final MemberServiceAsync memberService;
    private final SortedItemsCallback sortedItemsCallback;

    MembersDropDownCallback(
        MembersDropDown membersDropDown, 
        MemberServiceAsync memberService, 
        SortedItemsCallback sortedItemsCallback) {
      this.membersDropDown = membersDropDown;
      this.memberService = memberService;
      this.sortedItemsCallback = sortedItemsCallback;
    }

    //      @Override
    public void onSuccess(SortedSet<Member> members) {
      membersDropDown.setMembers(members);
      membersDropDown.refresh();
      memberService.getFancySortedItems(sortedItemsCallback);
    }
  }
  
  public static final class MembersDropDown extends ListBox {

    private SortedSet<Member> members;
    private final Map<Long, Member> memberIdToCodeMap = new HashMap<Long, Member>();

    public MembersDropDown() {
      addItem("");
    }
    
    public void setMembers(SortedSet<Member> members) {
      this.members = members;
      for (Member member : members) {
        memberIdToCodeMap.put(member.getId(), member);
      }
    }

    public Member getSelectedMember() {
      int index = getSelectedIndex();
      String value = getValue(index);
      if (value.equals("")) {
        return null;
      }
      return memberIdToCodeMap.get(Long.valueOf(value));
    }
    
    public String idToCode(Long id) {
      return memberIdToCodeMap.get(id).getCodigo();
    }

    public void refresh() {
      clear();
      addItem("");
      for (Member member : members) {
        addItem(
          member.getCodigo() + " - " + TamanduaUtil.nome(member),
          member.getId().toString());
      }
    }
    
    public boolean memberExistsWithCode(String code) {
      for (Member member : members) {
        if (member.getCodigo().equalsIgnoreCase(code.trim())) {
          return true;
        }
      }
      return false;
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

  public static final class ActivityTable extends FlowPanel {
    private boolean dirty;

    /**
     * Test and set, relying on single-threadness
     */
    public boolean clearDirty() {
      boolean result = dirty;
      dirty = false;
      return result;
    }

    public void addItem(Label item) {
      item.setStyleName("entry-row read");
      add(item);
      dirty = true;
    }
  }
  
  public static final class SortedItemsCallback 
      extends NaiveAsyncCallback<ItemBundle> {
    
    private final Panel availableItemListWidget;
    private final Panel borrowedItemListWidget;
    private final MembersDropDown membersDropDown;
    private final ActivityTable activityTable;
    private final MemberServiceAsync memberService;
    private final SliderBar sliderBar;

    private ItemWidgetBundle itemWidgetBundle;
    private ItemBundle itemBundle;
    private final Panel lendingPanel;

    SortedItemsCallback(
        Panel availableItemListWidget, 
        Panel borrowedItemListWidget, 
        MembersDropDown membersDropDown, 
        ActivityTable activityTable, 
        MemberServiceAsync memberService, 
        SliderBar sliderBar, 
        Panel lendingPanel) {
      this.availableItemListWidget = availableItemListWidget;
      this.borrowedItemListWidget = borrowedItemListWidget;
      this.membersDropDown = membersDropDown;
      this.activityTable = activityTable;
      this.memberService = memberService;
      this.sliderBar = sliderBar;
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
      // 10 is a fudge
      sliderBar.setMaxValue(10 + availableItemListWidget.getOffsetHeight() - SCROLL_PANEL_HEIGH);
    }
  }

  private MainPanel mainPanel;

  //  @Override
  public void onModuleLoad() {

    final MemberServiceAsync memberService = GWT.create(MemberService.class);
    
    mainPanel = new MainPanel(memberService);
    RootPanel.get("list").add(mainPanel);

    AsyncCallback<Void> callback = new AdminOrDieCallback(memberService, mainPanel);
    
    memberService.adminOrDie(callback);
  }
}
