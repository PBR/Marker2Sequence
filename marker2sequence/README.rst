Marker2sequence-lib
====================

:Author: Pierre-Yves Chibon <pierre-yves.chibon@wur.nl>, <pingou@pingoured.fr>
:Author: Richard Finkers <richard.finkers@wur.nl>


Java library to query a sparql endpoint containing genome annotation together
with other data to integrate (UniProt, GO, UniPathway...).
This integration is then used to mine through QTL intervals for a potential
candidate gene


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

The unit-tests of ``marker2sequence-lib`` are off by default.
These tests relie on a sparql endpoint being available and containing a
defined number of information.

To build the project with the unit-test, you can run:

 mvn clean install -DskipTests=falsee

This will generate a .jar file under the ``target`` folder. This jar file
can then be used as any other libraries for your java project.

License
-------

.. _Apache license, V2.0: http://www.apache.org/licenses/LICENSE-2.0.html

``Marker2Sequence`` is licensed under the `Apache license, V2.0`_

