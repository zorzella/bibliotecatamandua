package com.zorzella.tamandua.gwt.client;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

public interface MemberServiceAsync {

  void adminOrDie(AsyncCallback<Void> callback) throws NotAnAdminException;

  void getSortedMembers(AsyncCallback<Collection<Member>> callback);

  void getFancySortedItems(AsyncCallback<ItemBundle> callback);

  void returnItem(Long memberId, Item item, AsyncCallback<Void> callback) 
    throws AlreadyReturnedException;

  void borrowItem(Long memberId, Item item, AsyncCallback<Void> borrowItemCallback) 
    throws AlreadyBorrowedToThisMemberException;

  void createNewMember(
      String parentName, 
      String childFirstName, 
      String childLastName, 
      String code,
      String email, 
      AsyncCallback<Void> callback);
}
