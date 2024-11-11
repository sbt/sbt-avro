package com.github.sbt.avro;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;

import org.apache.avro.Protocol;
import org.apache.avro.compiler.idl.Idl;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility;
import org.apache.avro.generic.GenericData.StringType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AvroCompilerBridge implements AvroCompiler {

    private static final AvroVersion AVRO_1_9_0 = new AvroVersion(1, 9, 0);
    private static final AvroVersion AVRO_1_10_0 = new AvroVersion(1, 10, 0);

    private final AvroVersion avroVersion = AvroVersion.getRuntimeVersion();

    private StringType stringType;
    private FieldVisibility fieldVisibility;
    private boolean useNamespace;
    private boolean enableDecimalLogicalType;
    private boolean createSetters;
    private boolean optionalGetters;

    protected Schema.Parser createParser() {
        return new Schema.Parser();
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

    @Override
    public void recompile(Class<?>[] records, File target) throws Exception {
        AvscFilesCompiler compiler = new AvscFilesCompiler(this::createParser);
        compiler.setStringType(stringType);
        compiler.setFieldVisibility(fieldVisibility);
        compiler.setUseNamespace(useNamespace);
        compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
        compiler.setCreateSetters(createSetters);
        if (avroVersion.compareTo(AVRO_1_9_0) >= 0) {
            compiler.setGettersReturnOptional(optionalGetters);
        }
        if (avroVersion.compareTo(AVRO_1_10_0) >= 0) {
            compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
        }
        compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/");

        Set<Class<? extends SpecificRecord>> classes = new HashSet<>();
        for (Class<?> record : records) {
            System.out.println("Recompiling Avro record: " + record.getName());
            classes.add((Class<? extends SpecificRecord>) record);
        }
        compiler.compileClasses(classes, target);
    }

    @Override
    public void compileIdls(File[] idls, File target) throws Exception {
        for (File idl : idls) {
            System.out.println("Compiling Avro IDL: " + idl);
            Idl parser = new Idl(idl);
            Protocol protocol = parser.CompilationUnit();
            SpecificCompiler compiler = new SpecificCompiler(protocol);
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
            compiler.compileToDestination(null, target);
        }
    }

    @Override
    public void compileAvscs(AvroFileRef[] avscs, File target) throws Exception {
        AvscFilesCompiler compiler = new AvscFilesCompiler(this::createParser);
        compiler.setStringType(stringType);
        compiler.setFieldVisibility(fieldVisibility);
        compiler.setUseNamespace(useNamespace);
        compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
        compiler.setCreateSetters(createSetters);
        if (avroVersion.compareTo(AVRO_1_9_0) >= 0) {
            compiler.setGettersReturnOptional(optionalGetters);
        }
        if (avroVersion.compareTo(AVRO_1_10_0) >= 0) {
            compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
        }
        compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/");

        Set<AvroFileRef> files = new HashSet<>();
        for (AvroFileRef ref : avscs) {
            System.out.println("Compiling Avro schema: " + ref.getFile());
            files.add(ref);
        }
        compiler.compileFiles(files, target);
    }

    @Override
    public void compileAvprs(File[] avprs, File target) throws Exception {
        for (File avpr : avprs) {
            System.out.println("Compiling Avro protocol: " + avpr);
            Protocol protocol = Protocol.parse(avpr);
            SpecificCompiler compiler = new SpecificCompiler(protocol);
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
            compiler.compileToDestination(null, target);
        }
    }
}
