package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gen2.picker.client.SliderBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

public class Tamandua implements EntryPoint {

  private static final class MembersDropDownCallback extends 
      NaiveAsyncCallback<SortedSet<Member>> {
    private final MembersDropDown membersDropDown;
    private final MemberServiceAsync memberService;
    private final SortedItemsCallback sortedItemsCallback;

    private MembersDropDownCallback(
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
      //TODO
      membersDropDown.setSelectedIndex(2);
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
  }

  private final class CurrentMemberChangeHandler implements ChangeHandler {
    private final SortedItemsCallback sortedItemsCallback;
    private final MemberServiceAsync memberService;
    private final ActivityTable activityTable;

    private CurrentMemberChangeHandler(
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

    private SortedItemsCallback(
        Panel availableItemListWidget, 
        Panel borrowedItemListWidget, 
        MembersDropDown membersDropDown, 
        ActivityTable activityTable, 
        MemberServiceAsync memberService, 
        SliderBar sliderBar) {
      this.availableItemListWidget = availableItemListWidget;
      this.borrowedItemListWidget = borrowedItemListWidget;
      this.membersDropDown = membersDropDown;
      this.activityTable = activityTable;
      this.memberService = memberService;
      this.sliderBar = sliderBar;
    }

    public void onSuccess(ItemBundle itemBundle) {
      this.itemBundle = itemBundle;
      this.itemWidgetBundle = new ItemWidgetBundle(
          membersDropDown, 
          memberService, 
          activityTable, 
          itemBundle);
      this.refresh();
//      Window.scrollTo(0, 1);
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

  private Panel mainPanel = new FlowPanel();

//  @Override
  public void onModuleLoad() {

//    iphone();
    
    final MemberServiceAsync memberService = GWT.create(MemberService.class);

    AsyncCallback<Void> callback = new AsyncCallback<Void>() {
      
//      @Override
      public void onSuccess(Void result) {
        adminOk(memberService);
      }
      
//      @Override
      public void onFailure(Throwable caught) {
        throw new IllegalArgumentException(caught);
      }
    };
    
    memberService.adminOrDie(callback);
  }

  public static final int SCROLL_PANEL_HEIGH = 320;
  
  void adminOk(MemberServiceAsync memberService) {
    final Panel borrowedItemsTable = new FlowPanel();
    final Widget separator = new HTML("<hr/>");
    final Panel availableItemsTable = new FlowPanel();
    final ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setHeight(SCROLL_PANEL_HEIGH + "px");
    scrollPanel.add(availableItemsTable);
    final ActivityTable activityTable = new ActivityTable();
    final MembersDropDown membersDropDown = new MembersDropDown();
    
    SliderBar sliderBar = new SliderBar(0.0, 100.0);

    final SortedItemsCallback sortedItemsCallback =
      new SortedItemsCallback(
          availableItemsTable, 
          borrowedItemsTable, 
          membersDropDown, 
          activityTable, 
          memberService,
          sliderBar);
    
    final AsyncCallback<SortedSet<Member>> sortedMembersCallback = 
      new MembersDropDownCallback(membersDropDown, memberService, sortedItemsCallback);
    memberService.getSortedMembers(sortedMembersCallback);
    
	final CurrentMemberChangeHandler memberChangeHandler = 
      new CurrentMemberChangeHandler(sortedItemsCallback, memberService, activityTable);
    membersDropDown.addChangeHandler(memberChangeHandler);

    mainPanel.add(activityTable);
    mainPanel.add(membersDropDown);
    mainPanel.add(borrowedItemsTable);
    
    sliderBar.setStepSize(5.0);
//    sliderBar.setNumTicks(10);
//    sliderBar.setNumLabels(5);
    mainPanel.add(sliderBar);
    
    ValueChangeHandler<Double> handler = new ValueChangeHandler<Double>() {
      
      public void onValueChange(ValueChangeEvent<Double> event) {
        scrollPanel.setScrollPosition(event.getValue().intValue());
      }
    };
    sliderBar.addValueChangeHandler(handler);
    
//    mainPanel.add(separator);
    mainPanel.add(scrollPanel);
//    mainPanel.add(availableItemsTable);
    
    RootPanel.get("list").add(mainPanel);

  }

//  public native void iphone() /*-{
//    window.scrollTo(0, 1);
//  }-*/;

  //  public native void iphone() /*-{
//    if (navigator.userAgent.indexOf('iPhone') != -1) {
//      addEventListener("load", function() {
//          setTimeout(hideURLbar, 0);
//      }, false);
//    }
//
//    function hideURLbar() {
//      window.scrollTo(0, 1);
//    }
//  }-*/; 
}
