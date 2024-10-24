package com.github.sbt.avro
import sbt.*
import xsbti.FileConverter

import java.nio.file.{Path => NioPath}

private[avro] object PluginCompat {
  type FileRef = java.io.File
  type Out = java.io.File

  def toNioPath(a: Attributed[File])(implicit conv: FileConverter): NioPath =
    a.data.toPath()
  def toFileRef(f: File): FileRef = f
}
