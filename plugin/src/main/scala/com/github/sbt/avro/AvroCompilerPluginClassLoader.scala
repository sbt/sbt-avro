package com.github.sbt.avro

import java.net.{URL, URLClassLoader}

private object AvroCompilerPluginClassLoader {
  private val JvmPrefixes = Array("java.", "jdk.")
  private val CompilerInterface = classOf[AvroCompiler].getName
}

private class AvroCompilerPluginClassLoader(urls: Array[URL], parent: ClassLoader)
    extends URLClassLoader(urls) {
  import AvroCompilerPluginClassLoader.*

  override def findClass(name: String): Class[?] = {
    if (JvmPrefixes.exists(name.startsWith) || CompilerInterface == name) {
      parent.loadClass(name)
    } else {
      super.findClass(name)
    }
  }
}
