Marker2sequence
=========

:Author: Pierre-Yves Chibon <pierre-yves.chibon@wur.nl>, <pingou@pingoured.fr>
:Author: Richard Finkers <richard.finkers@wur.nl>


Provides biologist a way to mine through their QTL interval for a potential candidate gene
using data integration.


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

 mvn clean install -DskipTests=false


License
-------

.. _Apache license, V2.0: http://www.apache.org/licenses/LICENSE-2.0.html

``Marker2Sequence`` is licensed under the `Apache license, V2.0`_

