sbt-avro
========

[![Build Status](https://travis-ci.org/sbt/sbt-avro.svg?branch=master)](https://travis-ci.org/sbt/sbt-avro)

# Overview

[sbt-avro](http://avro.apache.org) is a [sbt](http://www.scala-sbt.org) plugin for generating Java sources for Avro schemas. It also supports referencing schemas from different files.

# Usage

## Installing the plugin

Add the plugin according to the [sbt documentation](https://www.scala-sbt.org/1.x/docs/Using-Plugins.html).

For instance, add the following lines to `project/plugins.sbt`:

```
addSbtPlugin("com.github.sbt" % "sbt-avro" % "3.4.1")

// Java sources compiled with one version of Avro might be incompatible with a
// different version of the Avro library. Therefore we specify the compiler
// version here explicitly.
libraryDependencies += "org.apache.avro" % "avro-compiler" % "1.11.0"
```

Add the library dependency to `build.sbt`:

```
// Version must match that of `avro-compiler` in `project/plugins.sbt`
libraryDependencies += "org.apache.avro" % "avro" % "1.11.0"
```

## Settings

| Name                                       | Default                                    | Description |
|:-------------------------------------------|:-------------------------------------------|:------------|
| `avroSource`                               | `sourceDirectory` / `avro`                 | Source directory with `*.avsc`, `*.avdl` and `*.avpr` files. |
| `avroSchemaParserBuilder`                  | `DefaultSchemaParserBuilder.default()`     | `.avsc` schema parser builder |
| `avroUnpackDependencies` / `includeFilter` | All avro specifications                    | Avro specification files from dependencies to unpack |
| `avroUnpackDependencies` / `excludeFilter` | Hidden files                               | Avro specification files from dependencies to exclude from unpacking |
| `avroUnpackDependencies` / `target`        | `target` / `avro` / `$config`              | Target directory for schemas packaged in the dependencies |
| `avroGenerate` / `target`                  | `target` / `compiled_avro` / `$config`     | Source directory for generated `.java` files. |
| `avroDependencyIncludeFilter`              | `source` typed `avro` classifier artifacts | Dependencies containing avro schema to be unpacked for generation |
| `avroIncludes`                             | `Seq()`                                    | Paths with extra `*.avsc` files to be included in compilation. |
| `packageAvro` / `artifactClassifier`       | `Some("avro")`                             | Classifier for avro artifact |
| `packageAvro` / `publishArtifact`          | `false`                                    | Enable / Disable avro artifact publishing |
| `avroStringType`                           | `CharSequence`                             | Type for representing strings. Possible values: `CharSequence`, `String`, `Utf8`. |
| `avroUseNamespace`                         | `false`                                    | Validate that directory layout reflects namespaces, i.e. `com/myorg/MyRecord.avsc`. |
| `avroFieldVisibility`                      | `public`                                   | Field Visibility for the properties. Possible values: `private`, `public`. |
| `avroEnableDecimalLogicalType`             | `true`                                     | Set to true to use `java.math.BigDecimal` instead of `java.nio.ByteBuffer` for logical type `decimal`. |
| `avroOptionalGetters`                      | `false` (requires avro `1.10+`)            | Set to true to generate getters that return `Optional` for nullable fields. |

## Examples

For example, to change the Java type of the string fields, add the following lines to `build.sbt`:

```
avroStringType := "String"
```

## Tasks

| Name                     | Description |
|:-------------------------|:------------|
| `avroUnpackDependencies` | Unpack avro schemas from dependencies. This task is automatically executed before `avroGenerate`.
| `avroGenerate`           | Generate Java sources for Avro schemas. This task is automatically executed before `compile`.
| `packageAvro`            | Produces an avro artifact, such as a jar containing avro schemas.

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

`sbt-avro` is maintained by the [sbt Community](http://www.scala-sbt.org/release/docs/Community-Plugins.html). The initial code was based on a similar plugin: [`sbt-protobuf`](https://github.com/gseitz/sbt-protobuf). Feel free to file issues or pull requests.

# Contributors

- [Juan Manuel Caicedo](https://cavorite.com)
- [Brennan Saeta](https://github.com/saeta)
- [Daniel Lundin](https://github.com/dln)
- [Vince Tse](https://github.com/vtonehundred)
- [Ashwanth Kumar](https://github.com/ashwanthkumar)
- [Jérôme Wacongne](https://github.com/ch4mpy)
- [Ben McCann](http://www.benmccann.com)
- [Ryan Koval](https://github.com/rkoval)
- [Saket Kumar](https://github.com/skate056)
- [Julian Peeters](https://github.com/julianpeeters)
- [Przemysław Dubaniewicz](https://github.com/przemekd)
- [Neville Li](https://github.com/nevillelyh)
- [Michel Davit](https://github.com/RustedBones)
- [Mārtiņš Kalvāns](https://github.com/sisidra)
- [Oskar Jung](https://github.com/ojung)
- [Martin Achenbach](https://github.com/drachenbach)
