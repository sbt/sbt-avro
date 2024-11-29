package com.github.sbt.avro;

import java.io.File;

public interface AvroCompiler {

    void setStringType(String stringType);
    void setFieldVisibility(String fieldVisibility);
    void setEnableDecimalLogicalType(boolean enableDecimalLogicalType);
    void setCreateSetters(boolean createSetters);
    void setOptionalGetters(boolean optionalGetters);

    void recompile(Class<?>[] records, File target) throws Exception;
    void compileIdls(File[] idls, File target) throws Exception;
    void compileAvscs(File[] avscs, File target) throws Exception;
    void compileAvprs(File[] avprs, File target) throws Exception;
}
