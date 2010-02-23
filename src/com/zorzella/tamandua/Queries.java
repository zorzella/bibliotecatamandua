package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Sets;

import com.zorzella.tamandua.Item.Type;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

public class Queries {
  
  public static final class AutorComparator implements Comparator<Item> {

//    @Override
    public int compare(Item one, Item other) {
      String oneAutor = one.getAutor();
      String otherAutor = other.getAutor();
      if (oneAutor.equals(otherAutor)) {
        return Items.ITEM_COMPARATOR.compare(one, other);
      }
      return oneAutor.compareTo(otherAutor);
    }
  }

  public static final class TypeComparator implements Comparator<Item> {

//    @Override
    public int compare(Item one, Item other) {
      Type oneType = one.getType();
      Type otherType = other.getType();
      if (oneType.equals(otherType)) {
        return Items.ITEM_COMPARATOR.compare(one, other);
      }
      return oneType.compareTo(otherType);
    }
  }

  public static final class ParadeiroComparator implements Comparator<Item> {

//    @Override
    public int compare(Item one, Item other) {
//      String oneParadeiro = one.getParadeiro();
//      String otherParadeiro = other.getParadeiro();
//      if (oneParadeiro.equals(otherParadeiro)) {
//        return one.compareTo(other);
//      }
//      return oneParadeiro.compareTo(otherParadeiro);
      
    Long oneParadeiro = one.getParadeiro();
    Long otherParadeiro = other.getParadeiro();
    if ((oneParadeiro != null) && (oneParadeiro.equals(otherParadeiro))) {
      return Items.ITEM_COMPARATOR.compare(one, other);
    }
    if ((oneParadeiro == null) && (otherParadeiro == null)) {
      return Items.ITEM_COMPARATOR.compare(one, other);
    }
    if (otherParadeiro == null) {
      return -1;
    }
    if (oneParadeiro == null) {
      return 1;
    }
    return oneParadeiro.compareTo(otherParadeiro);
      
    }
  }

  public static final class TocaComparator implements Comparator<Item> {

//    @Override
    public int compare(Item one, Item other) {
      String oneToca = one.getToca();
      String otherToca = other.getToca();
      if (oneToca.equals(otherToca)) {
        return Items.ITEM_COMPARATOR.compare(one, other);
      }
      return oneToca.compareTo(otherToca);
    }
  }

  public static Collection<Item> getSortedItems(PersistenceManager pm) {
    return Sets.newTreeSet(Items.ITEM_COMPARATOR, allItems(pm));
  }
  
  public static Collection<Item> getParadeiroSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new ParadeiroComparator());
    result.addAll(allItems(pm));
    return result;
  }

  public static Collection<Item> getTocaSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new TocaComparator());
    result.addAll(allItems(pm));
    return result;
  }
  
  public static Collection<Item> getTypeSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new TypeComparator());
    result.addAll(allItems(pm));
    return result;
  }
  
  public static Collection<Item> getAutorSortedItems(PersistenceManager pm) {
    Collection<Item> result = new TreeSet<Item>(new AutorComparator());
    result.addAll(allItems(pm));
    return result;
  }

  public static Collection<Item> getUnSortedItems(PersistenceManager pm) {
    return allItems(pm);
  }

private final Map<Long, String> paradeiroToCodeMap;

  public Queries(Map<Long,String> paradeiroToCodeMap) {
	  this.paradeiroToCodeMap = paradeiroToCodeMap;
  }
  
  public ItemBundle getFancySortedItems(PersistenceManager pm) {
    Collection<Item> borrowed = 
      new TreeSet<Item>(new FancyItemComparator(paradeiroToCodeMap));
    Collection<Item> available = 
      new TreeSet<Item>(new FancyItemComparator(paradeiroToCodeMap));
    for (Item item : allValidItems(pm)) {
      if (item.getParadeiro() == null) {
        available.add(item);
      } else {
        borrowed.add(item);
      }
    }
    return new ItemBundle(available, borrowed);
  }

  public ItemBundle getDetachedFancySortedItems(PersistenceManager pm) {
    Collection<Item> borrowed = 
      new TreeSet<Item>(new FancyItemComparator(paradeiroToCodeMap));
    Collection<Item> available = 
      new TreeSet<Item>(new FancyItemComparator(paradeiroToCodeMap));
    for (Item item : allValidItems(pm)) {
      if (item.getParadeiro() == null) {
        available.add(pm.detachCopy(item));
      } else {
        borrowed.add(pm.detachCopy(item));
      }
    }
    return new ItemBundle(available, borrowed);
  }
  
  @SuppressWarnings("unchecked")
  private static Collection<Item> allValidItems(PersistenceManager pm) {
    Collection<Item> temp = (Collection<Item>)pm.newQuery(Item.class).execute();
    Collection<Item> result = Lists.newArrayList();
    for (Item item : temp) {
      if (!item.getTitulo().trim().equals("")) {
        result.add(item);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Collection<Item> allItems(PersistenceManager pm) {
    return (Collection<Item>)pm.newQuery(Item.class).execute();
  }
  
  public static Collection<Member> getSortedMembersWithBlanks(PersistenceManager pm) {
    return new TreeSet<Member>(allMembers(pm));
  }

  public static SortedSet<Member> getSortedMembers(PersistenceManager pm) {
    return new TreeSet<Member>(allValidMembers(pm));
  }

  @SuppressWarnings("unchecked")
  private static Collection<Member> allValidMembers(PersistenceManager pm) {
    Collection<Member> temp = (Collection<Member>)pm.newQuery(Member.class).execute();
    Collection<Member> result = Lists.newArrayList();
    for (Member member : temp) {
      if (!member.getCodigo().trim().equals("")) {
        result.add(member);
      }
    }
    return result;
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
