// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Collection;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

public class Queries {

  public static Collection<Book> getSortedBooks(PersistenceManager pm) {
    return new TreeSet<Book>(allBooks(pm));
  }

  public static Collection<Book> getUnSortedBooks(PersistenceManager pm) {
    return allBooks(pm);
  }

  public static Collection<Book> getFancySortedBooks(PersistenceManager pm) {
    Collection<Book> result = new TreeSet<Book>(new FancyMemberComparator());
    result.addAll(allBooks(pm));
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Collection<Book> allBooks(PersistenceManager pm) {
    return (Collection<Book>)pm.newQuery(Book.class).execute();
  }
  
  public static Collection<Member> getSortedMembers(PersistenceManager pm) {
    return new TreeSet<Member>(allMembers(pm));
  }

  @SuppressWarnings("unchecked")
  private static Collection<Member> allMembers(PersistenceManager pm) {
    return (Collection<Member>)pm.newQuery(Member.class).execute();
  }
}
