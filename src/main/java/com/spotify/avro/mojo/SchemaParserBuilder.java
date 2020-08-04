package com.spotify.avro.mojo;

import org.apache.avro.Schema;

public interface SchemaParserBuilder {
  Schema.Parser build();
}
