package com.github.sbt.avro.mojo;

public class SchemaGenerationException extends RuntimeException {

  public SchemaGenerationException(String message) {
    super(message);
  }

  public SchemaGenerationException(String message, Exception cause) {
    super(message, cause);
  }
}
