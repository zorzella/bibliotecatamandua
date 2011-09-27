package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.zorzella.tamandua.Item;

public final class ToReturnPopup extends Composite {

    private final ItemWidgetBundle itemWidgetBundle;
    private final Panel backing;
    private final Label itemTitleLabel = GwtUtil.label("None", Styles.TITLE);

    private Item item;

    ToReturnPopup(ItemWidgetBundle itemWidgetBundle) {
      this.itemWidgetBundle = itemWidgetBundle;
      backing = new FlowPanel();

      backing.setStyleName(Styles.POPUP_OUTER);

      Label prevButton = buildPrevButton();
      Label nextButton = buildNextButton();
      Label closeButton = buildCloseButton();

      Label returnPrevButton = buildReturnPrevButton();
      Label returnNextButton = buildReturnNextButton();
      Label returnButton = buildReturnButton();

      final Panel fullPanel = new FlowPanel();
      fullPanel.setStyleName(Styles.POPUP_INNER);
      
      Panel prevNextClosePanel = GwtUtil.div(Styles.PREV_NEXT_CLOSE);
      
      prevNextClosePanel.add(prevButton);
      prevNextClosePanel.add(nextButton);
      prevNextClosePanel.add(closeButton);
      
      fullPanel.add(prevNextClosePanel);
      fullPanel.add(itemTitleLabel);
      fullPanel.add(returnPrevButton);
      fullPanel.add(returnNextButton);
      fullPanel.add(returnButton);

      backing.add(fullPanel);
      backing.setVisible(false);
      initWidget(backing);
    }

    private Label buildReturnPrevButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          itemWidgetBundle.initiateReturn(item);
          item = itemWidgetBundle.prevBorrowed(item);
          repaint();
        }
      };
      Label closeButton = GwtUtil.label("Return", Styles.BORROW_PREV);
      closeButton.addClickHandler(handler);
      return closeButton;
    }
    
    private Label buildReturnNextButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
          itemWidgetBundle.initiateReturn(item);
          item = itemWidgetBundle.nextBorrowed(item);
          repaint();
        }
      };
      Label closeButton = GwtUtil.label("Return", Styles.BORROW_NEXT);
      closeButton.addClickHandler(handler);
      return closeButton;
    }
    
    private Label buildReturnButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
        	backing.setVisible(false);
          itemWidgetBundle.initiateReturn(item);
        }
      };
      Label closeButton = GwtUtil.label("Return", Styles.BORROW);
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
      closeButton.setStyleName(Styles.CLOSE);
      closeButton.addClickHandler(handler);
      return closeButton;
    }

    private Label buildPrevButton() {
      ClickHandler handler = new ClickHandler() {
        //        @Override
        public void onClick(ClickEvent event) {
//          backing.hide();
          item = itemWidgetBundle.prevBorrowed(item);
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
          item = itemWidgetBundle.nextBorrowed(item);
          repaint();
//          backing.hide();
        }
      };
      Label closeButton = new Label("Next");
      closeButton.setStyleName(Styles.NEXT);
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