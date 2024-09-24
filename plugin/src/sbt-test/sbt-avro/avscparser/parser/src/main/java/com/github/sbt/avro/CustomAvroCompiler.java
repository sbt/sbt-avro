package com.github.sbt.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.NameValidator;

import java.util.Collections;

public class CustomAvroCompiler extends AvroCompilerBridge {

    @Override
    protected Schema.Parser createParser() {
        Schema.Parser parser = new Schema.Parser();
        parser.setValidateDefaults(false);
        Schema externalSchema = SchemaBuilder
                .enumeration("B")
                .namespace("com.github.sbt.avro.test")
                .symbols("B1");
        parser.addTypes(Collections.singletonList(externalSchema));
        return parser;
    }

}
