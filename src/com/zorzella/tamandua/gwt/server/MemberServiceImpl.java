package com.zorzella.tamandua.gwt.server;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.zorzella.tamandua.BorrowReturnServlet;
import com.zorzella.tamandua.ItemBundle;
import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.PMF;
import com.zorzella.tamandua.Queries;
import com.zorzella.tamandua.gwt.client.MemberService;

import java.util.Collection;
import java.util.Map;

import javax.jdo.PersistenceManager;

public class MemberServiceImpl extends RemoteServiceServlet implements MemberService {

//  @Override
  public Collection<Member> getSortedMembers() {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    Collection<Member> members = Queries.getSortedMembers(pm);

    return members;
  }

  public ItemBundle getFancySortedItems() {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    Map<Long, String> map = BorrowReturnServlet.getMap(getSortedMembers());

    ItemBundle temp = new Queries(map).getFancySortedItems(pm);
    
    return new ItemBundle(
        Lists.newArrayList(temp.getAvailable()), 
        Lists.newArrayList(temp.getBorrowed()));
  }
}
