package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.zorzella.tamandua.Item;

/**
 * The popup to confirm the borrowing of an item. This popup has 5 buttons (3 on top and 2 on the bottom):
 * 
 * Go To Prev
 * Go To Next
 * Close
 * 
 * Edit
 * Borrow and close 
 * 
 * @author zorzella
 */
final class ToBorrowPopup extends Composite {

    private final ItemWidgetBundle itemWidgetBundle;
    private final Panel backing;
    private final Label itemTitleLabel = GwtUtil.label("None", Styles.TITLE);
    private final ItemPanel editItemPanel;

    private Item item;
    
    ToBorrowPopup(
        ItemPanel editItemPanel,
        ItemWidgetBundle itemWidgetBundle) {
      this.editItemPanel = editItemPanel;
      this.itemWidgetBundle = itemWidgetBundle;
      backing = new FlowPanel();

      backing.setStyleName(Styles.POPUP_OUTER);

      Label prevButton = buildPrevButton();
      Label nextButton = buildNextButton();
      Label closeButton = buildCloseButton();

      Label editButton = buildEditButton();
//      Label borrowNextButton = buildBorrowNextButton();
      Label borrowButton = buildBorrowButton();

      final Panel fullPanel = new FlowPanel();
      fullPanel.setStyleName(Styles.POPUP_INNER);
      
      Panel prevNextClosePanel = GwtUtil.div(Styles.PREV_NEXT_CLOSE);
      
      prevNextClosePanel.add(prevButton);
      prevNextClosePanel.add(nextButton);
      prevNextClosePanel.add(closeButton);
      
      fullPanel.add(prevNextClosePanel);
      fullPanel.add(itemTitleLabel);
      fullPanel.add(editButton);
//      fullPanel.add(borrowNextButton);
      fullPanel.add(borrowButton);

      backing.add(fullPanel);
      backing.setVisible(false);
      initWidget(backing);
    }

    private Label buildEditButton() {
      ClickHandler handler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          setVisible(false);
          editItemPanel.setToItem(item);
          editItemPanel.setVisible(true);
          
//          itemWidgetBundle.initiateEdit(item);
//          item = itemWidgetBundle.prevAvailable(item);
//          repaint();
        }
      };
      Label result = GwtUtil.label("Edit", Styles.BORROW_PREV);
      result.addClickHandler(handler);
      return result;
    }
    
//    private Label buildBorrowNextButton() {
//      ClickHandler handler = new ClickHandler() {
//        //        @Override
//        public void onClick(ClickEvent event) {
//          itemWidgetBundle.initiateBorrow(item);
//          item = itemWidgetBundle.nextAvailable(item);
//          repaint();
//        }
//      };
//      Label closeButton = GwtUtil.label("Borrow", "borrow-next");
//      closeButton.addClickHandler(handler);
//      return closeButton;
//    }
    
    private Label buildBorrowButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
        	backing.setVisible(false);
          itemWidgetBundle.initiateBorrow(item);
        }
      };
      Label result = GwtUtil.label("Borrow", Styles.BORROW);
      result.addClickHandler(handler);
      return result;
    }

    private Label buildCloseButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
        	backing.setVisible(false);
        }
      };
      Label result = new Label("Close");
      result.setStyleName(Styles.CLOSE);
      result.addClickHandler(handler);
      return result;
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
      closeButton.setStyleName(Styles.PREV);
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
      itemTitleLabel.setText(item.getTitulo() + " -- "
          + item.getAutor() + " -- "
          + item.getToca() + " -- "
          + item.getIsbn() + " -- "
          + item.getTamanho() + " -- "
          + item.getTagsAsString());
    }
    
    public void show(Item item) {
      this.item = item;
      repaint();
//      backing.show();
      backing.setVisible(true);
    }
  }