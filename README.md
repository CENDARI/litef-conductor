# About

Litef is a semantic indexer and the Data API service for Cendari project.

# Building

You need CKAN and Virtuoso systems already installed.

In order to build and run **litef-conductor**, you need [SBT](www.scala-sbt.org).

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


