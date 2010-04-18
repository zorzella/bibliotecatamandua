package com.zorzella.tamandua.gwt.client;

import java.io.Serializable;

public class NotAnAdminException extends RuntimeException implements Serializable {

  public NotAnAdminException() {}
  
  public NotAnAdminException(String message) {
    super(message);
  }
}
