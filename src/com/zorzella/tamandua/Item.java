package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import com.ibm.icu.text.Collator;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Item implements Comparable<Item> {

  public enum Type {
    BOOK,
    MUSIC_CD,
    MOVIE_DVD,
    COMPUTER_GAME_CD,    
  }

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private Type type;

  @Persistent
  private Long paradeiroLong;

  @Persistent
  private String toca;

  @Persistent
  private String titulo;

  @Persistent
  private String barcode;
  
  @Persistent
  private String isbn;

  @Persistent
  private String autor;

  @Persistent
  private Boolean especial;

  @Persistent
  private String tamanho;

  @Persistent
  private Date desde;

  @Persistent
  private List<String> tags;

  public Item() {
    this.desde = new Date();
    this.titulo = "";
    this.autor = "";
    this.paradeiroLong = null;
    this.toca = "";
    this.isbn = "";
    this.barcode = "";
    this.tamanho = "";
    this.tags = Lists.newArrayList();
    this.especial = false;
  }

  public Item(
      Long paradeiro, 
      String toca, 
      String isbn, 
      String titulo,
      String autor,
      boolean especial, 
      String tamanho) {
    super();
    this.type = Type.BOOK;
    this.paradeiroLong = paradeiro;
    this.toca = toca;
    this.titulo = titulo;
    this.isbn = isbn;
    this.autor = autor;
    this.especial = especial;
    this.tamanho = tamanho;
    this.tags = Lists.newArrayList();
    this.desde = new Date();
  }

  public Long getId() {
    return id;
  }

  public Long getParadeiro() {
    return paradeiroLong;
  }

  public void addTag(String tag) {
    if (tag == null) {
      throw new NullPointerException();
    }
    if (tags == null) {
      tags = Lists.newArrayList();
    }
    tags.add(tag);
  }

  public void removeTag(String tag) {
    tags.remove(tag);
  }

  public void setParadeiro(Long paradeiro) {
    this.paradeiroLong = paradeiro;
  }

  public String getToca() {
    return toca;
  }

  public void setToca(String toca) {
    this.toca = toca;
  }

  public String getIsbn() {
    return isbn;
  }
  
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }
  
  public String getBarcode() {
    if (barcode == null) {
      return "";
    }
    return barcode;
  }
  
  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Type getType() {
    if (type == null) {
      return Type.BOOK;
    }
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }
  
  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    this.autor = autor;
  }

  public boolean isEspecial() {
    return especial;
  }

  public String getTamanho() {
    return tamanho;
  }

  public void setTamanho(String tamanho) {
    this.tamanho = tamanho;
  }

  @SuppressWarnings("unchecked") List<String> getTags() {
    // Bug in local DB impl causes tags to be null when they should be empty
    if (tags == null) {
      return Collections.EMPTY_LIST;
    }
    // Bug in prod DB impl causes tags to be have a null when they should be empty
    if ((tags.size() == 1) && (tags.iterator().next() == null)) {
      return Collections.EMPTY_LIST;
    }
    return tags;
  }

  @Override
  public String toString() {
    return String.format(
        "%s,%s,%s,%s,%s,%s,%s,%s,%s",
        id, 
        paradeiroLong, 
        toca, 
        isbn,
        quote(titulo), 
        quote(autor), 
        especial,
        tamanho, 
        getTags());
  }

  public String toDebugString() {
    StringBuilder result = new StringBuilder("id:" + id + " ");

    if (especial) {
      result.append("<especial> ");
    }
    maybeAdd(result, "paradeiro", paradeiroLong + "");
    maybeAdd(result, "toca", toca);
    maybeAdd(result, "isbn", isbn);
    maybeAdd(result, "titulo", titulo);
    maybeAdd(result, "autor", autor);
    maybeAdd(result, "tamanho", tamanho);
    maybeAdd(result, "desde", Dates.dateToString(desde));
    result.append("tags:" + getTags());
    return result.toString();
  }

  private void maybeAdd(StringBuilder result, String key, String value) {
    if (value.trim().length() > 0) {
      result.append(key + ":[" + value + "] ");
    }
  }

  private static String quote(String string) {
    int commaAt = string.indexOf(',');
    if (commaAt == -1) {
      return string;
    }
    return '"' + string + '"';
  }

  public String getStrippedTitle() {
    return strip(getTitulo());
  }
  
  private static final Collator collator = Collator.getInstance(Locale.US);

  @Override
  public int compareTo(Item that) {
    String tituloOne = strip(titulo);
    String tituloOther = strip(that.titulo);
    int col = collator.compare(tituloOne, tituloOther);
    if (col != 0) {
      return col;
    }
    if (this.getId() > that.getId()) {
      return 1;
    }
    return -1;
  }

  private String strip(String toStrip) {
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
}