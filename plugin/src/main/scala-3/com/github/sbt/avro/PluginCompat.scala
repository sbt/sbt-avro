package com.github.sbt.avro

import java.nio.file.{Path => NioPath}
import sbt.*
import xsbti.{FileConverter, HashedVirtualFileRef, VirtualFile}

private[avro] object PluginCompat:
  type FileRef = HashedVirtualFileRef
  type Out = VirtualFile

  def toNioPath(a: Attributed[HashedVirtualFileRef])(using conv: FileConverter): NioPath =
    conv.toPath(a.data)

  def toFileRef(f: File)(using conv: FileConverter): FileRef = conv.toVirtualFile(f.toPath)
