========
sbt-avro
========

sbt-avro is a plugin for `sbt`_-0.12.1 for generating the Java sources for 
Avro_ schemas and protocols.

.. _sbt: https://github.com/harrah/xsbt/
.. _Avro: http://avro.apache.org/ 

Usage
=====

Install the plugin
------------------

Add the plugin according to the `sbt documentation`_.

.. _`sbt documentation`: https://github.com/harrah/xsbt/wiki/Getting-Started-Using-Plugins

For instance, add the following lines to the file ``hello/project/build.sbt`` in your
project directory::

    resolvers += "cavorite" at "http://files.cavorite.com/maven/"

    addSbtPlugin("com.cavorite" % "sbt-avro" % "0.2")


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
sourceDirectory     ``source-directory``     ``src/main/avro``                    Path containing ``*.avsc`` and ``*.avpr`` files.
javaSource          ``java-source``          ``$sourceManaged/compiled_avro``     Path for the generated ``*.java`` files.
version             ``version``              ``1.7.2``                            Version of the Avro library should be used. A dependency to ``"org.apache.avro" % "avro-compiler" % "$version"`` is automatically added to ``libraryDependencies``.
===============     ====================     ================================     ===============


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

.. _`sbt-protobuf`: https://github.com/gseitz/sbt-protobuf
.. __: http://cavorite.com


