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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.Collection;

public class Tamandua implements EntryPoint {

  private final class MembersDropDownCallback extends 
      NaiveAsyncCallback<Collection<Member>> {
    private final ListBox membersDropDown;

    private MembersDropDownCallback(ListBox membersDropDown) {
      this.membersDropDown = membersDropDown;
    }

    //      @Override
    public void onSuccess(Collection<Member> members) {
      for (Member member : members) {
        if (member.getNome().trim().equals("")) {
          continue;
        }
        membersDropDown.addItem(
      		  member.getCodigo() + " - " + TamanduaUtil.nome(member),
      		  member.getId().toString());
      }
    }
  }

  private final class CurrentMemberChangeHandler implements ChangeHandler {
    private final SortedItemsCallback sortedItemsCallback;

    private CurrentMemberChangeHandler(SortedItemsCallback sortedItemsCallback) {
      this.sortedItemsCallback = sortedItemsCallback;
    }

    public void onChange(ChangeEvent event) {
      sortedItemsCallback.refresh();
    }
  }

  private final class SortedItemsCallback 
      extends NaiveAsyncCallback<ItemBundle> {
    
    private final class ReturnItemCallback extends NaiveAsyncCallback<Void> {

      @Override
      public void onSuccess(Void result) {}
    }

    private final FlexTable availableItems;
    private final FlexTable borrowedItems;
    private ItemBundle itemBundle;
    private final ListBox membersDropDown;
    private final MemberServiceAsync memberService;

    private SortedItemsCallback(
        FlexTable availableItems, 
        FlexTable borrowedItems, 
        ListBox membersDropDown, 
        MemberServiceAsync memberService) {
      this.availableItems = availableItems;
      this.borrowedItems = borrowedItems;
      this.membersDropDown = membersDropDown;
      this.memberService = memberService;
    }

    public void onSuccess(ItemBundle itemBundle) {
      this.itemBundle = itemBundle;
      refresh();
    }
    
    public void refresh() {
      borrowedItems.clear();
      availableItems.clear();
      int i = 0;
      String selectedMember = selectedMember();
      for (Item item : itemBundle.getBorrowed()) {
        if (selectedMember.equals("")) {
          borrowedItems.setText(++i, 0, item.getTitulo());
        } else if (item.getParadeiro().toString().equals(selectedMember)) {
          Widget temp = buildBorrowedItemWidget(item);
          borrowedItems.setWidget(++i, 0, temp);
        }
      }
      i = 0;
      for (Item item : itemBundle.getAvailable()) {
        availableItems.setText(++i, 0, item.getTitulo());
      }
    }

    private String selectedMember() {
      return membersDropDown.getValue(membersDropDown.getSelectedIndex());
    }

    private Label buildBorrowedItemWidget(final Item item) {
      final Label result = new Label(item.getTitulo());
      final HandlerRegistration foo; 
      ClickHandler clickHandler = new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          String memberCode = 
            selectedMember();
          AsyncCallback<Void> returnItemCallback = new ReturnItemCallback();
          memberService.returnItem(memberCode, item, returnItemCallback);
          result.setText(item.getTitulo() + " - returning");
          //TODO: remove click handler and install new one
          
        }
      };
      foo = result.addClickHandler(clickHandler);
      return result;
    }
  }

  private VerticalPanel mainPanel = new VerticalPanel();

//  @Override
  public void onModuleLoad() {

    MemberServiceAsync memberService = GWT.create(MemberService.class);
    
    final FlexTable borrowedItemsTable = new FlexTable();
    final FlexTable availableItemsTable = new FlexTable();
    final ListBox membersDropDown = new ListBox();
    membersDropDown.addItem("");
    borrowedItemsTable.setText(0, 0, "borrowed");
    availableItemsTable.setText(0, 0, "available");
    
    final AsyncCallback<Collection<Member>> sortedMembersCallback = 
      new MembersDropDownCallback(membersDropDown);
    memberService.getSortedMembers(sortedMembersCallback);
    
	final SortedItemsCallback sortedItemsCallback =
	  new SortedItemsCallback(availableItemsTable, borrowedItemsTable, membersDropDown, memberService);
	memberService.getFancySortedItems(sortedItemsCallback);

	final CurrentMemberChangeHandler memberChangeHandler = 
      new CurrentMemberChangeHandler(sortedItemsCallback);
    membersDropDown.addChangeHandler(memberChangeHandler);
    

    mainPanel.add(membersDropDown);
    mainPanel.add(borrowedItemsTable);
    mainPanel.add(availableItemsTable);
    
    RootPanel.get("list").add(mainPanel);

  }

}
