package com.zorzella.tamandua.gwt.server;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.zorzella.tamandua.AdminOrDie;
import com.zorzella.tamandua.Item;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Loan;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.Members;
import com.zorzella.tamandua.PMF;
import com.zorzella.tamandua.Queries;
import com.zorzella.tamandua.gwt.client.AlreadyBorrowedToThisMemberException;
import com.zorzella.tamandua.gwt.client.AlreadyReturnedException;
import com.zorzella.tamandua.gwt.client.MemberService;
import com.zorzella.tamandua.gwt.client.NotAnAdminException;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
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
    Map<Long, String> map = Members.getMap(Queries.getSortedMembers(pm));

    ItemBundle temp = new Queries(map).getDetachedFancySortedItems(pm);

    return new ItemBundle(
        Lists.newArrayList(temp.getAvailable()), 
        Lists.newArrayList(temp.getBorrowed()));
  }

//  @Override
  public void returnItem(final Long memberId, final Item item) throws AlreadyReturnedException {
    final Item liveItem = pm.getObjectById(Item.class, item.getId());
    Long currentItemParadeiro = liveItem.getParadeiro();
    if (currentItemParadeiro == null) {
      throw new AlreadyReturnedException(); 
    }
	if (!memberId.equals(currentItemParadeiro)) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is in paradeiro '%s', not in '%s'.", 
          item.getTitulo(), currentItemParadeiro, memberId));
    }
    final String adminCode = AdminOrDie.adminOrDie().getNickname();

    Loan loan;
    try {
      loan = Queries.getFirstByQuery(Loan.class, pm, 
        "memberId == " + memberId + 
        " && itemId == " + item.getId() + 
        " && returnDate == null");
    } catch (NoSuchElementException e) {
      doJob(createLoan(memberId, liveItem, adminCode));
      loan = Queries.getFirstByQuery(Loan.class, pm, 
    	        "memberId == " + memberId + 
    	        " && itemId == " + item.getId() + 
    	        " && returnDate == null");
    }

    doJob(new Runnable() {
		public void run() {
			liveItem.setParadeiro(null);
			pm.makePersistent(liveItem);
		}
	});
    
	doJob(closeLoan(adminCode, loan));    
  }

  private Runnable closeLoan(final String adminCode, final Loan loan) {
	return new Runnable() {
		public void run() {
			loan.setReturnDate(adminCode, new Date());
			pm.makePersistent(loan);
		}
	};
  }

//  @Override
  public void borrowItem(final Long memberId, final Item item) throws AlreadyBorrowedToThisMemberException {
    final Item liveItem = pm.getObjectById(Item.class, item.getId());
    Long currentParadeiro = liveItem.getParadeiro();
	  if (currentParadeiro != null) {
      if (currentParadeiro.equals(memberId)) {
        throw new AlreadyBorrowedToThisMemberException();
      }
      throw new IllegalArgumentException(String.format(
          "Item '%s' is already in paradeiro '%s'.", 
          item.getTitulo(), currentParadeiro));
    }
    
    final String adminCode = AdminOrDie.adminOrDie().getNickname();

    doJob(new Runnable() {
      public void run() {
        liveItem.setParadeiro(memberId);
        pm.makePersistent(liveItem);
      }
    });

    doJob(createLoan(memberId, item, adminCode));

  }

  private Runnable createLoan(final Long memberId, final Item item,
		final String adminCode) {
	return new Runnable() {	
		public void run() {
			Loan loan = new Loan(adminCode, memberId, item.getId());
			pm.makePersistent(loan);
		}
	};
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
    } catch (RuntimeException e) {
      currentTransaction.rollback();
      throw e;
    }
  }
  
//  @Override
  public void adminOrDie() throws NotAnAdminException {
    AdminOrDie.adminOrDie();
  }

  public void createNewMember(
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

  public void createNewItem(String itemName, String authorName, String isbn) {
    Item item = new Item(null, "Z", isbn, itemName, authorName, false, "");
    Transaction currentTransaction = pm.currentTransaction();
    currentTransaction.begin();
    
    pm.makePersistent(item);
    
    currentTransaction.commit();
	
  }
}
