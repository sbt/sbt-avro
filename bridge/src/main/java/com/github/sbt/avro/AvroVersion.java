package com.github.sbt.avro;

import org.apache.avro.Schema;

public class AvroVersion implements Comparable<AvroVersion> {

    private final int major;
    private final int minor;
    private final int patch;

    public AvroVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    int getMajor() {
        return major;
    }

    int getMinor() {
        return minor;
    }

    int getPatch() {
        return patch;
    }

    static AvroVersion getRuntimeVersion() {
        String[] parts = Schema.class.getPackage().getImplementationVersion().split("\\.", 3);
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        return new AvroVersion(major, minor, patch);
    }


    @Override
    public int compareTo(AvroVersion o) {
        if (major != o.major) {
            return major - o.major;
        } else if (minor != o.minor) {
            return minor - o.minor;
        } else {
            return patch - o.patch;
        }
    }
}
