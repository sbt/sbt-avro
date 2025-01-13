sbt-avro
========

[![Build Status](https://github.com/sbt/sbt-avro/actions/workflows/ci.yml/badge.svg)](https://github.com/sbt/sbt-avro/actions/workflows/ci.yml)

# Overview

[sbt-avro](http://avro.apache.org) is a [sbt](http://www.scala-sbt.org) plugin for generating Java sources for Avro
schemas. It also supports referencing schemas from different files.

# Usage

## Installing the plugin

Add the plugin according to the [sbt documentation](https://www.scala-sbt.org/1.x/docs/Using-Plugins.html).

For instance, add the following lines to `project/plugins.sbt`:

```
addSbtPlugin("com.github.sbt" % "sbt-avro" % "3.5.0")
```

Enable the plugin in your `build.sbt` and select the desired avro version to use:

```
enablePlugins(SbtAvro)
avroVersion := "1.12.0"
```

## Config

An `avro` configuration will be added to the project. Libraries defined with this scope will be loaded by the sbt plugin
to generate the avro classes.

## Settings

### Project settings

| Name                           | Default                                                                             | Description                                                                             |
|:-------------------------------|:------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------|
| `avroAdditionalDependencies`   | `avro-compiler % avroVersion % "avro-compiler"`<br>`avro % avroVersion % "compile"` | Additional dependencies to be added to library dependencies.                            | 
| `avroCompiler`                 | `com.github.sbt.avro.AvroCompilerBridge`                                            | Sbt avro compiler class.                                                                |
| `avroCreateSetters`            | `true`                                                                              | Generate setters.                                                                       |
| `avroEnableDecimalLogicalType` | `true`                                                                              | Use `java.math.BigDecimal` instead of `java.nio.ByteBuffer` for logical type `decimal`. |
| `avroFieldVisibility`          | `public`                                                                            | Field visibility for the properties. Possible values: `private`, `public`.              |
| `avroOptionalGetters`          | `false` (requires avro `1.10+`)                                                     | Generate getters that return `Optional` for nullable fields.                            |
| `avroStringType`               | `CharSequence`                                                                      | Type for representing strings. Possible values: `CharSequence`, `String`, `Utf8`.       |
| `avroVersion`                  | `1.12.0`                                                                            | Avro version to use in the project.                                                     |

### Scoped settings (Compile/Test)

| Name                                       | Default                                                                         | Description                                                                                          |
|:-------------------------------------------|:--------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------|
| `avroGenerate` / `target`                  | `sourceManaged` / `compiled_avro` / `$config`                                   | Source directory for generated `.java` files.                                                        |
| `avroSource`                               | `sourceDirectory` / `$config` / `avro`                                          | Default Avro source directory for `*.avsc`, `*.avdl` and `*.avpr` files.                             |
| `avroSpecificRecords`                      | `Seq.empty`                                                                     | List of fully qualified Avro record class names to recompile with current avro version and settings. |
| `avroDependencyIncludeFilter`              | `Compile`: artifacts in `Avro` config<br>`Test`: artifacts in `AvroTest` config | Filter for including modules containing avro dependencies.                                           |
| `avroUmanagedSourceDirectories`            | `Seq(avroSource)`                                                               | Unmanaged Avro source directories, which contain manually created sources.                           |
| `avroUnpackDependencies` / `excludeFilter` | `HiddenFileFilter`                                                              | Filter for excluding avro specification files from unpacking.                                        |
| `avroUnpackDependencies` / `includeFilter` | `AllPassFilter`                                                                 | Filter for including avro specification files to unpack.                                             |
| `avroUnpackDependencies` / `target`        | `sourceManaged` / `avro` / `$config`                                            | Target directory for schemas packaged in the dependencies                                            |
| `packageAvro` / `artifactClassifier`       | `Some("avro")`                                                                  | Classifier for avro artifact                                                                         |
| `packageAvro` / `publishArtifact`          | `false`                                                                         | Enable / Disable avro artifact publishing                                                            |


## Scoped Tasks (Compile/Test)

| Name                     | Description                                                                                       |
|:-------------------------|:--------------------------------------------------------------------------------------------------|
| `avroGenerate`           | Generate Java sources for Avro schemas. This task is automatically executed before `compile`.     |
| `avroUnpackDependencies` | Unpack avro schemas from dependencies. This task is automatically executed before `avroGenerate`. |
| `packageAvro`            | Produces an avro artifact, such as a jar containing avro schemas.                                 |

## Examples

For example, to change the Java type of the string fields, add the following lines to `build.sbt`:

```sbt
avroStringType := "String"
```

If you depend on an artifact with previously generated avro java classes with string fields as `CharSequence`,
you can recompile them with `String` by also adding the following

```sbt
libraryDependencies +=  "org" % "name" % "rev" % "avro-compiler"
Compile / avroSpecificRecords += "com.example.MyAvroRecord"
```

## Packaging Avro files

Avro sources (`*.avsc`, `*.avdl` and `*.avpr` files) can be packaged in a separate jar with `avro` classifier
by running `packageAvro`.

By default, `sbt-avro` does not publish this. You can enable it with

```sbt
Compile / packageAvro / publishArtifact := true
```

## Declaring dependencies

You can specify a dependency on an avro source artifact that contains the schemas like so:

```sbt
libraryDependencies += "org" % "name" % "rev" % "avro" classifier "avro" intransitive()
```

If some avro schemas are not packaged in a `avro` artifact, you can update the `avroDependencyIncludeFilter`
setting to instruct the plugin to look for schemas in the desired dependency:

```sbt
libraryDependencies += "org" % "name" % "rev" % "avro" intransitive() // module containing avro schemas
Compile / avroDependencyIncludeFilter := configurationFilter("avro") && moduleFilter(organization = "org", name = "name")
```

If some artifact is meant to be used in the test scope only, you can do the following

```sbt
libraryDependencies += "org" % "name" % "rev" % "avro-test" classifier "avro" intransitive()
```

# License

This program is distributed under the BSD license. See the file `LICENSE` for more details.

# Credits

`sbt-avro` is maintained by the [sbt Community](http://www.scala-sbt.org/release/docs/Community-Plugins.html).
The code was based on a similar plugin: [`sbt-protoc`](https://github.com/thesamet/sbt-protoc). Feel free to file
issues or pull requests.

# Contributors

- [Ashwanth Kumar](https://github.com/ashwanthkumar)
- [Ben McCann](http://www.benmccann.com)
- [Brennan Saeta](https://github.com/saeta)
- [Daniel Lundin](https://github.com/dln)
- [Juan Manuel Caicedo](https://cavorite.com)
- [Julian Peeters](https://github.com/julianpeeters)
- [Jérôme Wacongne](https://github.com/ch4mpy)
- [Kellen Dye](https://github.com/kellen)
- [Martin Achenbach](https://github.com/drachenbach)
- [Michel Davit](https://github.com/RustedBones)
- [Mārtiņš Kalvāns](https://github.com/sisidra)
- [Neville Li](https://github.com/nevillelyh)
- [Oskar Jung](https://github.com/ojung)
- [Przemysław Dubaniewicz](https://github.com/przemekd)
- [Ryan Koval](https://github.com/rkoval)
- [Saket Kumar](https://github.com/skate056)
- [Vince Tse](https://github.com/vtonehundred)
