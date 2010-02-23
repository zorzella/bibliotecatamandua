package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

import java.util.SortedSet;

public interface MemberServiceAsync {

  void getSortedMembers(AsyncCallback<SortedSet<Member>> callback);

  void getFancySortedItems(AsyncCallback<ItemBundle> callback);

  void returnItem(Long memberId, Item item, AsyncCallback<Void> callback);

  void borrowItem(Long memberId, Item item, AsyncCallback<Void> borrowItemCallback);

  void adminOrDie(AsyncCallback<Void> callback);

  void createNew(String parentName, String childFirstName, String childLastName, String code,
      String email, AsyncCallback<Void> callback);

}
