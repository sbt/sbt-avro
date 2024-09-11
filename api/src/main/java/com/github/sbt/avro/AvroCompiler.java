package com.github.sbt.avro;

import java.io.File;
import java.io.IOException;

public interface AvroCompiler {
    void compileAvroSchema(
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
            Boolean createSetters
    ) throws IOException;
}
