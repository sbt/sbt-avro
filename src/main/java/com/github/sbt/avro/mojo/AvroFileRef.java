package com.github.sbt.avro.mojo;

import java.io.File;
import java.util.Objects;

public class AvroFileRef implements Comparable<AvroFileRef> {

  private final File root;
  private final String path;

  public AvroFileRef(File root, String path) {
    this.root = root;
    this.path = path;
  }

  public File getFile() {
    return new File(root, path);
  }

  public String pathToClassName() {
    String woExt = path;
    if (woExt.endsWith(".avsc")) {
      woExt = woExt.substring(0, woExt.length() - 5);
    }
    return woExt.replace("/", ".");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AvroFileRef that = (AvroFileRef) o;
    return Objects.equals(root, that.root) &&
           Objects.equals(path, that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(root, path);
  }

  @Override
  public String toString() {
    return getFile().toString();
  }

  @Override
  public int compareTo(AvroFileRef o) {
    int result = root.compareTo(o.root);
    if (result == 0) {
      result = path.compareTo(o.path);
    }
    return result;
  }
}
