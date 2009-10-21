package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.zorzella.tamandua.Member;
import com.zorzella.tamandua.TamanduaUtil;

import java.util.Collection;

public class Tamandua implements EntryPoint {

  private VerticalPanel mainPanel = new VerticalPanel();

  @Override
  public void onModuleLoad() {

    MemberServiceAsync memberService = GWT.create(MemberService.class);
    
    
    FlexTable stocksFlexTable = new FlexTable();
    final ListBox membersDropDown = new ListBox();
    
    AsyncCallback<Collection<Member>> callback = new AsyncCallback<Collection<Member>>() {

      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
      }

      @Override
      public void onSuccess(Collection<Member> members) {
        for (Member member : members) {
          if (member.getNome().trim().equals("")) {
            continue;
          }
          membersDropDown.addItem(member.getId().toString(), 
              member.getCodigo() + " - " + TamanduaUtil.nome(member));
        }
        
      }};
    memberService.getSortedMembers(callback);
    
    
    
    stocksFlexTable.setText(0, 0, "Symbol");

    mainPanel.add(stocksFlexTable);
    mainPanel.add(membersDropDown);
    
    RootPanel.get("list").add(mainPanel);

  }

}
