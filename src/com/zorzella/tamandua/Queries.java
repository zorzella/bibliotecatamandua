// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Collection;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

public class Queries {

  @SuppressWarnings("unchecked")
  public static Collection<Book> getSortedBooks(PersistenceManager pm) {
    Collection<Book> result = new TreeSet<Book>();
    result.addAll((Collection<Book>)pm.newQuery(Book.class).execute());
    return result;
  }

  @SuppressWarnings("unchecked")
  public static Collection<Book> getFancySortedBooks(PersistenceManager pm) {
    Collection<Book> result = new TreeSet<Book>(new FancyMemberComparator());
    result.addAll((Collection<Book>)pm.newQuery(Book.class).execute());
    return result;
  }
  
  @SuppressWarnings("unchecked")
  public static Collection<Member> getSortedMembers(PersistenceManager pm) {
    Collection<Member> result = new TreeSet<Member>();
    result.addAll((Collection<Member>)pm.newQuery(Member.class).execute());
    return result;
  }

}
