// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

public class Queries {
  
  public static final class AutorComparator implements Comparator<Book> {

    @Override
    public int compare(Book one, Book other) {
      String oneAutor = one.getAutor();
      String otherAutor = other.getAutor();
      if (oneAutor.equals(otherAutor)) {
        return one.compareTo(other);
      }
      return oneAutor.compareTo(otherAutor);
    }
  }

  public static final class ParadeiroComparator implements Comparator<Book> {

    @Override
    public int compare(Book one, Book other) {
      String oneParadeiro = one.getParadeiro();
      String otherParadeiro = other.getParadeiro();
      if (oneParadeiro.equals(otherParadeiro)) {
        return one.compareTo(other);
      }
      return oneParadeiro.compareTo(otherParadeiro);
    }
  }

  public static final class TocaComparator implements Comparator<Book> {

    @Override
    public int compare(Book one, Book other) {
      String oneToca = one.getToca();
      String otherToca = other.getToca();
      if (oneToca.equals(otherToca)) {
        return one.compareTo(other);
      }
      return oneToca.compareTo(otherToca);
    }
  }

  public static Collection<Book> getSortedItems(PersistenceManager pm) {
    return new TreeSet<Book>(allBooks(pm));
  }
  
  public static Collection<Book> getParadeiroSortedItems(PersistenceManager pm) {
    Collection<Book> result = new TreeSet<Book>(new ParadeiroComparator());
    result.addAll(allBooks(pm));
    return result;
  }

  public static Collection<Book> getTocaSortedItems(PersistenceManager pm) {
    Collection<Book> result = new TreeSet<Book>(new TocaComparator());
    result.addAll(allBooks(pm));
    return result;
  }
  
  public static Collection<Book> getAutorSortedItems(PersistenceManager pm) {
    Collection<Book> result = new TreeSet<Book>(new AutorComparator());
    result.addAll(allBooks(pm));
    return result;
  }

  public static Collection<Book> getUnSortedItems(PersistenceManager pm) {
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
