// Copyright 2008 Google Inc.  All Rights Reserved.
// Copyright 2010 Google Inc. All Rights Reserved.

package com.zorzella.tamandua.gwt.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public final class AlreadyReturnedException extends RuntimeException implements IsSerializable, Serializable {}