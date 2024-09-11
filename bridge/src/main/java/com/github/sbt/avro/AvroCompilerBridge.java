package com.github.sbt.avro;

import org.apache.avro.Protocol;
import org.apache.avro.compiler.idl.Idl;
import org.apache.avro.compiler.idl.ParseException;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility;
import org.apache.avro.generic.GenericData.StringType;
import org.apache.avro.specific.SpecificRecord;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class AvroCompilerBridge implements AvroCompiler {

    void recompile(
            Class<? extends SpecificRecord>[] records,
            File target,
            StringType stringType,
            FieldVisibility fieldVisibility,
            Boolean enableDecimalLogicalType,
            Boolean useNamespace,
            Boolean optionalGetters,
            Boolean createSetters
            // builder: SchemaParserBuilder
    ) {
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
        compiler.setLogCompileExceptions(true);
        compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/");
        compiler.compileClasses(Set.of(records), target);
    }

    void compileIdls(
        File[] idls,
        File target,
        StringType stringType,
        FieldVisibility fieldVisibility,
        Boolean enableDecimalLogicalType,
        Boolean optionalGetters,
        Boolean createSetters
    ) throws IOException, ParseException {
        for (File idl : idls) {
//            log.info(s"Compiling Avro IDL $idl")
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

    void compileAvscs(
            AvroFileRef[] avscs,
            File target,
            StringType stringType,
            FieldVisibility fieldVisibility,
            Boolean enableDecimalLogicalType,
            Boolean useNamespace,
            Boolean optionalGetters,
            Boolean createSetters
            // builder: SchemaParserBuilder
    ) {
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
        compiler.setLogCompileExceptions(true);
        compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/");

        compiler.compileFiles(Set.of(avscs), target);
    }

    void compileAvprs(
            File[] avprs,
            File target,
            StringType stringType,
            FieldVisibility fieldVisibility,
            Boolean enableDecimalLogicalType,
            Boolean optionalGetters,
            Boolean createSetters
    ) throws IOException {
        for (File avpr : avprs) {
//            log.info(s"Compiling Avro protocol $avpr")
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


    @Override
    public void compileAvroSchema(
            Class<?>[] records,
            File[] avdls,
            AvroFileRef[] avscs,
            File[] avprs,
            File target,
            String stringType,
            String fieldVisibility,
            Boolean enableDecimalLogicalType,
            Boolean useNamespace,
            Boolean optionalGetters,
            Boolean createSetters) {
        StringType stringTypeEnum = StringType.valueOf(stringType);
        FieldVisibility fieldVisibilityEnum = FieldVisibility.valueOf(fieldVisibility);

        try {
            recompile((Class<? extends SpecificRecord>[]) records, target, stringTypeEnum, fieldVisibilityEnum, enableDecimalLogicalType, useNamespace, optionalGetters, createSetters);

            compileIdls(avdls, target, stringTypeEnum, fieldVisibilityEnum, enableDecimalLogicalType, optionalGetters, createSetters);
            compileAvscs(avscs, target,stringTypeEnum, fieldVisibilityEnum, enableDecimalLogicalType, useNamespace, optionalGetters, createSetters);
            compileAvprs(avprs, target, stringTypeEnum, fieldVisibilityEnum, enableDecimalLogicalType, optionalGetters, createSetters);
        } catch (Exception e) {
            throw new RuntimeException("Avro schema compilation failed: " + e.getMessage(), e);
        }
    }
}
