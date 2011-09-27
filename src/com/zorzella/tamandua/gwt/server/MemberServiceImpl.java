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
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public class MemberServiceImpl extends RemoteServiceServlet implements MemberService {

  private final PersistenceManagerFactory pmf;

  public MemberServiceImpl() {
    pmf = PMF.get();
  }
  
  @Override
  public Collection<Member> getSortedMembers() {
    PersistenceManager pm = pm();
    SortedSet<Member> members = Queries.getSortedMembers(pm);
    
    Collection<Member> result = Lists.newArrayList();
    for (Member member : members) {
      result.add(pm.detachCopy(member));
    }
    return result;
  }

  private PersistenceManager pm() {
    return pmf.getPersistenceManager();
  }

  @Override
  public ItemBundle getFancySortedItems() {
    PersistenceManager pm = pm();
    Map<Long, String> map = Members.getMap(Queries.getSortedMembers(pm));

    ItemBundle temp = new Queries(map).getDetachedFancySortedItems(pm);

    return new ItemBundle(
        Lists.newArrayList(temp.getAvailable()), 
        Lists.newArrayList(temp.getBorrowed()));
  }

  @Override
  public void returnItem(final Long memberId, final Item item) throws AlreadyReturnedException {
    Long currentItemParadeiro = item.getParadeiro();
    if (currentItemParadeiro == null) {
      throw new AlreadyReturnedException(); 
    }
	if (!memberId.equals(currentItemParadeiro)) {
      throw new IllegalArgumentException(String.format(
          "Item '%s' is in paradeiro '%s', not in '%s'.", 
          item.getTitulo(), currentItemParadeiro, memberId));
    }
    final String adminCode = AdminOrDie.adminOrDie().getNickname();

    doJob(new Job<Void>() {
		public Void run(PersistenceManager pm) {
		  Item liveItem = pm.getObjectById(Item.class, item.getId());
		  liveItem.setParadeiro(null);
		  pm.makePersistent(liveItem);
          return null;
		}
	});
    
	doJob(closeLoan(adminCode, memberId, item));    
  }

  private Job<Void> closeLoan(final String adminCode, final Long memberId, final Item item) {
	return new Job<Void>() {
		public Void run(PersistenceManager pm) {
	
		    Loan loan;
		    try {
		      loan = Queries.getFirstByQuery(Loan.class, pm, 
		        "memberId == " + memberId + 
		        " && itemId == " + item.getId() + 
		        " && returnDate == null");
		    } catch (NoSuchElementException e) {
		      doJob(createLoan(memberId, item, adminCode));
		      loan = Queries.getFirstByQuery(Loan.class, pm, 
		                "memberId == " + memberId + 
		                " && itemId == " + item.getId() + 
		                " && returnDate == null");
		    }

		  loan.setReturnDate(adminCode, new Date());
			pm.makePersistent(loan);
          return null;
		}
	};
  }

  @Override
  public void borrowItem(final Long memberId, final Item item) throws AlreadyBorrowedToThisMemberException {
    Long currentParadeiro = item.getParadeiro();
	  if (currentParadeiro != null) {
      if (currentParadeiro.equals(memberId)) {
        throw new AlreadyBorrowedToThisMemberException();
      }
      throw new IllegalArgumentException(String.format(
          "Item '%s' is already in paradeiro '%s'.", 
          item.getTitulo(), currentParadeiro));
    }
    
    final String adminCode = AdminOrDie.adminOrDie().getNickname();

    doJob(new Job<Void>() {
      public Void run(PersistenceManager pm) {
        final Item liveItem = pm.getObjectById(Item.class, item.getId());
        liveItem.setParadeiro(memberId);
        pm.makePersistent(liveItem);
        return null;
      }
    });

    doJob(createLoan(memberId, item, adminCode));
  }

  private Job<Void> createLoan(final Long memberId, final Item item,
		final String adminCode) {
	return new Job<Void>() {	
		public Void run(PersistenceManager pm) {
			Loan loan = new Loan(adminCode, memberId, item.getId());
			pm.makePersistent(loan);
			return null;
		}
	};
  }

  private <T> T doJob(Job<T> job) {
    T result;
    PersistenceManager pm = pm();
    Transaction currentTransaction = pm.currentTransaction();
    try {
      currentTransaction.begin();
      result = job.run(pm);
      currentTransaction.commit();
    } catch (RuntimeException e) {
      currentTransaction.rollback();
      throw e;
    }
    pm.close();
    return result;
  }
  
  @Override
  public void softAdminOrDie() throws NotAnAdminException {
    AdminOrDie.softAdminOrDie();
  }

  @Override
  public void createNewMember(
      String parentName, 
      String childFirstName, 
      String childLastName,
      String code, 
      String email) {
    
    final Member member = new Member(code);
    member.setMae(parentName);
    member.setNome(childFirstName);
    member.setSobrenome(childLastName);
    member.setEmail(email);
    
    doJob(new Job<Void>() {
      
      @Override
      public Void run(PersistenceManager pm) {
        pm.makePersistent(member);
        return null;
      }
    });
  }

  @Override
  public void editItem(
      final Long itemId,
      final String toca,
      final String itemName,
      final String authorName,
      final String publishingHouse,
      final String tamanho,
      final String tags,
      final String isbn) {
    doJob(new Job<Void>() {
      
      @Override
      public Void run(PersistenceManager pm) {
        final Item item = pm.getObjectById(Item.class, itemId);
        item.setToca(toca);
        item.setTitulo(itemName);
        item.setAutor(authorName);
        item.setPublishingHouse(publishingHouse);
        item.setIsbn(isbn);
        item.setTamanho(tamanho);
        item.getTags().clear();
        item.addTags(tags);
        pm.makePersistent(item);
        return null;
      }
    });
  }
  
  @Override
  public void createNewItem(
      String toca,
      String itemName,
      String authorName,
      String publishingHouse,
      String tamanho,
      String tags,
      String isbn) {
    final Item item = new Item(null, toca, isbn, itemName, authorName,
        publishingHouse, false, tamanho);
    item.addTags(tags);
    
    doJob(new Job<Void>() {
      @Override
      public Void run(PersistenceManager pm) {
        pm.makePersistent(item);
        return null;
      }
    });
  }

  @Override
  public void bulkUpload(String csvData) {
    for (String csvLine : csvData.split("\n")) {
      String[] parts = csvLine.split(",");
      final Item item = new Item(null, parts[0], "", parts[1], parts[2],
          "", false, "");
      item.addTag(parts[2]);
      
      doJob(new Job<Void> () {

        @Override
        public Void run(PersistenceManager pm) {
          pm.makePersistent(item);
          return null;
        }
      });
    }
  }

  interface Job<T> {
    T run(PersistenceManager pm);
  }
}
