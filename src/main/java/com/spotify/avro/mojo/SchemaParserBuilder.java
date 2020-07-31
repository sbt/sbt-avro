package com.spotify.avro.mojo;

import org.apache.avro.Schema;

import java.util.Map;

public class SchemaParserBuilder {

    protected Map<String, Schema> types;
    protected boolean validate;
    protected boolean validateDefaults;

    protected SchemaParserBuilder() {
        this(new Schema.Parser());
    }

    protected SchemaParserBuilder(Schema.Parser parser) {
        this.types = parser.getTypes();
        this.validate = parser.getValidate();
        this.validateDefaults = parser.getValidateDefaults();
    }

    public Schema.Parser build() {
        Schema.Parser copy = new Schema.Parser();
        copy.addTypes(types);
        copy.setValidate(validate);
        copy.setValidateDefaults(validateDefaults);
        return copy;
    }

    public SchemaParserBuilder withTypes(Map<String, Schema> types) {
        this.types = types;
        return this;
    }

    public SchemaParserBuilder withValidate(boolean validate) {
        this.validate = validate;
        return this;
    }

    public SchemaParserBuilder withValidateDefaults(boolean validate) {
        this.validateDefaults = validate;
        return this;
    }

    public static SchemaParserBuilder newBuilder() {
        return new SchemaParserBuilder();
    }

    public static SchemaParserBuilder newBuilder(Schema.Parser parser) {
        return new SchemaParserBuilder(parser);
    }
}
