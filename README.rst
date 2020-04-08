========
sbt-avro
========

.. image:: https://travis-ci.org/sbt/sbt-avro.svg?branch=master
        :target: https://travis-ci.org/sbt/sbt-avro

Overview
========

sbt-avro is a `sbt` plugin for generating the Java sources for Avro schemas and protocols.
It tries to be clever in compiling schemas with referenced types before dependent ones.

.. _sbt: http://www.scala-sbt.org/
.. _Avro: http://avro.apache.org/

Usage
=====

Install the plugin
------------------

Add the plugin according to the `sbt documentation`_.

.. _`sbt documentation`: http://www.scala-sbt.org/0.13/docs/Using-Plugins.html

For instance, add the following lines to the file ``project/plugins.sbt`` in your
project directory::

    addSbtPlugin("com.cavorite" % "sbt-avro" % "1.1.7")
    libraryDependencies += "org.apache.avro" % "avro-compiler" % "1.9.x" // or 1.8.x depending on the avroVersion you want to use



Settings
========

===============               ===================================   =============
Name                          Default                               Description
===============               ===================================   =============
avroSource                    ``avro`` folder in config directory   Default avro source directory containing ``*.avsc``, ``*.avdl`` and ``*.avpr`` files.
avroVersion                   ``1.9.2``                             Apache avro library version. A dependency to ``"org.apache.avro" % "avro" % "$version"`` is automatically added to ``libraryDependencies``.
avroStringType                ``CharSequence``                      Type for representing strings. Possible values: ``CharSequence``, ``String``, ``Utf8``.
avroUseNamespace              ``false``                             Validate that directory layout reflects namespaces, i.e. ``src/main/avro/com/myorg/MyRecord.avsc``.
avroFieldVisibility           ``public_deprecated``                 Field Visibility for the properties. Possible values: ``private``, ``public``, ``public_deprecated``.
avroEnableDecimalLogicalType  ``true``                              Set to true to use ``java.math.BigDecimal`` instead of ``java.nio.ByteBuffer`` for logical type ``decimal``.
===============               ===================================   =============

Example
-------

For example, if you want to change the Java type of the string elements in
the schema, you can add the following lines to your ``build.sbt`` file::

    avroStringType := "String"


Tasks
=====

===============   ==================
Name              Description
===============   ==================
avroGenerate      Generate the Java sources for the Avro files. This task is automatically executed every time the project is compiled.
===============   ==================


License
=======
This program is distributed under the BSD license. See the file ``LICENSE`` for
more details.

Credits
=======

`sbt-avro` is maintained by the `sbt Community`_. The initial code was based on a
similar plugin: `sbt-protobuf`_. Feel free to send your comments and bug
reports.

Contributors
------------

- `Juan Manuel Caicedo`_
- `Brennan Saeta`_
- `Daniel Lundin`_
- `Vince Tse`_
- `Ashwanth Kumar`_
- `Jérôme - Ch4mpy - Wacongne`_
- `Ben McCann`_
- `Ryan Koval`_
- `Saket`_
- `Julian Peeters`_
- `Przemysław Dubaniewicz`_
- `Neville Li`_

.. _`sbt Community`: http://www.scala-sbt.org/release/docs/Community-Plugins.html
.. _`sbt-protobuf`: https://github.com/gseitz/sbt-protobuf
.. _`Juan Manuel Caicedo`: https://cavorite.com
.. _`Brennan Saeta`: https://github.com/saeta
.. _`Daniel Lundin`: https://github.com/dln
.. _`Vince Tse`: https://github.com/vtonehundred
.. _`Ashwanth Kumar`: https://github.com/ashwanthkumar
.. _`Jérôme - Ch4mpy - Wacongne`: https://github.com/ch4mpy
.. _`Ben McCann`: http://www.benmccann.com
.. _`Ryan Koval`: https://github.com/rkoval
.. _`Saket`: https://github.com/skate056
.. _`Julian Peeters`: https://github.com/julianpeeters
.. _`Przemysław Dubaniewicz`: https://github.com/przemekd
.. _`Neville Li`: https://github.com/nevillelyh
