package com.zorzella.tamandua.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class NaiveAsyncCallback<T> implements AsyncCallback<T> {

  public final void onFailure(Throwable caught) {
    throw new RuntimeException(this.toString(), caught);
  }
}
