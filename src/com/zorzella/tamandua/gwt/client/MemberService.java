package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;

import java.util.Collection;

@RemoteServiceRelativePath("member")
public interface MemberService extends RemoteService {

  void adminOrDie() throws NotAnAdminException;
  
  Collection<Member> getSortedMembers();
  
  ItemBundle getFancySortedItems();

  /**
   * @throws AlreadyReturnedException
   */
  void returnItem(Long memberId, Item item)
    throws AlreadyReturnedException;

  /**
   * @throws AlreadyBorrowedToThisMemberException
   */
  void borrowItem(Long memberId, Item item)
    throws AlreadyBorrowedToThisMemberException;
  
  void createNewMember(
      String parentName, 
      String childFirstName, 
      String childLastName, 
      String code, 
      String email);

  void editItem(Long itemId, String toca, String itemName, String authorName, String isbn);
  
  void createNewItem(String toca, String itemName, String authorName, String isbn);

  void bulkUpload(String csvData);
}
