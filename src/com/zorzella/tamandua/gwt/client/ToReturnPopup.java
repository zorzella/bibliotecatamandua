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
    private final Label itemTitleLabel = GwtUtil.label("None", "title");

    private Item item;

    ToReturnPopup(ItemWidgetBundle itemWidgetBundle) {
      this.itemWidgetBundle = itemWidgetBundle;
      backing = new FlowPanel();

      backing.setStyleName("demo-popup");

      Label prevButton = buildPrevButton();
      Label nextButton = buildNextButton();
      Label closeButton = buildCloseButton();

      Label returnPrevButton = buildReturnPrevButton();
      Label returnNextButton = buildReturnNextButton();
      Label returnButton = buildReturnButton();

      final Panel fullPanel = new FlowPanel();
      fullPanel.setStyleName("popup-panel");
      
      Panel prevNextClosePanel = GwtUtil.div("prev-next-close");
      
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
      Label closeButton = GwtUtil.label("Return", "borrow-prev");
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
      Label closeButton = GwtUtil.label("Return", "borrow-next");
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
      Label closeButton = GwtUtil.label("Return", "borrow");
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
          item = itemWidgetBundle.prevBorrowed(item);
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
          item = itemWidgetBundle.nextBorrowed(item);
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