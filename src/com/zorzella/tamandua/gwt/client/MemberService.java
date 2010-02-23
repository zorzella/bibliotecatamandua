package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

import java.util.Collection;

@RemoteServiceRelativePath("member")
public interface MemberService extends RemoteService {

  void adminOrDie();
  
  Collection<Member> getSortedMembers();
  
  ItemBundle getFancySortedItems();

  void returnItem(Long memberId, Item item);

  void borrowItem(Long memberId, Item item);
  
  void createNew(String parentName, String childFirstName, String childLastName, String code, String email);
  
}
