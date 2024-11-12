package com.github.sbt.avro;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AvscFilesParser {

  private final Supplier<Schema.Parser> parserSupplier;
  // act as the ParseContext introduced in avro 1.12
  // contain all types known by the parser
  private Map<String, Schema> context;

  public AvscFilesParser() {
    this(Schema.Parser::new);
  }

  public AvscFilesParser(Supplier<Schema.Parser> parserSupplier) {
    this.parserSupplier = parserSupplier;
    this.context = new HashMap<>();
  }

  public void addTypes(Iterable<Schema> types) {
    for (Schema schema : types) {
      context.put(schema.getFullName(), schema);
    }
  }

  public Map<AvroFileRef, Schema> parseFiles(Collection<AvroFileRef> files) {
    Set<AvroFileRef> unparsedFiles = new HashSet<>(files);
    Map<AvroFileRef, Schema> parsedFiles = new HashMap<>();
    Map<AvroFileRef, Exception> parseExceptions = new HashMap<>();

    Schema.Parser parser = unstashParser();
    boolean progressed = true;
    while (progressed && !unparsedFiles.isEmpty()) {
      progressed = false;
      parseExceptions.clear();

      for (AvroFileRef file : unparsedFiles) {
        try {
          Schema schema = parser.parse(file.getFile());
          parsedFiles.put(file, schema);
          progressed = true;
          stashParser(parser);
        } catch (AvroRuntimeException e) {
          parseExceptions.put(file, e);
          parser = unstashParser();
        } catch (IOException e) {
          throw new SchemaGenerationException(String.format("Error parsing schema file %s", file), e);
        }
      }

      unparsedFiles.removeAll(parsedFiles.keySet());
    }

    if (!unparsedFiles.isEmpty()) {
      String failedFiles = unparsedFiles.stream()
              .map(f -> {
                String message = Optional.ofNullable(parseExceptions.get(f))
                        .map(Exception::getMessage)
                        .orElse("Unknown error");
                return f.getFile().getName() + ": " + message;
              })
              .collect(Collectors.joining(",\n"));

      throw new SchemaGenerationException("Can not parse schema files:\n" + failedFiles);
    }

    return parsedFiles;
  }

  private void stashParser(Schema.Parser parser) {
    this.context = parser.getTypes();
  }

  private Schema.Parser unstashParser() {
    // on failure Schema.Parser changes cache state.
    // We want last successful state.
    Schema.Parser parser = parserSupplier.get();
    // filter-out known types
    Set<String> predefinedTypes = parser.getTypes().keySet();
    context.keySet().removeAll(predefinedTypes);
    parser.addTypes(context);
    return parser;
  }
}
