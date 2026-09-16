package com.github.sbt.avro;

import org.apache.avro.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvscFilesParserTest {

  private static final File SOURCE_DIR = sourceDir();

  @Test
  void parseFilesWithDependantSchemas() {
    AvscFilesParser parser = new AvscFilesParser();
    List<File> sourceFiles = Stream.of(
            // fully-qualified names
            "a.avsc", "b.avsc", "c.avsc", "d.avsc", "e.avsc",
            // simple names
            "_a.avsc", "_b.avsc", "_c.avsc", "_d.avsc", "_e.avsc")
        .map(name -> new File(SOURCE_DIR, name))
        .collect(Collectors.toList());

    Map<File, Schema> schemas = parser.parseFiles(sourceFiles);

    assertEquals(
        List.of(
            "com.github.sbt.avro.test.A",
            "com.github.sbt.avro.test.B",
            "com.github.sbt.avro.test.C",
            "com.github.sbt.avro.test.D",
            "com.github.sbt.avro.test.E",
            "com.github.sbt.avro.test._A",
            "com.github.sbt.avro.test._B",
            "com.github.sbt.avro.test._C",
            "com.github.sbt.avro.test._D",
            "com.github.sbt.avro.test._E"),
        fullNames(schemas));
  }

  @Test
  void parseFilesWithDependantTypes() {
    AvscFilesParser parser = new AvscFilesParser();
    // TestSpecificRecordParent depends on TestSpecificRecord
    Schema dependant = new Schema.Parser().parse("""
        {
          "name": "TestSpecificRecord",
          "namespace": "com.github.sbt.avro",
          "type": "record",
          "fields": [
            {
              "name": "value",
              "type": "string"
            }
          ]
        }""");
    File parent = new File(SOURCE_DIR, "test_records.avsc");

    parser.addTypes(Collections.singletonList(dependant));
    Map<File, Schema> schemas = parser.parseFiles(Collections.singletonList(parent));

    assertEquals(List.of("com.github.sbt.avro.TestSpecificRecordParent"), fullNames(schemas));
  }

  private static List<String> fullNames(Map<File, Schema> schemas) {
    return schemas.values().stream().map(Schema::getFullName).sorted().collect(Collectors.toList());
  }

  private static File sourceDir() {
    try {
      return new File(AvscFilesParserTest.class.getClassLoader().getResource("avro").toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
