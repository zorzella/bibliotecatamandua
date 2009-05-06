package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import com.ibm.icu.text.Collator;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Book implements Comparable<Book> {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String paradeiro;
    
    @Persistent
    private String toca;
    
    @Persistent
    private String titulo;
    
    @Persistent
    private String isbn;
    
    @Persistent
    private String autor;
    
    @Persistent
    private Boolean especial;
    
    @Persistent
    private String tamanho;
    
    @Persistent
    private List<String> tags;
    
    public Book(
        String paradeiro, 
        String toca, 
        String isbn, 
        String titulo,
        String autor,
        boolean especial, 
        String tamanho) {
      super();
      this.paradeiro = paradeiro;
      this.toca = toca;
      this.titulo = titulo;
      this.isbn = isbn;
      this.autor = autor;
      this.especial = especial;
      this.tamanho = tamanho;
      this.tags = Lists.newArrayList();
    }

    public Long getId() {
      return id;
    }
    
    public String getParadeiro() {
      return paradeiro;
    }

    public void addTag(String tag) {
      tags.add(tag);
    }

    public void removeTag(String tag) {
      tags.remove(tag);
    }
    
    public void setParadeiro(String paradeiro) {
      this.paradeiro = paradeiro;
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

    @SuppressWarnings("unchecked")
    private List<String> getTags() {
      if (tags == null) {
        return Collections.EMPTY_LIST;
      }
      return tags;
    }
    
    @Override
    public String toString() {
      return String.format(
          "%s,%s,%s,%s,%s,%s,%s,%s,%s",
          id, 
          paradeiro, 
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
      maybeAdd(result, "paradeiro", paradeiro);
      maybeAdd(result, "toca", toca);
      maybeAdd(result, "isbn", isbn);
      maybeAdd(result, "titulo", titulo);
      maybeAdd(result, "autor", autor);
      maybeAdd(result, "tamanho", tamanho);
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
    
    private static final Collator collator = Collator.getInstance(Locale.US);
    
    @Override
    public int compareTo(Book o) {
      return collator.compare(titulo, o.titulo);
    }
}