package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Tamandua implements EntryPoint {

  private final class MembersDropDownCallback extends 
      NaiveAsyncCallback<Collection<Member>> {
    private final MembersDropDown membersDropDown;

    private MembersDropDownCallback(MembersDropDown membersDropDown) {
      this.membersDropDown = membersDropDown;
    }

    //      @Override
    public void onSuccess(Collection<Member> members) {
      membersDropDown.setMembers(members);
      membersDropDown.refresh();
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

  private static final class ActivityTable extends FlexTable {
    private boolean dirty;

    /**
     * Test and set, relying on single-threadness
     */
    public boolean clearDirty() {
      boolean result = dirty;
      dirty = false;
      return result;
    }

    public void addItem(Widget item) {
      setWidget(getRowCount(), 0, item);
      dirty = true;
    }
  }
  
  private static final class SortedItemsCallback 
      extends NaiveAsyncCallback<ItemBundle> {
    
    private final class ReturnItemCallback extends NaiveAsyncCallback<Void> {

      private Widget returned;

      public ReturnItemCallback(String memberCode, Item item) {
        this.returned = new Label("[" + memberCode + "] returned: " + item.getTitulo());
      }
      
      @Override
      public void onSuccess(Void result) {
        activityTable.addItem(returned);
      }
    }

    private final class BorrowItemCallback extends NaiveAsyncCallback<Void> {

      private Widget borrowed;
      
      public BorrowItemCallback(String memberCode, Item item) {
        this.borrowed = new Label("[" + memberCode + "] borrowed: " + item.getTitulo());
      }

      @Override
      public void onSuccess(Void result) {
        activityTable.addItem(borrowed);
      }
    }

    private final FlexTable availableItems;
    private final FlexTable borrowedItems;
    private final MembersDropDown membersDropDown;
    private final ActivityTable activityTable;
    private final MemberServiceAsync memberService;

    private ItemBundle itemBundle;

    private SortedItemsCallback(
        FlexTable availableItems, 
        FlexTable borrowedItems, 
        MembersDropDown membersDropDown, 
        ActivityTable activityTable, 
        MemberServiceAsync memberService) {
      this.availableItems = availableItems;
      this.borrowedItems = borrowedItems;
      this.membersDropDown = membersDropDown;
      this.activityTable = activityTable;
      this.memberService = memberService;
    }

    public void onSuccess(ItemBundle itemBundle) {
      this.itemBundle = itemBundle;
      this.refresh();
    }
    
    public void refresh() {
      borrowedItems.clear();
      availableItems.clear();
      int i = 0;
      String selectedMember = selectedMember();
      for (Item item : itemBundle.getBorrowed()) {
        if (selectedMember.equals("")) {
          borrowedItems.setText(++i, 0, 
              membersDropDown.memberIdToCodeMap.get(item.getParadeiro()) + "-" + item.getTitulo());
        } else if (item.getParadeiro().toString().equals(selectedMember)) {
          Widget temp = buildBorrowedItemWidget(item);
          borrowedItems.setWidget(++i, 0, temp);
        }
      }
      i = 0;
      for (Item item : itemBundle.getAvailable()) {
        Widget temp = buildAvailableItemWidget(item);
        availableItems.setWidget(++i, 0, temp);
      }
    }

    private Widget buildAvailableItemWidget(final Item item) {
      final Label result = new Label(item.getTitulo());
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

  private VerticalPanel mainPanel = new VerticalPanel();

//  @Override
  public void onModuleLoad() {

    MemberServiceAsync memberService = GWT.create(MemberService.class);
    
    final FlexTable borrowedItemsTable = new FlexTable();
    final TextBox toBorrow = new TextBox();
    final FlexTable availableItemsTable = new FlexTable();
    final ActivityTable activityTable = new ActivityTable();
    final MembersDropDown membersDropDown = new MembersDropDown();
    borrowedItemsTable.setText(0, 0, "borrowed");
    availableItemsTable.setText(0, 0, "available");
    activityTable.setText(0, 0, "activity");
    
    final AsyncCallback<Collection<Member>> sortedMembersCallback = 
      new MembersDropDownCallback(membersDropDown);
    memberService.getSortedMembers(sortedMembersCallback);
    
	final SortedItemsCallback sortedItemsCallback =
	  new SortedItemsCallback(availableItemsTable, borrowedItemsTable, membersDropDown, activityTable, memberService);
	memberService.getFancySortedItems(sortedItemsCallback);

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
