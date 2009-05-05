package com.zorzella.tamandua;

import com.ibm.icu.text.Collator;

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
    private String autor;
    
    @Persistent
    private boolean pagGrossa;
    
    @Persistent
    private boolean rima;
    
    @Persistent
    private String tamanho;
    
    @Persistent
    private String desc;
    
    @Persistent
    private String nota;

    public Book(String paradeiro, String toca, String titulo, String autor,
        boolean pagGrossa, boolean rima, String tamanho, String desc, String nota) {
      super();
      this.paradeiro = paradeiro;
      this.toca = toca;
      this.titulo = titulo;
      this.autor = autor;
      this.pagGrossa = pagGrossa;
      this.rima = rima;
      this.tamanho = tamanho;
      this.desc = desc;
      this.nota = nota;
    }

    public Long getId() {
      return id;
    }
    
    public String getParadeiro() {
      return paradeiro;
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

    public boolean isPagGrossa() {
      return pagGrossa;
    }

    public void setPagGrossa(boolean pagGrossa) {
      this.pagGrossa = pagGrossa;
    }

    public boolean isRima() {
      return rima;
    }

    public void setRima(boolean rima) {
      this.rima = rima;
    }

    public String getTamanho() {
      return tamanho;
    }

    public void setTamanho(String tamanho) {
      this.tamanho = tamanho;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    public String getNota() {
      return nota;
    }

    public void setNota(String nota) {
      this.nota = nota;
    }
    
    @Override
    public String toString() {
      return String.format(
          "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
          id, 
          paradeiro, 
          toca, 
          quote(titulo), 
          quote(autor), 
          pagGrossa, 
          rima, 
          tamanho, 
          desc,
          nota);
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