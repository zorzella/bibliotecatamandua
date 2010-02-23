package com.zorzella.tamandua.gwt.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.gwt.client.Tamandua.ActivityTable;
import com.zorzella.tamandua.gwt.client.Tamandua.MembersDropDown;

public class ItemWidgetBundle {

  private final class BorrowItemCallback implements AsyncCallback<Void> {

    private final Member member;
    private final Item item;

    public BorrowItemCallback(ActivityTable activityTable, Member member, Item item) {
      this.member = member;
      this.item = item;
    }

    //      @Override
    public void onSuccess(Void result) {
      Label borrowedLabel = 
        new Label("[" + member.getCodigo() + "] borrowed: " + item.getTitulo());
      activityTable.addItem(borrowedLabel);
      itemStatusMap.put(item, Status.BORROWED);
      getWidgetForAvailable(item).setText(item.getTitulo() + " - borrowed");
    }

//    @Override
    public void onFailure(Throwable caught) {
      Label failedToBorrowedLabel = 
        new Label("[" + member.getCodigo() + "] failed to borrow: " + item.getTitulo());
      activityTable.addItem(failedToBorrowedLabel);
      itemStatusMap.put(item, Status.FAILURE_TO_BORROW);
      getWidgetForAvailable(item).setText(item.getTitulo() + " - failed to borrow");
    }
  }

  private final class ReturnItemCallback implements AsyncCallback<Void> {

    private final Member member;
    private final Item item;

    public ReturnItemCallback(Member member, Item item, ActivityTable activityTable) {
      this.member = member;
      this.item = item;
    }

    //      @Override
    public void onSuccess(Void result) {
      Label returned = 
        new Label("[" + member.getCodigo() + "] returned: " + item.getTitulo());
      activityTable.addItem(returned);
      itemStatusMap.put(item, Status.AVAILABLE);
      borrowedItemToWidgetClickableMap.get(item).setText(item.getTitulo() + " - returned");
    }

//    @Override
    public void onFailure(Throwable caught) {
      Label failedToReturnLabel = 
        new Label("[" + member.getCodigo() + "] failed to return: " + item.getTitulo());
      activityTable.addItem(failedToReturnLabel);
      itemStatusMap.put(item, Status.FAILURE_TO_RETURN);
      getWidgetForAvailable(item).setText(item.getTitulo() + " - failed to return");
    }
  }
  
  private enum Status {
    AVAILABLE(""),
    BORROWED(""),
    BORROWING(" - borrowing"),
    RETURNING(" - returning"),
    FAILURE_TO_BORROW(" - failed to borrow"),
    FAILURE_TO_RETURN(" - failed to return"),
    ;
    
    private final String string;
    
    Status(String string) {
      this.string = string;
    }
    
    @Override
    public String toString() {
      return string;
    }
  }

  private final MembersDropDown membersDropDown;
  private final MemberServiceAsync memberService;
  private final ActivityTable activityTable;
  private final ToBorrowPopup toBorrowPopup;
  private final ToReturnPopup toReturnPopup;
  private final ItemBundle itemBundle;

  private final Map<Item, Label> availableItemToWidgetMap;
  private final Map<Item, Label> borrowedItemToWidgetSimpleMap;
  private final Map<Item, Label> borrowedItemToWidgetClickableMap;
  private final Map<Item,Status> itemStatusMap;
  
  public ItemWidgetBundle(
      MembersDropDown membersDropDown, 
      MemberServiceAsync memberService, 
      ActivityTable activityTable, 
      ItemBundle itemBundle,
      Panel lendingPanel
      ) {
    this.membersDropDown = membersDropDown;
    this.memberService = memberService;
    this.activityTable = activityTable;
    this.itemBundle = itemBundle;
    
    this.toBorrowPopup = new ToBorrowPopup(this);
    this.toReturnPopup = new ToReturnPopup(this);
    lendingPanel.add(toBorrowPopup);
    lendingPanel.add(toReturnPopup);

    this.availableItemToWidgetMap = new HashMap<Item, Label>();
    this.borrowedItemToWidgetSimpleMap = new HashMap<Item, Label>();
    this.borrowedItemToWidgetClickableMap = new HashMap<Item, Label>();
    this.itemStatusMap = new HashMap<Item, Status>();
    for (Item item : itemBundle.getAvailable()) {
      itemStatusMap.put(item, Status.AVAILABLE);
      availableItemToWidgetMap.put(item, buildAvailableItemWidget(item));
    }
    for (Item item : itemBundle.getBorrowed()) {
      itemStatusMap.put(item, Status.BORROWED);
      borrowedItemToWidgetSimpleMap.put(item, buildBorrowedSimpleWidget(item));
      borrowedItemToWidgetClickableMap.put(item, buildBorrowedItemClickableWidget(item));
    }
  }

  Item prevAvailable(Item item) {
	  Collection<Item> allAvailable = itemBundle.getAvailable();
	    Item last = allAvailable.iterator().next();
		for (Item temp : allAvailable) {
	      if (item == temp) {
	        return last;
	      }
	      last = temp;
	    }
	    return last;
	  }
  
  Item nextAvailable(Item item) {
    boolean found = false;
    Collection<Item> allAvailable = itemBundle.getAvailable();
    Item last = allAvailable.iterator().next(); 
	for (Item temp : allAvailable) {
		last = temp;
      if (found) {
        return temp;
      }
      if (item == temp) {
        found = true;
      }
    }
    return last;
  }
  
  Item prevBorrowed(Item item) {
	  Collection<Item> borrowed = itemBundle.getBorrowed(item.getParadeiro());
	    Item last = borrowed.iterator().next();
		for (Item temp : borrowed) {
	      if (item == temp) {
	        return last;
	      }
	      last = temp;
	    }
	    return last;
	  }
  
  Item nextBorrowed(Item item) {
	    boolean found = false;
	    Collection<Item> borrowed = itemBundle.getBorrowed(item.getParadeiro());
	    Item last = borrowed.iterator().next();
		for (Item temp : borrowed) {
			last = temp;
	      if (found) {
	        return temp;
	      }
	      if (item == temp) {
	        found = true;
	      }
	    }
	    return last;
	  }
  private Label buildBorrowedSimpleWidget(Item item) {
    Label result = new Label(
        membersDropDown.idToCode(item.getParadeiro()) + "-" + getLabelTextFor(item));
    result.setStyleName("whisper");
    return result;
  }

  private String getLabelTextFor(Item item) {
    return item.getTitulo() + itemStatusMap.get(item);
  }
  
  public Label getWidgetForAvailable(Item item) {
    return this.availableItemToWidgetMap.get(item);
  }

  public Label getWidgetForBorrowed(Item item, Member selectedMember) {
    if (selectedMember == null) {
      return this.borrowedItemToWidgetSimpleMap.get(item);
    }
    return this.borrowedItemToWidgetClickableMap.get(item);
  }
  
  private Label buildBorrowedItemClickableWidget(final Item item) {
    final Label result = new Label(item.getTitulo());
    result.setStyleName("entry-row");
    ClickHandler clickHandler = new ClickHandler() {
      
//      @Override
      public void onClick(ClickEvent event) {
    	switch (itemStatusMap.get(item)) {
    	case BORROWED:
    	case FAILURE_TO_RETURN:
    		if (true) {
    			toReturnPopup.show(item);
    		} else {
    			initiateReturn(item);
    		}
    		break;
    	case RETURNING:
    		// Ok, we're in the process of returning right now
    		break;
    	case AVAILABLE:
    		// Ok, we have just returned this item, though it still shows up here
    		break;
    	case BORROWING:
    	case FAILURE_TO_BORROW:
    	default:
    		throw new IllegalStateException();
    	}
      }
    };
    result.addClickHandler(clickHandler);
    return result;
  }
  
  private Label buildAvailableItemWidget(final Item item) {
    final Label result = new Label(item.getTitulo());
    result.setStyleName("entry-row");
    ClickHandler clickHandler = new ClickHandler() {
      
//      @Override
      public void onClick(ClickEvent event) {

        Member member = membersDropDown.getSelectedMember();
        if (member == null) {
          return;
        }

    	switch (itemStatusMap.get(item)) {
    	case AVAILABLE:
    	case FAILURE_TO_BORROW:
            if (true) {
                toBorrowPopup.show(item);
              } else {
                initiateBorrow(item);
              }
    		break;
    	case BORROWING:
    		// Ok, we're in the process of borrowing right now
    		break;
    	case BORROWED:
    		// Ok, we have just borrowed this item, though it still shows up here
    		break;
    	case FAILURE_TO_RETURN:
    	case RETURNING:
    	default:
    		throw new IllegalStateException();
    	}
      }
    };
    result.addClickHandler(clickHandler);
    return result;
  }

  public void initiateReturn(final Item item) {
		Label label = borrowedItemToWidgetClickableMap.get(item);
		Member member = membersDropDown.getSelectedMember();
      AsyncCallback<Void> returnItemCallback = 
        new ItemWidgetBundle.ReturnItemCallback(member, item, activityTable);
      memberService.returnItem(member.getId(), item, returnItemCallback);
      itemStatusMap.put(item, Status.RETURNING);
      label.setText(getLabelTextFor(item));
	}
  
  void initiateBorrow(final Item item) {
    Member member = membersDropDown.getSelectedMember();
    if (member == null) {
      throw new IllegalArgumentException();
    }
    Label label = availableItemToWidgetMap.get(item);
    AsyncCallback<Void> borrowItemCallback = 
      new ItemWidgetBundle.BorrowItemCallback(activityTable, member, item);
    memberService.borrowItem(member.getId(), item, borrowItemCallback);
    itemStatusMap.put(item, Status.BORROWING);
    label.setText(getLabelTextFor(item));
  }
}
