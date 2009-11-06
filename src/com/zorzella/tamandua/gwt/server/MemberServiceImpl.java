package com.zorzella.tamandua.gwt.server;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.zorzella.tamandua.AdminOrDie;
import com.zorzella.tamandua.BorrowReturnServlet;
import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Loan;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.PMF;
import com.zorzella.tamandua.Queries;
import com.zorzella.tamandua.gwt.client.MemberService;

import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

public class MemberServiceImpl extends RemoteServiceServlet implements MemberService {

  private final PersistenceManager pm;

  public MemberServiceImpl() {
    pm = PMF.get().getPersistenceManager();
  }
  
//  @Override
  public SortedSet<Member> getSortedMembers() {
    SortedSet<Member> members = Queries.getSortedMembers(pm);
    return members;
  }

  public ItemBundle getFancySortedItems() {
    Map<Long, String> map = BorrowReturnServlet.getMap(getSortedMembers());

    ItemBundle temp = new Queries(map).getFancySortedItems(pm);
    
    return new ItemBundle(
        Lists.newArrayList(temp.getAvailable()), 
        Lists.newArrayList(temp.getBorrowed()));
  }

//  @Override
  public void returnItem(Long memberId, Item item) {
    Item liveItem = pm.getObjectById(Item.class, item.getId());
    if (!memberId.equals(liveItem.getParadeiro())) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is in paradeiro '%s', not in '%s'.", 
          item.getTitulo(), item.getParadeiro(), memberId));
    }
    String adminCode = AdminOrDie.adminOrDie().getNickname();

    Loan loan = Queries.getFirstByQuery(Loan.class, pm, 
        "memberId == " + memberId + 
        " && itemId == " + item.getId() + "");
    //                "memberCode == ? && itemId == ? && returnDate == NULL", memberCode, item.getId());

    Transaction currentTransaction = pm.currentTransaction();
    currentTransaction.begin();
    
    liveItem.setParadeiro(null);
    pm.makePersistent(liveItem);
    
    loan.setReturnDate(adminCode, new Date());
    pm.makePersistent(loan);
    
    currentTransaction.commit();
  }

//  @Override
  public void borrowItem(Long memberId, Item item) {
    Item liveItem = pm.getObjectById(Item.class, item.getId());
    if (liveItem.getParadeiro() != null) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is already in paradeiro '%s'.", 
          item.getTitulo(), item.getParadeiro()));
    }
    
    String admin = AdminOrDie.adminOrDie().getNickname();

    Transaction currentTransaction = pm.currentTransaction();
    currentTransaction.begin();

    liveItem.setParadeiro(memberId);
    pm.makePersistent(liveItem);
    
    Loan loan = new Loan(admin, memberId, item.getId());
    pm.makePersistent(loan);

    currentTransaction.commit();
  }

  @Override
  public void adminOrDie() {
    AdminOrDie.adminOrDie();
  }
}
