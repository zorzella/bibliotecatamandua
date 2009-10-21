package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;

import com.zorzella.tamandua.Member;

import java.util.Collection;

public interface MemberService extends RemoteService {

  Collection<Member> getSortedMembers();

  
}
