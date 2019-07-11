========
sbt-avro
========

.. image:: https://travis-ci.org/sbt/sbt-avro.svg?branch=master
        :target: https://travis-ci.org/sbt/sbt-avro

Overview
========

sbt-avro is a plugin for `sbt`_-0.13 for generating the Java sources for Avro_ schemas and protocols.
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

    addSbtPlugin("com.cavorite" % "sbt-avro-1-8" % "1.1.6")
 

Scope
=====
All settings and tasks are in the ``avro`` scope. If you want to execute the
``generate`` task directly, just run ``avro:generate``.


Settings
========

===============     ====================     ================================     ===============
Name                Name in shell            Default                              Description
===============     ====================     ================================     ===============
sourceDirectory     ``source-directory``     ``src/main/avro``                    Path containing ``*.avsc``, ``*.avdl`` and ``*.avpr`` files.
javaSource          ``java-source``          ``$sourceManaged/compiled_avro``     Path for the generated ``*.java`` files.
version             ``version``              ``1.8.2``                            Version of the Avro library should be used. A dependency to ``"org.apache.avro" % "avro-compiler" % "$version"`` is automatically added to ``libraryDependencies``.
stringType          ``string-type``          ``CharSequence``                     Java type for string elements. Possible values: ``CharSequence`` (by default), ``Utf8`` and ``String``.
===============     ====================     ================================     ===============

Example
-------

For example, if you want to change the Java type of the string elements in
the schema, you can add the following lines to your ``build.sbt`` file::

    (stringType in AvroConfig) := "String"


Tasks
=====

===============     ================    ==================
Name                Name in shell       Description
===============     ================    ==================
generate            generate            Compiles the Avro files. This task is automatically executed every time the project is compiled.
===============     ================    ==================


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
