package com.zorzella.tamandua.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.PMF;
import com.zorzella.tamandua.Queries;
import com.zorzella.tamandua.gwt.client.MemberService;

import java.util.Collection;

import javax.jdo.PersistenceManager;

public class MemberServiceImpl extends RemoteServiceServlet implements MemberService {

//  @Override
  public Collection<Member> getSortedMembers() {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    Collection<Member> members = Queries.getSortedMembers(pm);

    return members;
  }
}
