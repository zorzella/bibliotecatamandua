package com.zorzella.tamandua.gwt.client;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MembersDropDown extends Composite implements HasChangeHandlers {

  private Collection<Member> members;
  private final Map<Long, Member> memberIdToCodeMap = new HashMap<Long, Member>();
  private final ListBox listBox = new ListBox();
  
  public MembersDropDown() {
    initWidget(listBox);
    listBox.addItem("");
  }
  
  public void setMembers(Collection<Member> members) {
    this.members = members;
    for (Member member : members) {
      memberIdToCodeMap.put(member.getId(), member);
    }
  }

  public void setSelectedMember(String code) {
    listBox.setSelectedIndex(getIndexForMemberCode(code));
  }
  
  public int getIndexForMemberCode(String code) {
    for (int i=0 ; i < listBox.getItemCount() ; i++) {
      String idToCode = idToCode(listBox.getValue(i));
      if (idToCode.equals(code)) {
        return i;
      }
    }
    return 0;
  }
  
  public Member getSelectedMember() {
    int index = listBox.getSelectedIndex();
    String value = listBox.getValue(index);
    if (value.equals("")) {
      return null;
    }
    return memberIdToCodeMap.get(Long.valueOf(value));
  }
  
  public String idToCode(String id) {
    if (id.equals("")) {
      return "";
    }
    return idToCode(Long.valueOf(id));
  }
  
  public String idToCode(Long id) {
    return memberIdToCodeMap.get(id).getCodigo();
  }

  public void refresh() {
    listBox.clear();
    listBox.addItem("");
    for (Member member : members) {
      listBox.addItem(
        member.getCodigo() + " - " + TamanduaUtil.nome(member),
        member.getId().toString());
    }
  }
  
  public boolean memberExistsWithCode(String code) {
    for (Member member : members) {
      if (member.getCodigo().equalsIgnoreCase(code.trim())) {
        return true;
      }
    }
    return false;
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return listBox.addChangeHandler(handler);
  }
}