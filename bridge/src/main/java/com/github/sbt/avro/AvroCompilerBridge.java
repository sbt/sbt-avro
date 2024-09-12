package com.github.sbt.avro;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import xsbti.Logger;

import org.apache.avro.Protocol;
import org.apache.avro.compiler.idl.Idl;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility;
import org.apache.avro.generic.GenericData.StringType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AvroCompilerBridge implements AvroCompiler {

    private final Logger logger;
    private final StringType stringType;
    private final FieldVisibility fieldVisibility;
    private final boolean useNamespace;
    private final boolean enableDecimalLogicalType;
    private final boolean createSetters;
    private final boolean optionalGetters;

    public AvroCompilerBridge(
        Logger logger,
        String stringType,
        String fieldVisibility,
        boolean useNamespace,
        boolean enableDecimalLogicalType,
        boolean createSetters,
        boolean optionalGetters
    ) {
        this.logger = logger;
        this.stringType = StringType.valueOf(stringType);
        this.fieldVisibility = FieldVisibility.valueOf(fieldVisibility);
        this.useNamespace = useNamespace;
        this.enableDecimalLogicalType = enableDecimalLogicalType;
        this.createSetters = createSetters;
        this.optionalGetters = optionalGetters;
    }

    protected Schema.Parser createParser() {
        return new Schema.Parser();
    }

    @Override
    public void recompile(Class<?>[] records, File target) throws Exception {
        AvscFilesCompiler compiler = new AvscFilesCompiler();
        compiler.setStringType(stringType);
        compiler.setFieldVisibility(fieldVisibility);
        compiler.setUseNamespace(useNamespace);
        compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
        compiler.setCreateSetters(createSetters);
        if (AvroVersion.getMinor() > 8) {
            compiler.setGettersReturnOptional(optionalGetters);
        }
        if (AvroVersion.getMinor() > 9) {
            compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
        }
        compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/");

        Set<Class<? extends SpecificRecord>> classes = new HashSet<>();
        for (Class<?> record : records) {
            logger.info(() -> "Recompiling Avro record: " + record.getName());
            classes.add((Class<? extends SpecificRecord>) record);
        }
        compiler.compileClasses(classes, target);
    }

    @Override
    public void compileIdls(File[] idls, File target) throws Exception {
        for (File idl : idls) {
            logger.info(() -> "Compiling Avro IDL " + idl);
            Idl parser = new Idl(idl);
            Protocol protocol = parser.CompilationUnit();
            SpecificCompiler compiler = new SpecificCompiler(protocol);
            compiler.setStringType(stringType);
            compiler.setFieldVisibility(fieldVisibility);
            compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
            compiler.setCreateSetters(createSetters);
            if (AvroVersion.getMinor() > 8) {
                compiler.setGettersReturnOptional(optionalGetters);
            }
            if (AvroVersion.getMinor() > 9) {
                compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
            }
            compiler.compileToDestination(null, target);
        }
    }

    @Override
    public void compileAvscs(AvroFileRef[] avscs, File target) throws Exception {
        AvscFilesCompiler compiler = new AvscFilesCompiler();
        compiler.setStringType(stringType);
        compiler.setFieldVisibility(fieldVisibility);
        compiler.setUseNamespace(useNamespace);
        compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
        compiler.setCreateSetters(createSetters);
        if (AvroVersion.getMinor() > 8) {
            compiler.setGettersReturnOptional(optionalGetters);
        }
        if (AvroVersion.getMinor() > 9) {
            compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
        }
        compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/");

        Set<AvroFileRef> files = new HashSet<>();
        for (AvroFileRef ref: avscs) {
            logger.info(() -> "Compiling Avro schema: " + ref.getFile());
            files.add(ref);
        }
        compiler.compileFiles(Set.of(avscs), target);
    }

    @Override
    public void compileAvprs(File[] avprs, File target) throws Exception {
        for (File avpr : avprs) {
            logger.info(() -> "Compiling Avro protocol " + avpr);
            Protocol protocol = Protocol.parse(avpr);
            SpecificCompiler compiler = new SpecificCompiler(protocol);
            compiler.setStringType(stringType);
            compiler.setFieldVisibility(fieldVisibility);
            compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
            compiler.setCreateSetters(createSetters);
            if (AvroVersion.getMinor() > 8) {
                compiler.setGettersReturnOptional(optionalGetters);
            }
            if (AvroVersion.getMinor() > 9) {
                compiler.setOptionalGettersForNullableFieldsOnly(optionalGetters);
            }
            compiler.compileToDestination(null, target);
        }
    }
}
