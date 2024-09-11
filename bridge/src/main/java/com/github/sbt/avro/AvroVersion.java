package com.github.sbt.avro;

import org.apache.avro.Schema;

public class AvroVersion {

    private static String[] AVRO_VERSION_PARTS =
            Schema.class.getPackage().getImplementationVersion().split("\\.", 3);

    static int getMajor() {
        return Integer.parseInt(AVRO_VERSION_PARTS[0]);
    }

    static int getMinor() {
        return Integer.parseInt(AVRO_VERSION_PARTS[1]);
    }
}
