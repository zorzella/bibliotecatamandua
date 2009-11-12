package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.gwt.client.Tamandua.ActivityTable;
import com.zorzella.tamandua.gwt.client.Tamandua.MembersDropDown;

import java.util.HashMap;
import java.util.Map;

public class ItemWidgetBundle {

  private final class BorrowItemCallback implements AsyncCallback<Void> {

//    private final ActivityTable activityTable;
    private final Member member;
    private final Item item;

    public BorrowItemCallback(ActivityTable activityTable, Member member, Item item) {
//      this.activityTable = activityTable;
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

//    private final ActivityTable activityTable;
    private final Member member;
    private final Item item;

    public ReturnItemCallback(Member member, Item item, ActivityTable activityTable) {
//      this.activityTable = activityTable;
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
  private final TbrPopup toBorrowReturnItemWidget;
  private final ItemBundle itemBundle;

  private final Map<Item, Label> availableItemToWidgetMap;
  private final Map<Item, Label> borrowedItemToWidgetSimpleMap;
  private final Map<Item, Label> borrowedItemToWidgetClickableMap;
  private final Map<Item,Status> itemStatusMap;
  
  public ItemWidgetBundle(
      MembersDropDown membersDropDown, 
      MemberServiceAsync memberService, 
      ActivityTable activityTable, 
      ItemBundle itemBundle
      ) {
    this.membersDropDown = membersDropDown;
    this.memberService = memberService;
    this.activityTable = activityTable;
    this.itemBundle = itemBundle;
    
    this.toBorrowReturnItemWidget = new TbrPopup(this);
    


    
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

  private Item nextAvailable(Item item) {
    boolean found = false;
    for (Item temp : itemBundle.getAvailable()) {
      if (found) {
        return temp;
      }
      if (item == temp) {
        found = true;
      }
    }
    return null;
  }
  
  private Item prevAvailable(Item item) {
    Item last = null;
    for (Item temp : itemBundle.getAvailable()) {
      if (item == temp) {
        return last;
      }
      last = temp;
    }
    throw new IllegalStateException();
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
        Member member = membersDropDown.getSelectedMember();
        AsyncCallback<Void> returnItemCallback = 
          new ItemWidgetBundle.ReturnItemCallback(member, item, activityTable);
        memberService.returnItem(member.getId(), item, returnItemCallback);
        itemStatusMap.put(item, Status.RETURNING);
        result.setText(getLabelTextFor(item));
        //TODO: install new one (to undo)
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

        if (true) {
          toBorrowReturnItemWidget.show(item);
        } else {
          initiateBorrow(item);
        }
        //TODO: install new one (to undo)
      }
    };
    result.addClickHandler(clickHandler);
    return result;
  }

  private void initiateBorrow(final Item item) {
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

  private static final class TbrPopup extends Widget {

    private final ItemWidgetBundle itemWidgetBundle;
    private final PopupPanel backing;
    private final Label itemTitleLabel = label("None", "title");

    private Label label(String text, String styleName) {
      Label result = new Label(text);
      result.setStyleName(styleName);
      return result;
    }
    
    private Item item;
    
    private static Panel HSPACER() {
      return div("hspacer");
    }

    private static Panel div(String styleName) {
      Panel result = new FlowPanel();
      result.setStyleName(styleName);
      return result;
    }
    
    private static Panel VSPACER() {
      return div("vspacer");
    }
    
    TbrPopup(ItemWidgetBundle itemWidgetBundle) {
      this.itemWidgetBundle = itemWidgetBundle;
      backing = new PopupPanel(false);

      //    result.setStyleName("demo-PopUpPanel");
      backing.setStyleName("demo-popup");

      Label prevButton = buildPrevButton();
      Label nextButton = buildNextButton();
      Label closeButton = buildCloseButton();

      Label borrowPrevButton = buildBorrowPrevButton();
      Label borrowNextButton = buildBorrowNextButton();
      Label borrowButton = buildBorrowButton();

      final Panel fullPanel = new FlowPanel();
      fullPanel.setStyleName("popup-panel");
//      fullPanel.setSize("200px", "400px");
      
      Panel prevNextClosePanel = div("prev-next-close");
      
      prevNextClosePanel.add(prevButton);
      prevNextClosePanel.add(nextButton);
      prevNextClosePanel.add(closeButton);
      
      fullPanel.add(prevNextClosePanel);
      fullPanel.add(itemTitleLabel);
//      Panel bottom = div("borrow");
//      bottom.add(borrowButton);
//    fullPanel.add(bottom);
      fullPanel.add(borrowPrevButton);
      fullPanel.add(borrowNextButton);
      fullPanel.add(borrowButton);

      backing.add(fullPanel);
    }

    private Label buildBorrowPrevButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          itemWidgetBundle.initiateBorrow(item);
          item = itemWidgetBundle.prevAvailable(item);
          repaint();
        }
      };
      Label closeButton = label("Borrow", "borrow-prev");
      closeButton.addClickHandler(handler);
      return closeButton;
    }
    
    private Label buildBorrowNextButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          itemWidgetBundle.initiateBorrow(item);
          item = itemWidgetBundle.nextAvailable(item);
          repaint();
        }
      };
      Label closeButton = label("Borrow", "borrow-next");
      closeButton.addClickHandler(handler);
      return closeButton;
    }
    
    private Label buildBorrowButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          backing.hide();
          itemWidgetBundle.initiateBorrow(item);
        }
      };
      Label closeButton = label("Borrow", "borrow");
      closeButton.addClickHandler(handler);
      return closeButton;
    }

    private Label buildCloseButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          backing.hide();
        }
      };
      Label closeButton = new Label("Close");
      closeButton.setStyleName("close");
      closeButton.addClickHandler(handler);
      return closeButton;
    }

    private Label buildPrevButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
          item = itemWidgetBundle.prevAvailable(item);
          repaint();
        }
      };
      Label closeButton = new Label("Prev");
      closeButton.setStyleName("prev");
      closeButton.addClickHandler(handler);
      return closeButton;
    }
    
    private Label buildNextButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          item = itemWidgetBundle.nextAvailable(item);
          repaint();
//          backing.hide();
        }
      };
      Label closeButton = new Label("Next");
      closeButton.setStyleName("next");
      closeButton.addClickHandler(handler);
      return closeButton;
    }

    private void repaint() {
      itemTitleLabel.setText(item.getTitulo());
    }
    
    public void show(Item item) {
      this.item = item;
      repaint();
      backing.show();
    }
  }
}
