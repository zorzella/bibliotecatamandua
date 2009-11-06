package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

import java.util.SortedSet;

@RemoteServiceRelativePath("member")
public interface MemberService extends RemoteService {

  void adminOrDie();
  
  SortedSet<Member> getSortedMembers();
  
  ItemBundle getFancySortedItems();

  void returnItem(Long memberId, Item item);

  void borrowItem(Long memberId, Item item);
  
}
