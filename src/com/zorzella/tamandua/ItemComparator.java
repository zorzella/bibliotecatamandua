package com.zorzella.tamandua;

import com.ibm.icu.text.Collator;

import java.util.Comparator;
import java.util.Locale;

public class ItemComparator implements Comparator<Item> {

  private static final Collator collator = Collator.getInstance(Locale.US);

//  @Override
  public int compare(Item one, Item other) {
    String tituloOne = Items.strip(one.getTitulo());
    String tituloOther = Items.strip(other.getTitulo());
    int col = collator.compare(tituloOne, tituloOther);
    if (col != 0) {
      return col;
    }
    if (one.getId() > other.getId()) {
      return 1;
    }
    return -1;
  }
}
