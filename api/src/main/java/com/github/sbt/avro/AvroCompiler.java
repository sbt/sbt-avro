package com.github.sbt.avro;

import java.io.File;

public interface AvroCompiler {
    void recompile(Class<?>[] records, File target) throws Exception;
    void compileIdls(File[] idls, File target) throws Exception;
    void compileAvscs(AvroFileRef[] avscs, File target) throws Exception;
    void compileAvprs(File[] avprs, File target) throws Exception;
}
