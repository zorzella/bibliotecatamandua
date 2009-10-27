package com.zorzella.tamandua.gwt.server;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.zorzella.tamandua.AdminOrDie;
import com.zorzella.tamandua.BorrowReturnServlet;
import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.PMF;
import com.zorzella.tamandua.Queries;
import com.zorzella.tamandua.gwt.client.MemberService;

import java.util.Collection;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

public class MemberServiceImpl extends RemoteServiceServlet implements MemberService {

  private final PersistenceManager pm;

  public MemberServiceImpl() {
    pm = PMF.get().getPersistenceManager();
  }
  
//  @Override
  public Collection<Member> getSortedMembers() {
    Collection<Member> members = Queries.getSortedMembers(pm);
    return members;
  }

  public ItemBundle getFancySortedItems() {
    Map<Long, String> map = BorrowReturnServlet.getMap(getSortedMembers());

    ItemBundle temp = new Queries(map).getFancySortedItems(pm);
    
    return new ItemBundle(
        Lists.newArrayList(temp.getAvailable()), 
        Lists.newArrayList(temp.getBorrowed()));
  }

  @Override
  public void returnItem(String memberId, Item item) {
    Item liveItem = pm.getObjectById(Item.class, item.getId());
    if (!memberId.equals(liveItem.getParadeiro().toString())) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is in paradeiro '%s', not in '%s'.", 
          item.getTitulo(), item.getParadeiro(), memberId));
    }
    Transaction currentTransaction = pm.currentTransaction();
    currentTransaction.begin();
    liveItem.setParadeiro(null);
    pm.makePersistent(liveItem);
    currentTransaction.commit();
  }

  @Override
  public void borrowItem(String memberId, Item item) {
    Item liveItem = pm.getObjectById(Item.class, item.getId());
    if (liveItem.getParadeiro() != null) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is already in paradeiro '%s'.", 
          item.getTitulo(), item.getParadeiro()));
    }
    Transaction currentTransaction = pm.currentTransaction();
    currentTransaction.begin();
    liveItem.setParadeiro(Long.valueOf(memberId));
    pm.makePersistent(liveItem);
    currentTransaction.commit();
  }

  @Override
  public void adminOrDie() {
    AdminOrDie.adminOrDie();
  }
}
