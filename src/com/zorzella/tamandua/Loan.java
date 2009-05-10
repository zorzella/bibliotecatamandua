package com.zorzella.tamandua;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Loan {

  public enum Type {
    BOOK,
  }

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String adminCode;

  @Persistent
  private String memberCode;

  @Persistent
  private Long bookId;

  @Persistent
  private Date loanDate;

  @Persistent
  private Date returnDate;

  @Persistent
  private String comment;

  public Loan(
      String adminCode,
      String memberCode,
      Long bookId
      ) {
    super();
    loanDate = new Date();    
    this.adminCode = adminCode;
    this.memberCode = memberCode;
    this.bookId = bookId;
  }

  public Long getId() {
    return id;
  }

  public String getAdminCode() {
  return adminCode;
  }

  public void setAdminCode(String adminCode) {
  this.adminCode = adminCode;
  }

  public String getMemberCode() {
  return memberCode;
  }

  public void setMemberCode(String memberCode) {
  this.memberCode = memberCode;
  }

  public Long getBookId() {
  return bookId;
  }
  
  public void setBookId(Long bookId) {
  this.bookId = bookId;
  }
  
  public Date getLoanDate() {
  return loanDate;
  }

  public void setLoanDate(Date loanDate) {
  this.loanDate = loanDate;
  }

  public Date getReturnDate() {
  return returnDate;
  }

  public void setReturnDate(Date returnDate) {
  this.returnDate = returnDate;
  }

  public void setId(Long id) {
  this.id = id;
  }

  public String getComment() {
	return comment;
}
  
  public void setComment(String comment) {
	this.comment = comment;
  }
  
 @Override
public String toString() {
 return String.format("%s,%s,%s,%s,%s",
 this.adminCode,
 memberCode,
 bookId,
 Dates.dateToString(loanDate),
 Dates.dateToString(returnDate)
 );
} 

}