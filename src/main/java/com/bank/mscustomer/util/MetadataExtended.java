package com.bank.mscustomer.util;

import com.bank.mscustomer.model.Metadata;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataExtended extends Metadata {

  public MetadataExtended(@JsonProperty("status") Integer status,
                          @JsonProperty("message") String message) {
    super();
    this.setStatus(status);
    this.setMessage(message);
  }
}
