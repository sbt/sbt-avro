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
            compiler.compileToDestination(null, target);
        }
    }

    @Override
    public void compileAvscs(File[] avscs, File target) throws Exception {
        List<File> files = new ArrayList<>(avscs.length);
        for (File schema : avscs) {
            System.out.println("Compiling Avro schema: " + schema);
            files.add(schema);
        }
        List<Schema> schemas = parser.parseFiles(files);

        // This is a trick to use a single instance on the SpecificCompiler for all schemas
        // only avro 1.12+ SpecificCompiler has a constructor accepting a schema collection
        Schema schema = Schema.createUnion(schemas);
        SpecificCompiler compiler = new SpecificCompiler(schema);
        configureCompiler(compiler);
        compiler.compileToDestination(null, target);
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
}
