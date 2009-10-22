package com.zorzella.tamandua;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Item implements Serializable {

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
    this.tags = new ArrayList<String>();
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
    this.tags = new ArrayList<String>();
    this.desde = new Date();
  }

  public Long getId() {
    return id;
  }

  public Long getParadeiro() {
    return paradeiroLong;
  }

  public Date getDesde() {
    return desde;
  }
  
  public void addTag(String tag) {
    if (tag == null) {
      throw new NullPointerException();
    }
    if (tags == null) {
      tags = new ArrayList<String>();
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
    return GwtSupport.format(
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

  

  private static String quote(String string) {
    int commaAt = string.indexOf(',');
    if (commaAt == -1) {
      return string;
    }
    return '"' + string + '"';
  }

  
//  private static final Collator collator = Collator.getInstance(Locale.US);

//  @Override
//  public int compareTo(Item that) {
//    String tituloOne = Items.strip(titulo);
//    String tituloOther = Items.strip(that.titulo);
//    int col = collator.compare(tituloOne, tituloOther);
//    if (col != 0) {
//      return col;
//    }
//    if (this.getId() > that.getId()) {
//      return 1;
//    }
//    return -1;
//  }
}