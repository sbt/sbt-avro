package com.github.sbt.avro;

public class SchemaGenerationException extends RuntimeException {

  public SchemaGenerationException(String message) {
    super(message);
  }

  public SchemaGenerationException(String message, Exception cause) {
    super(message, cause);
  }
}
