package com.zorzella.tamandua;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Member implements Comparable<Member> {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
  @Persistent
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
  private String fone2;
  @Persistent
  private int dolares;
  @Persistent
  private int livrosDoados;
  @Persistent
  private boolean confirmado;
  @Persistent
  private Date lastContacted;
  @Persistent
  private Date desde;
  @Persistent
  private String endereco;
  @Persistent
  private String cidade;
  @Persistent
  private String estado;
  @Persistent
  private String zip;

  public Member(String codigo) {
    this(codigo, "", "", null, "", "", "", "", "", "", "", "", "", 0, 0, null, false, new Date());
  }
  
  public Member(
      String codigo, 
      String nome, 
      String sobrenome, 
      Date nascimento, 
      String email, 
      String pai, 
      String mae,
      String endereco,
      String cidade,
      String estado,
      String zip,
      String fone, 
      String fone2, 
      int dolares, 
      int livrosDoados, 
      Date lastContacted,
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
    this.fone2 = fone2;
    this.dolares = dolares;
    this.livrosDoados = livrosDoados;
    this.confirmado = confirmado;
    this.desde = desde;
    this.endereco = endereco;
    this.estado = estado;
    this.cidade = cidade;
    this.zip = zip;

  }

  public Long getId() {
  return id;
  }
  
  public String getCodigo() {
  if (codigo == null) {
    return "";
  }
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }
  
  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getSobrenome() {
    return sobrenome;
  }

  public void setSobrenome(String sobrenome) {
    this.sobrenome = sobrenome;
  }
  
  public Date getNascimento() {
    return nascimento;
  }

  public void setNascimento(Date nascimento) {
    this.nascimento = nascimento;
  }
  
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getPai() {
    return pai;
  }

  public void setPai(String pai) {
    this.pai = pai;
  }
  
  public String getMae() {
    return mae;
  }

  public void setMae(String mae) {
    this.mae = mae;
  }
  
  public String getFone() {
    return fone;
  }

  public void setFone(String fone) {
    this.fone = fone;
  }
  
  public String getFone2() {
    return fone2;
  }
  
  public void setFone2(String fone2) {
    this.fone2 = fone2;
  }
  
  public int getDolares() {
    return dolares;
  }

  public void setDolares(int dolares) {
    this.dolares = dolares;
  }
  
  public void setDesde(Date desde) {
    this.desde = desde;
  }
  
  public int getLivrosDoados() {
    return livrosDoados;
  }

  public void setLivrosDoados(int livrosDoados) {
    this.livrosDoados = livrosDoados;
  }
  
  public Date getLastContacted() {
    return lastContacted;
  }
  
  public boolean isConfirmado() {
    return confirmado;
  }

  public void setConfirmado(boolean confirmado) {
    this.confirmado = confirmado;
  }
  
  public Date getDesde() {
    return desde;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }
  
  public String getCidade() {
    return cidade;
  }
  
  public void setCidade(String cidade) {
    this.cidade = cidade;
  }
  
  public String getEstado() {
    return estado;
  }
  
  public void setEstado(String estado) {
    this.estado = estado;
  }
  
  public String getZip() {
    return zip;
  }
  
  public void setZip(String zip) {
    this.zip = zip;
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
    int result = getCodigo().compareTo(o.getCodigo());
    if (result == 0) {
      return id.compareTo(o.id);
    }
    return result;
  }
}