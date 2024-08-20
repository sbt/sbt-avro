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

// Java sources compiled with one version of Avro might be incompatible with a
// different version of the Avro library. Therefore we specify the compiler
// version here explicitly.
libraryDependencies += "org.apache.avro" % "avro-compiler" % "1.12.0"
```

Add the library dependency to `build.sbt`:

```
libraryDependencies += "org.apache.avro" % "avro" % avroCompilerVersion
```

## Settings

| Name                                       | Default                                       | Description                                                                             |
|:-------------------------------------------|:----------------------------------------------|:----------------------------------------------------------------------------------------|
| `avroSource`                               | `sourceDirectory` / `avro`                    | Source directory with `*.avsc`, `*.avdl` and `*.avpr` files.                            |
| `avroSpecificRecords`                      | `Seq.empty`                                   | List of avro generated classes to recompile with current avro version and settings.     |
| `avroSchemaParserBuilder`                  | `DefaultSchemaParserBuilder.default()`        | `.avsc` schema parser builder                                                           |
| `avroUnpackDependencies` / `includeFilter` | All avro specifications                       | Avro specification files from dependencies to unpack                                    |
| `avroUnpackDependencies` / `excludeFilter` | Hidden files                                  | Avro specification files from dependencies to exclude from unpacking                    |
| `avroUnpackDependencies` / `target`        | `sourceManaged` / `avro` / `$config`          | Target directory for schemas packaged in the dependencies                               |
| `avroGenerate` / `target`                  | `sourceManaged` / `compiled_avro` / `$config` | Source directory for generated `.java` files.                                           |
| `avroDependencyIncludeFilter`              | `source` typed `avro` classifier artifacts    | Dependencies containing avro schema to be unpacked for generation                       |
| `avroIncludes`                             | `Seq()`                                       | Paths with extra `*.avsc` files to be included in compilation.                          |
| `packageAvro` / `artifactClassifier`       | `Some("avro")`                                | Classifier for avro artifact                                                            |
| `packageAvro` / `publishArtifact`          | `false`                                       | Enable / Disable avro artifact publishing                                               |
| `avroStringType`                           | `CharSequence`                                | Type for representing strings. Possible values: `CharSequence`, `String`, `Utf8`.       |
| `avroUseNamespace`                         | `false`                                       | Validate that directory layout reflects namespaces, i.e. `com/myorg/MyRecord.avsc`.     |
| `avroFieldVisibility`                      | `public`                                      | Field Visibility for the properties. Possible values: `private`, `public`.              |
| `avroEnableDecimalLogicalType`             | `true`                                        | Use `java.math.BigDecimal` instead of `java.nio.ByteBuffer` for logical type `decimal`. |
| `avroOptionalGetters`                      | `false` (requires avro `1.10+`)               | Generate getters that return `Optional` for nullable fields.                            |

## Tasks

| Name                     | Description                                                                                       |
|:-------------------------|:--------------------------------------------------------------------------------------------------|
| `avroUnpackDependencies` | Unpack avro schemas from dependencies. This task is automatically executed before `avroGenerate`. |
| `avroGenerate`           | Generate Java sources for Avro schemas. This task is automatically executed before `compile`.     |
| `packageAvro`            | Produces an avro artifact, such as a jar containing avro schemas.                                 |

## Examples

For example, to change the Java type of the string fields, add the following lines to `build.sbt`:

```sbt
avroStringType := "String"
```

If you depend on an artifact with previously generated avro java classes with string fields as `CharSequence`,
you can recompile them with `String` by also adding the following

```sbt
Compile / avroSpecificRecords += classOf[com.example.MyAvroRecord] // lib must be declared in project/plugins.sbt
```

## Packaging Avro files

Avro sources (`*.avsc`, `*.avdl` and `*.avpr` files) can be packaged in a separate jar with the `source` type and
`avro` classifier by running `packageAvro`.

By default, `sbt-avro` does not publish this. You can enable it with

```sbt
Compile / packageAvro / publishArtifact := true
```

## Declaring dependencies

You can specify a dependency on an avro source artifact that contains the schemas like so:

```sbt
libraryDependencies += "org" % "name" % "rev" classifier "avro"
```

If some avro schemas are not packaged in a `source/avro` artifact, you can update the `avroDependencyIncludeFilter`
setting to instruct the plugin to look for schemas in the desired dependency:

```sbt
libraryDependencies += "org" % "name" % "rev" // module containing avro schemas
avroDependencyIncludeFilter := avroDependencyIncludeFilter.value || moduleFilter(organization = "org", name = "name")
```

# License

This program is distributed under the BSD license. See the file `LICENSE` for more details.

# Credits

`sbt-avro` is maintained by the [sbt Community](http://www.scala-sbt.org/release/docs/Community-Plugins.html). The
initial code was based on a similar plugin: [`sbt-protobuf`](https://github.com/gseitz/sbt-protobuf). Feel free to file
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
