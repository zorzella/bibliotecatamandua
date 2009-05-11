// Copyright 2008 Google Inc.  All Rights Reserved.
package com.zorzella.tamandua;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

public class Queries {
  
  public static final class AutorComparator implements Comparator<Item> {

    @Override
    public int compare(Item one, Item other) {
      String oneAutor = one.getAutor();
      String otherAutor = other.getAutor();
      if (oneAutor.equals(otherAutor)) {
        return one.compareTo(other);
      }
      return oneAutor.compareTo(otherAutor);
    }
  }

  public static final class ParadeiroComparator implements Comparator<Item> {

    @Override
    public int compare(Item one, Item other) {
      String oneParadeiro = one.getParadeiro();
      String otherParadeiro = other.getParadeiro();
      if (oneParadeiro.equals(otherParadeiro)) {
        return one.compareTo(other);
      }
      return oneParadeiro.compareTo(otherParadeiro);
    }
  }

  public static final class TocaComparator implements Comparator<Item> {

    @Override
    public int compare(Item one, Item other) {
      String oneToca = one.getToca();
      String otherToca = other.getToca();
      if (oneToca.equals(otherToca)) {
        return one.compareTo(other);
      }
      return oneToca.compareTo(otherToca);
    }
  }

  public static Collection<Item> getSortedItems(PersistenceManager pm) {
    return new TreeSet<Item>(allBooks(pm));
  }
  
  public static Collection<Item> getParadeiroSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new ParadeiroComparator());
    result.addAll(allBooks(pm));
    return result;
  }

  public static Collection<Item> getTocaSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new TocaComparator());
    result.addAll(allBooks(pm));
    return result;
  }
  
  public static Collection<Item> getAutorSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new AutorComparator());
    result.addAll(allBooks(pm));
    return result;
  }

  public static Collection<Item> getUnSortedItems(PersistenceManager pm) {
    return allBooks(pm);
  }

  public static Collection<Item> getFancySortedBooks(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new FancyMemberComparator());
    result.addAll(allBooks(pm));
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Collection<Item> allBooks(PersistenceManager pm) {
    return (Collection<Item>)pm.newQuery(Item.class).execute();
  }
  
  public static Collection<Member> getSortedMembers(PersistenceManager pm) {
    return new TreeSet<Member>(allMembers(pm));
  }

  @SuppressWarnings("unchecked")
  private static Collection<Member> allMembers(PersistenceManager pm) {
    return (Collection<Member>)pm.newQuery(Member.class).execute();
  }

  public static <T> T getById(Class<T> clazz, PersistenceManager pm, String idFieldName, String idValue) {
  return getFirstByQuery(clazz, pm, idFieldName + " == " + idValue);
  }

  public static <T> T getFirstByQuery(Class<T> clazz, PersistenceManager pm, String query, Object... args) {
  return getByQuery(clazz, pm, query, args)
      .iterator().next();
  }

  @SuppressWarnings("unchecked")
  public static <T> Collection<T> getByQuery(Class<T> clazz,
      PersistenceManager pm, String query, Object... args) {
  return ((Collection<T>)pm.newQuery(clazz, query).execute(args));
  }

  @SuppressWarnings("unchecked")
  public static <T> Collection<T> getAll(Class<T> clazz,
      PersistenceManager pm) {
  return ((Collection<T>)pm.newQuery(clazz).execute());
  }
}
