package com.bank.pe.mscustomer.util;

import com.bank.pe.mscustomer.model.Metadata;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataExtended extends Metadata {

  public MetadataExtended(@JsonProperty("status") Integer status,
                          @JsonProperty("message") String message) {
    super();
    this.setStatus(status);
    this.setMessage(message);
  }
}
