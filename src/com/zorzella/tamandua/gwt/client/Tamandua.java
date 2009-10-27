package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Tamandua implements EntryPoint {

  private static final class MembersDropDownCallback extends 
      NaiveAsyncCallback<Collection<Member>> {
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
    public void onSuccess(Collection<Member> members) {
      membersDropDown.setMembers(members);
      membersDropDown.refresh();
      memberService.getFancySortedItems(sortedItemsCallback);
    }
  }
  
  private static final class MembersDropDown extends ListBox {

    private Collection<Member> members;
    private final Map<Long, String> memberIdToCodeMap = new HashMap<Long, String>();

    public MembersDropDown() {
      addItem("");
    }
    
    public void setMembers(Collection<Member> members) {
      this.members = members;
      for (Member member : members) {
        memberIdToCodeMap.put(member.getId(), member.getCodigo());
      }
    }

    public String idToCode(Long id) {
      return memberIdToCodeMap.get(id);
    }

    public String idToCode(String id) {
      return memberIdToCodeMap.get(Long.valueOf(id));
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

  private static final class ActivityTable extends FlowPanel {
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
  
  private static final class SortedItemsCallback 
      extends NaiveAsyncCallback<ItemBundle> {
    
    private final class ReturnItemCallback extends NaiveAsyncCallback<Void> {

      private Label returned;

      public ReturnItemCallback(String memberCode, Item item) {
        this.returned = new Label("[" + memberCode + "] returned: " + item.getTitulo());
      }
      
      @Override
      public void onSuccess(Void result) {
        activityTable.addItem(returned);
      }
    }

    private final class BorrowItemCallback extends NaiveAsyncCallback<Void> {

      private Label borrowed;
      
      public BorrowItemCallback(String memberCode, Item item) {
        this.borrowed = new Label("[" + memberCode + "] borrowed: " + item.getTitulo());
      }

      @Override
      public void onSuccess(Void result) {
        activityTable.addItem(borrowed);
      }
    }

    private final Panel availableItemListWidget;
    private final Panel borrowedItemListWidget;
    private final MembersDropDown membersDropDown;
    private final ActivityTable activityTable;
    private final MemberServiceAsync memberService;

    private ItemBundle itemBundle;

    private SortedItemsCallback(
        Panel availableItemListWidget, 
        Panel borrowedItemListWidget, 
        MembersDropDown membersDropDown, 
        ActivityTable activityTable, 
        MemberServiceAsync memberService) {
      this.availableItemListWidget = availableItemListWidget;
      this.borrowedItemListWidget = borrowedItemListWidget;
      this.membersDropDown = membersDropDown;
      this.activityTable = activityTable;
      this.memberService = memberService;
    }

    public void onSuccess(ItemBundle itemBundle) {
      this.itemBundle = itemBundle;
      this.refresh();
    }
    
    public void refresh() {
      borrowedItemListWidget.clear();
      availableItemListWidget.clear();
      String selectedMember = selectedMember();
      for (Item item : itemBundle.getBorrowed()) {
        if (selectedMember.equals("")) {
          Label label = new Label(
              membersDropDown.idToCode(item.getParadeiro()) + "-" + item.getTitulo());
          label.setStyleName("whisper");
          borrowedItemListWidget.add(label);
        } else if (item.getParadeiro().toString().equals(selectedMember)) {
          Widget temp = buildBorrowedItemWidget(item);
          borrowedItemListWidget.add(temp);
        }
      }
      for (Item item : itemBundle.getAvailable()) {
        Widget temp = buildAvailableItemWidget(item);
        availableItemListWidget.add(temp);
      }
    }

    private Widget buildAvailableItemWidget(final Item item) {
      final Label result = new Label(item.getTitulo());
      result.setStyleName("entry-row");
      ClickHandler clickHandler = new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          String memberCode = selectedMember();
          if (memberCode.trim().equals("")) {
            return;
          }
          AsyncCallback<Void> borrowItemCallback = new BorrowItemCallback(membersDropDown.idToCode(memberCode), item);
          memberService.borrowItem(memberCode, item, borrowItemCallback);
          result.setText(item.getTitulo() + " - borrowing");
          availableItemsHandlerRegistrationMap.remove(item).removeHandler();
          //TODO: install new one (to undo)
        }
      };
      availableItemsHandlerRegistrationMap.put(item, result.addClickHandler(clickHandler));
      return result;
    }

    private String selectedMember() {
      return membersDropDown.getValue(membersDropDown.getSelectedIndex());
    }

    private final Map<Item, HandlerRegistration> borrowedItemsHandlerRegistrationMap = 
      new HashMap<Item, HandlerRegistration>();
    
    private final Map<Item, HandlerRegistration> availableItemsHandlerRegistrationMap = 
      new HashMap<Item, HandlerRegistration>();
    
    private Label buildBorrowedItemWidget(final Item item) {
      final Label result = new Label(item.getTitulo());
      result.setStyleName("entry-row");
      ClickHandler clickHandler = new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          String memberCode = selectedMember();
          AsyncCallback<Void> returnItemCallback = new ReturnItemCallback(membersDropDown.idToCode(memberCode), item);
          memberService.returnItem(memberCode, item, returnItemCallback);
          result.setText(item.getTitulo() + " - returning");
          borrowedItemsHandlerRegistrationMap.remove(item).removeHandler();
          //TODO: install new one (to undo)
        }
      };
      borrowedItemsHandlerRegistrationMap.put(item, result.addClickHandler(clickHandler));
      return result;
    }
  }

  private Panel mainPanel = new FlowPanel();

//  @Override
  public void onModuleLoad() {

    final MemberServiceAsync memberService = GWT.create(MemberService.class);

    AsyncCallback<Void> callback = new AsyncCallback<Void>() {
      
      @Override
      public void onSuccess(Void result) {
        foo(memberService);
      }
      
      @Override
      public void onFailure(Throwable caught) {}
    };
    
    memberService.adminOrDie(callback);
  }

  void foo(MemberServiceAsync memberService) {
    final Panel borrowedItemsTable = new FlowPanel();
    final TextBox toBorrow = new TextBox();
    final Panel availableItemsTable = new FlowPanel();
    final ActivityTable activityTable = new ActivityTable();
    final MembersDropDown membersDropDown = new MembersDropDown();
    
    final SortedItemsCallback sortedItemsCallback =
      new SortedItemsCallback(
          availableItemsTable, 
          borrowedItemsTable, 
          membersDropDown, 
          activityTable, 
          memberService);

    
    
    final AsyncCallback<Collection<Member>> sortedMembersCallback = 
      new MembersDropDownCallback(membersDropDown, memberService, sortedItemsCallback);
    memberService.getSortedMembers(sortedMembersCallback);
    
	final CurrentMemberChangeHandler memberChangeHandler = 
      new CurrentMemberChangeHandler(sortedItemsCallback, memberService, activityTable);
    membersDropDown.addChangeHandler(memberChangeHandler);

    mainPanel.add(membersDropDown);
    mainPanel.add(borrowedItemsTable);
    mainPanel.add(toBorrow);
    mainPanel.add(availableItemsTable);
    mainPanel.add(activityTable);
    
    RootPanel.get("list").add(mainPanel);

  }

}
