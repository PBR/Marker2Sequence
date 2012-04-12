Marker2sequence-web
====================

:Author: Pierre-Yves Chibon <pierre-yves.chibon@wur.nl>, <pingou@pingoured.fr>
:Author: Richard Finkers <richard.finkers@wur.nl>


Web interface for the Marker2Sequence project.
This provide the biologist with an interface to mine through QTL intervals
for a potential candidate gene.


This project has been tested on Glassfish v3.


Use this project:
-----------------
A live instance of this project is freely available for Tomato at:
http://www.plantbreeding.wur.nl/BreeDB/marker2seq/

If you wish to run your own instance, building instructions are available below.


Get this project:
-----------------
Source:  https://github.com/PBR/Marker2Sequence


Building this project:
----------------------

This project uses maven to manage its dependencies, you can therefore easily
build the sources using:

 mvn clean install

This will generate a .war file under the ``target`` folder. This war file
can then be deploy in your application server.

Beware that this web interface relies on the java library ``marker2sequence-lib``,
so you will have to compile this one first.


License
-------

.. _Apache license, V2.0: http://www.apache.org/licenses/LICENSE-2.0.html

``Marker2Sequence`` is licensed under the `Apache license, V2.0`_

