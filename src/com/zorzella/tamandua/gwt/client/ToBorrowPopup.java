package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.zorzella.tamandua.Item;

/**
 * The popup to confirm the borrowing of an item. This popup has 6 buttons (3 on top and 3 on the bottom):
 * 
 * Go To Prev
 * Go To Next
 * Close
 * 
 * Borrow and go to prev
 * Borrow and go to next
 * Borrow and close 
 * 
 * @author zorzella
 */
final class ToBorrowPopup extends Composite {

    private final ItemWidgetBundle itemWidgetBundle;
    private final Panel backing;
    private final Label itemTitleLabel = GwtUtil.label("None", "title");

    private Item item;
    
    ToBorrowPopup(ItemWidgetBundle itemWidgetBundle) {
      this.itemWidgetBundle = itemWidgetBundle;
      backing = new FlowPanel();

      backing.setStyleName("demo-popup");

      Label prevButton = buildPrevButton();
      Label nextButton = buildNextButton();
      Label closeButton = buildCloseButton();

      Label borrowPrevButton = buildBorrowPrevButton();
      Label borrowNextButton = buildBorrowNextButton();
      Label borrowButton = buildBorrowButton();

      final Panel fullPanel = new FlowPanel();
      fullPanel.setStyleName("popup-panel");
      
      Panel prevNextClosePanel = GwtUtil.div("prev-next-close");
      
      prevNextClosePanel.add(prevButton);
      prevNextClosePanel.add(nextButton);
      prevNextClosePanel.add(closeButton);
      
      fullPanel.add(prevNextClosePanel);
      fullPanel.add(itemTitleLabel);
      fullPanel.add(borrowPrevButton);
      fullPanel.add(borrowNextButton);
      fullPanel.add(borrowButton);

      backing.add(fullPanel);
      backing.setVisible(false);
      initWidget(backing);
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
      Label closeButton = GwtUtil.label("Borrow", "borrow-prev");
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
      Label closeButton = GwtUtil.label("Borrow", "borrow-next");
      closeButton.addClickHandler(handler);
      return closeButton;
    }
    
    private Label buildBorrowButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
        	backing.setVisible(false);
          itemWidgetBundle.initiateBorrow(item);
        }
      };
      Label closeButton = GwtUtil.label("Borrow", "borrow");
      closeButton.addClickHandler(handler);
      return closeButton;
    }

    private Label buildCloseButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
        	backing.setVisible(false);
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
//      backing.show();
      backing.setVisible(true);
    }
  }