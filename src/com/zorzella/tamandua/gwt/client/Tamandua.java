package com.zorzella.tamandua.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

//  @Override
  public void onModuleLoad() {

    MemberServiceAsync memberService = GWT.create(MemberService.class);
    
    
    final FlexTable stocksFlexTable = new FlexTable();
    final ListBox membersDropDown = new ListBox();
    
    AsyncCallback<Collection<Member>> callback = new AsyncCallback<Collection<Member>>() {

//      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
      }

//      @Override
      public void onSuccess(Collection<Member> members) {
        for (Member member : members) {
          if (member.getNome().trim().equals("")) {
            continue;
          }
          membersDropDown.addItem(
        		  member.getCodigo() + " - " + TamanduaUtil.nome(member),
        		  member.getId().toString());
        }
        
      }};
    memberService.getSortedMembers(callback);
    
    ChangeHandler memberChangeHandler = new ChangeHandler() {
		
		public void onChange(ChangeEvent event) {
			stocksFlexTable.setText(0, 0, ((ListBox)event.getSource()).getSelectedIndex() + "");
		}
	};
	
	membersDropDown.addChangeHandler(memberChangeHandler);
    
    
    stocksFlexTable.setText(0, 0, "Symbol");

    mainPanel.add(stocksFlexTable);
    mainPanel.add(membersDropDown);
    
    RootPanel.get("list").add(mainPanel);

  }

}
