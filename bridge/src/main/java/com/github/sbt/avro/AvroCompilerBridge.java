package com.github.sbt.avro;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.Protocol;
import org.apache.avro.compiler.idl.Idl;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility;
import org.apache.avro.generic.GenericData.StringType;

import java.io.File;
import java.util.*;

public class AvroCompilerBridge implements AvroCompiler {

    private static final AvroVersion AVRO_1_9_0 = new AvroVersion(1, 9, 0);
    private static final AvroVersion AVRO_1_10_0 = new AvroVersion(1, 10, 0);

    private final AvroVersion avroVersion;
    protected AvscFilesParser parser;

    protected StringType stringType;
    protected FieldVisibility fieldVisibility;
    protected boolean useNamespace;
    protected boolean enableDecimalLogicalType;
    protected boolean createSetters;
    protected boolean optionalGetters;

    public AvroCompilerBridge() {
      avroVersion = AvroVersion.getRuntimeVersion();
      parser = new AvscFilesParser();
    }

    @Override
    public void setStringType(String stringType) {
        this.stringType = StringType.valueOf(stringType);
    }

    @Override
    public void setFieldVisibility(String fieldVisibility) {
        this.fieldVisibility = FieldVisibility.valueOf(fieldVisibility);
    }

    @Override
    public void setUseNamespace(boolean useNamespace) {
        this.useNamespace = useNamespace;
    }

    @Override
    public void setEnableDecimalLogicalType(boolean enableDecimalLogicalType) {
        this.enableDecimalLogicalType = enableDecimalLogicalType;
    }

    @Override
    public void setCreateSetters(boolean createSetters) {
        this.createSetters = createSetters;
    }

    @Override
    public void setOptionalGetters(boolean optionalGetters) {
        this.optionalGetters = optionalGetters;
    }

    protected void configureCompiler(SpecificCompiler compiler) {
        compiler.setStringType(stringType);
        compiler.setFieldVisibility(fieldVisibility);
        compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
        compiler.setCreateSetters(createSetters);
        if (avroVersion.compareTo(AVRO_1_9_0) >= 0) {
            compiler.setGettersReturnOptional(optionalGetters);
        }
        if (avroVersion.compareTo(AVRO_1_10_0) >= 0) {
            compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
        }
    }

    @Override
    public void recompile(Class<?>[] records, File target) throws Exception {
        List<Schema> schemas = new ArrayList<>(records.length);
        for (Class<?> record : records) {
            System.out.println("Recompiling Avro record: " + record.getName());
            Schema schema = SpecificData.get().getSchema(record);
            schemas.add(schema);
            SpecificCompiler compiler = new SpecificCompiler(schema);
            configureCompiler(compiler);
            compiler.compileToDestination(null, target);
        }
        parser.addTypes(schemas);
    }

    @Override
    public void compileIdls(File[] idls, File target) throws Exception {
        for (File idl : idls) {
            System.out.println("Compiling Avro IDL: " + idl);
            Idl parser = new Idl(idl);
            Protocol protocol = parser.CompilationUnit();
            SpecificCompiler compiler = new SpecificCompiler(protocol);
            configureCompiler(compiler);
            compiler.compileToDestination(idl, target);
        }
    }

    @Override
    public void compileAvscs(AvroFileRef[] avscs, File target) throws Exception {
        List<AvroFileRef> files = new ArrayList<>(avscs.length);
        for (AvroFileRef ref : avscs) {
            System.out.println("Compiling Avro schema: " + ref.getFile());
            files.add(ref);
        }
        Map<AvroFileRef, Schema> schemas = parser.parseFiles(files);
        if (useNamespace) {
            for (Map.Entry<AvroFileRef, Schema> s: schemas.entrySet()) {
                validateParsedSchema(s.getKey(), s.getValue());
            }
        }

        for (Map.Entry<AvroFileRef, Schema> entry: schemas.entrySet()) {
            File file = entry.getKey().getFile();
            Schema schema = entry.getValue();
            SpecificCompiler compiler = new SpecificCompiler(schema);
            configureCompiler(compiler);
            compiler.compileToDestination(file, target);
        }
    }

    @Override
    public void compileAvprs(File[] avprs, File target) throws Exception {
        for (File avpr : avprs) {
            System.out.println("Compiling Avro protocol: " + avpr);
            Protocol protocol = Protocol.parse(avpr);
            SpecificCompiler compiler = new SpecificCompiler(protocol);
            configureCompiler(compiler);
            compiler.compileToDestination(null, target);
        }
    }

    private void validateParsedSchema(AvroFileRef src, Schema schema) {
        if (useNamespace) {
            if (schema.getType() != Schema.Type.RECORD && schema.getType() != Schema.Type.ENUM) {
                throw new SchemaGenerationException(String.format(
                        "Error compiling schema file %s. "
                                + "Only one root RECORD or ENUM type is allowed per file.",
                        src
                ));
            } else if (!src.pathToClassName().equals(schema.getFullName())) {
                throw new SchemaGenerationException(String.format(
                        "Error compiling schema file %s. "
                                + "File class name %s does not match record class name %s",
                        src,
                        src.pathToClassName(),
                        schema.getFullName()
                ));
            }
        }
    }
}
