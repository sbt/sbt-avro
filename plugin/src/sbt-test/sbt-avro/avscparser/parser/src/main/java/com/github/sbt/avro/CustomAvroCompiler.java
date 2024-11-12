package com.github.sbt.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.NameValidator;

import java.util.Collections;

public class CustomAvroCompiler extends AvroCompilerBridge {

    private static final Schema EXTERNAL_SCHEMA = SchemaBuilder
            .enumeration("B")
            .namespace("com.github.sbt.avro.test")
            .symbols("B1");

    public CustomAvroCompiler() {
        super();
        this.parser = new AvscFilesParser(() -> {
            Schema.Parser p = new Schema.Parser();
            p.addTypes(Collections.singletonList(EXTERNAL_SCHEMA));
            p.setValidateDefaults(false);
            return p;
        });
    }
}
