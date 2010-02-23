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

import java.util.Collection;
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
  public Collection<Member> getSortedMembers() {
    SortedSet<Member> members = Queries.getSortedMembers(pm);
    
    Collection<Member> result = Lists.newArrayList();
    for (Member member : members) {
      result.add(pm.detachCopy(member));
    }
    return result;
  }

  public ItemBundle getFancySortedItems() {
    Map<Long, String> map = BorrowReturnServlet.getMap(Queries.getSortedMembers(pm));

    ItemBundle temp = new Queries(map).getDetachedFancySortedItems(pm);

    return new ItemBundle(
        Lists.newArrayList(temp.getAvailable()), 
        Lists.newArrayList(temp.getBorrowed()));
  }

//  @Override
  public void returnItem(final Long memberId, final Item item) {
    final Item liveItem = pm.getObjectById(Item.class, item.getId());
    if (!memberId.equals(liveItem.getParadeiro())) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is in paradeiro '%s', not in '%s'.", 
          item.getTitulo(), item.getParadeiro(), memberId));
    }
    final String adminCode = AdminOrDie.adminOrDie().getNickname();

    final Loan loan = Queries.getFirstByQuery(Loan.class, pm, 
        "memberId == " + memberId + 
        " && itemId == " + item.getId() + 
        " && returnDate == null");

    doJob(new Runnable() {
		
		public void run() {
			liveItem.setParadeiro(null);
			pm.makePersistent(liveItem);
		}
	});
    
	doJob(new Runnable() {
		
		public void run() {
			loan.setReturnDate(adminCode, new Date());
			pm.makePersistent(loan);
		}
	});    
  }

//  @Override
  public void borrowItem(final Long memberId, final Item item) {
    final Item liveItem = pm.getObjectById(Item.class, item.getId());
    if (liveItem.getParadeiro() != null) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is already in paradeiro '%s'.", 
          item.getTitulo(), liveItem.getParadeiro()));
    }
    
    final String admin = AdminOrDie.adminOrDie().getNickname();

	doJob(new Runnable() {
      public void run() {
        liveItem.setParadeiro(memberId);
        pm.makePersistent(liveItem);
      }
    });

    doJob(new Runnable() {	
		public void run() {
			Loan loan = new Loan(admin, memberId, item.getId());
			pm.makePersistent(loan);
		}
	});

  }

  private void doJob(Runnable r) {
    Transaction currentTransaction = pm.currentTransaction();
    if (currentTransaction.isActive()) {
      currentTransaction.rollback();
    }
    try {
      currentTransaction.begin();
      r.run();
      currentTransaction.commit();
    } catch (Exception e) {
      currentTransaction.rollback();
    }
	  
  }
  
//  @Override
  public void adminOrDie() {
    AdminOrDie.adminOrDie();
  }

  public void createNew(
      String parentName, 
      String childFirstName, 
      String childLastName,
      String code, 
      String email) {
    
    Member member = new Member(code);
    member.setMae(parentName);
    member.setNome(childFirstName);
    member.setSobrenome(childLastName);
    member.setEmail(email);
    
    Transaction currentTransaction = pm.currentTransaction();
    currentTransaction.begin();

    pm.makePersistent(member);
    
    currentTransaction.commit();
  }
}
