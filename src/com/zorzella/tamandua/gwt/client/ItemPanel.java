package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.zorzella.tamandua.Item;

/**
 * Panel to add or edit an item.
 */
final class ItemPanel extends Composite implements FullPanel {

  private final TextBox itemNameInput = new TextBox();
  private final TextBox tocaInput = new TextBox();
  private final TextBox authorNameInput = new TextBox();
  private final TextBox publishingHouseInput = new TextBox();
  private final TextBox isbnInput = new TextBox();
  private final TextBox tamanhoInput = new TextBox();
  private final TextBox tagsInput = new TextBox();

  private Long itemId = null;
  
  public void clear() {
    itemId = null;
    
    tocaInput.setText(Labels.TOCA_Z);
    itemNameInput.setText("");
    authorNameInput.setText("");
    publishingHouseInput.setText("");
    isbnInput.setText("");
    tamanhoInput.setText("");
    tagsInput.setText("");
  }
  
  /**
   * Set the contents of this panel to match the given {@code item}.
   * This will also set the {@link #itemId}, since this is to be used
   * to start editing {@code item}. See {@link #buildOkClickHandler(MainPanel)}.
   */
  public void setToItem(Item item) {
    itemId = item.getId();
    tocaInput.setText(item.getToca());
    itemNameInput.setText(item.getTitulo());
    authorNameInput.setText(item.getAutor());
    publishingHouseInput.setText(item.getPublishingHouse());
    isbnInput.setText(item.getIsbn());
    tamanhoInput.setText(item.getTamanho());
    tagsInput.setText(item.getTagsAsString());
  }
  
  public ItemPanel(final MainPanel mainPanel) {
    final FlowPanel outer = new FlowPanel();
    initWidget(outer);
    outer.setStyleName(Styles.POPUP_OUTER);
    
    final FlowPanel result = new FlowPanel();
    result.setStyleName(Styles.POPUP_INNER);
    outer.add(result);

    result.add(new Label("Toca"));
    result.add(tocaInput);

    result.add(new Label("Item name"));
    result.add(itemNameInput);

    result.add(new Label("Author Name"));
    result.add(authorNameInput);

    result.add(new Label("Publishing House"));
    result.add(publishingHouseInput);

    result.add(new Label("ISBN"));
    result.add(isbnInput);
    
    result.add(new Label("Tamanho"));
    result.add(tamanhoInput);
    
    result.add(new Label("Tags"));
    result.add(tagsInput);
    
    Label ok = new Label(Labels.OK);
    ok.setStyleName(Styles.PREV_PAGE);
    ok.addClickHandler(buildOkClickHandler(mainPanel));
    result.add(ok);

    Label cancel = new Label(Labels.CANCEL);
    cancel.setStyleName(Styles.NEXT_PAGE);
    cancel.addClickHandler(buildCancelClickHandler(mainPanel));
    result.add(cancel);
  }

  private ClickHandler buildCancelClickHandler(final MainPanel mainPanel) {
    ClickHandler handler = new ClickHandler() {

      public void onClick(ClickEvent event) {
        mainPanel.makeLendingVisible();
      }
    };
    return handler;
  }

  /**
   * Id {@link #itemId} is {@code null}, Labels.OK will create a brand-new item,
   * otherwise it will edit the item with {@link #itemId}.
   */
  private ClickHandler buildOkClickHandler(final MainPanel mainPanel) {
    ClickHandler result = new ClickHandler() {

      public void onClick(ClickEvent event) {
        
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {

          public void onSuccess(Void result) {
            mainPanel.lendingPanel.reloadItems();
            mainPanel.makeLendingVisible();
            mainPanel.clearMessage();
          }

          public void onFailure(Throwable caught) {
            mainPanel.showMessage("Failed!");
          }
        };
        if (itemId == null) {
          mainPanel.memberService.createNewItem(
              tocaInput.getValue(),
              itemNameInput.getValue(), 
              authorNameInput.getValue(), 
              publishingHouseInput.getValue(), 
              isbnInput.getValue(),
              tamanhoInput.getValue(),
              tagsInput.getValue(),
              callback);
        } else {
          mainPanel.memberService.editItem(
              itemId,
              tocaInput.getValue(),
              itemNameInput.getValue(), 
              authorNameInput.getValue(), 
              publishingHouseInput.getValue(), 
              isbnInput.getValue(),
              tamanhoInput.getValue(),
              tagsInput.getValue(),
              callback);
        }
      }
    };
    return result;
  }

  public String getName() {
    return "New Item";
  }

  public Widget payload() {
    return this;
  }
}