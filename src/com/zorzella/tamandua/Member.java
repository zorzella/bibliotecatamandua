package com.zorzella.tamandua;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Member implements Comparable<Member> {

  @PrimaryKey
  //    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private String codigo;
  @Persistent
  private String nome;
  @Persistent
  private String sobrenome;
  @Persistent
  private Date nascimento;
  @Persistent
  private String email;
  @Persistent
  private String pai;
  @Persistent
  private String mae;
  @Persistent
  private String fone;
  @Persistent
  private int dolares;
  @Persistent
  private int livrosDoados;
  @Persistent
  private boolean confirmado;
  @Persistent
  private Date desde;

  public Member(
      String codigo, 
      String nome, 
      String sobrenome, 
      Date nascimento, 
      String email, 
      String pai, 
      String mae, 
      String fone, 
      int dolares, 
      int livrosDoados, 
      boolean confirmado, 
      Date desde) {
    this.codigo = codigo;
    this.nome = nome;
    this.sobrenome = sobrenome;
    this.nascimento = nascimento;
    this.email = email;
    this.pai = pai;
    this.mae = mae;
    this.fone = fone;
    this.dolares = dolares;
    this.livrosDoados = livrosDoados;
    this.confirmado = confirmado;
    this.desde = desde;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNome() {
    return nome;
  }

  public String getSobrenome() {
    return sobrenome;
  }

  public Date getNascimento() {
    return nascimento;
  }

  public String getEmail() {
    return email;
  }

  public String getPai() {
    return pai;
  }

  public String getMae() {
    return mae;
  }

  public String getFone() {
    return fone;
  }

  public int getDolares() {
    return dolares;
  }

  public int getLivrosDoados() {
    return livrosDoados;
  }

  public boolean isConfirmado() {
    return confirmado;
  }

  public Date getDesde() {
    return desde;
  }

  @Override
  public String toString() {
    return String.format(
        "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
        codigo, 
        nome, 
        sobrenome, 
        nascimento, 
        email, 
        pai, 
        mae, 
        fone, 
        dolares, 
        livrosDoados, 
        confirmado, 
        desde
    );
  }

  @Override
  public int compareTo(Member o) {
    return codigo.compareTo(o.codigo);
  }
}