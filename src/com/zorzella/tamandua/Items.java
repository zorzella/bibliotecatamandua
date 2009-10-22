package com.zorzella.tamandua;

public class Items {

  static String strip(String toStrip) {
    if (toStrip == null) {
      return "";
    }
    String lowerCaseToStrip = toStrip.toLowerCase();
    if (
        (lowerCaseToStrip.startsWith("a ")) ||
        (lowerCaseToStrip.startsWith("o "))) {
      return toStrip.substring(2);
    }
    if (
        (lowerCaseToStrip.startsWith("as ")) ||
        (lowerCaseToStrip.startsWith("os ")) ||
        (lowerCaseToStrip.startsWith("um "))) {
      return toStrip.substring(3);
    }
    if (
        (lowerCaseToStrip.startsWith("uma ")) ||
        (lowerCaseToStrip.startsWith("uns "))) {
      return toStrip.substring(4);
    }
    if (lowerCaseToStrip.startsWith("umas ")) {
      return toStrip.substring(5);
    }
    return toStrip;
  }

  public static final ItemComparator ITEM_COMPARATOR = new ItemComparator();

  public static String getStrippedTitle(Item item) {
    return Items.strip(item.getTitulo());
  }
}
