# About

Litef is a semantic indexer and the Data API service for Cendari project.

# Building

You need CKAN and Virtuoso systems already installed.

In order to build and run **litef-conductor**, you need [SBT](www.scala-sbt.org).

In order to compile, you can use the *sbt compile* command, for starting
the service, execute *sbt reStart*.

## Environment requisites

If you have plans to use a non local CKAN/PostgreSQL and Virtuoso, you need to
forward the necessary ports to your local system through an SSH tunnel.

You should copy the src/main/resources/application.conf-example to
src/main/resources/application.conf and adjust accordingly. Do **not**
commit the config file to the respository and do **not** package
it in the assembly jar file.

