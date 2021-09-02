package com.github.sbt.avro.mojo;

import org.apache.avro.Schema;

public interface SchemaParserBuilder {
  Schema.Parser build();
}
