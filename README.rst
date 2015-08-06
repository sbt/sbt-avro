========
sbt-avro
========

Overview
========
This is a fork of https://github.com/cavorite/sbt-avro

My aim here is to sort avsc files so that type that are re-used are compiled first

Usage
=====

Install the plugin
------------------

Add the plugin according to the `sbt documentation`_.

.. _`sbt documentation`: https://github.com/harrah/xsbt/wiki/Getting-Started-Using-Plugins

For instance, add the following lines to the file ``hello/project/build.sbt`` in your
project directory::

    resolvers += "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"

    addSbtPlugin("com.cavorite" % "sbt-avro" % "0.3.2")


Import the plugin settings
--------------------------

To activate the plugin, import its settings by adding the following lines to 
your ``hello/build.sbt`` file::

    seq( sbtavro.SbtAvro.avroSettings : _*)


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
version             ``version``              ``1.7.3``                            Version of the Avro library should be used. A dependency to ``"org.apache.avro" % "avro-compiler" % "$version"`` is automatically added to ``libraryDependencies``.
stringType          ``string-type``          ``CharSequence``                     Java type for string elements. Possible values: ``CharSequence`` (by default), ``Utf8`` and ``String``.
===============     ====================     ================================     ===============

Example
-------

For example, if you want to change the Java type of the string elements in 
the schema, you can add the following lines to your ``build.sbt``  file: 
    
    seq( sbtavro.SbtAvro.avroSettings : _*)
    
    (stringType in avroConfig) := "String"


Tasks
=====

===============     ================    ==================
Name                Name in shell        Description
===============     ================    ==================
generate            generate            Compiles the Avro files. This task is automatically executed everytime the project is compiled.
===============     ================    ==================


License
=======
This program is distributed under the BSD license. See the file ``LICENSE`` for
more details.

Credits
=======

`sbt-avro` is maintained by `Juan Manuel Caicedo`__. I wrote it based on `sbt-protobuf`_
(even this README file!). This is my first attempt to write an `sbt` plugin,
so feel free to send your comments and bug reports.

Contributors
------------

- `Brennan Saeta`_
- `Daniel Lundin`_
- `Vince Tse`_
- `Ashwanth Kumar`_

.. _`sbt-protobuf`: https://github.com/gseitz/sbt-protobuf
.. _`Brennan Saeta`: https://github.com/saeta
.. _`Daniel Lundin`: https://github.com/dln
.. _`Vince Tse`: https://github.com/vtonehundred
.. _`Ashwanth Kumar`: https://github.com/ashwanthkumar
.. __: http://cavorite.com


