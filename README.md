# About

Litef is a semantic indexer and the Data API service for Cendari project.

# Building

You need CKAN and Virtuoso systems already installed.

In order to build and run **litef-conductor**, you need Java 1.7.x, Scala 2.10.4
and [SBT](www.scala-sbt.org).

In order to compile, you can use the `sbt compile` command, for starting
the service, execute `sbt reStart`.

## Environment requisites

If you have plans to use a non local CKAN/PostgreSQL and Virtuoso, you need to
forward the necessary ports to your local system through an SSH tunnel.

You should copy the `PROJECTDIR/src/main/resources/application.conf-example` to
`PROJECTDIR/application.conf` and adjust it accordingly. Do **not** commit
the config file to the respository and do **not** package it in the assembly
jar file.

When starting the application with `reStart`, it will start the server using
that configuration file.

If you start the server manually from the assembly jar file, you need to
pass the `-Dconfig.file=PROJECTDIR/application.conf` argument manually.

## Data location

Indexer needs to be able to access CKAN files directly (litef.ckan.localStoragePrefix),
so you need to have a local ckan installation, or to use sshfs. Also, it needs
to be able to create and write into the directory specified in
litef.indexer.localStoragePrefix.

You need to add the litef.indexer.localStoragePrefix path to the
DirsAllowed variable in virtuoso.ini

In order not to have collisions between the main server and a local instance
(in the case where you use the main server's databases and files), you need
to create a local file /opt/litef/conductor:disable-document-processing
before starting the local service.

## Packaging

For the production environments, the sbt assembly command is provided.
It builds a single jar file that contains all the libraries and litef.

For quicker deployment, but more involved maintainance, you can use
the sbt package command and maintain the libraries manually. If you
need to collect all the libraries, use sbt oneJar and extract the library
jar files to the runtime classpath.


