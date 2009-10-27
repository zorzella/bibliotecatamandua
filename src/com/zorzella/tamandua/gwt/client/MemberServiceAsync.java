package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

import java.util.Collection;

public interface MemberServiceAsync {

  void getSortedMembers(AsyncCallback<Collection<Member>> callback);

  void getFancySortedItems(AsyncCallback<ItemBundle> callback);

  void returnItem(String memberCode, Item item, AsyncCallback<Void> callback);

  void borrowItem(String memberCode, Item item, AsyncCallback<Void> borrowItemCallback);

  void adminOrDie(AsyncCallback<Void> callback);

}
