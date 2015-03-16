GET /status
===========

Response JSON object:

==========  ======= ===========
Attribute   Type    Description
==========  ======= ===========
status      String  If the API is running, "OK" is returned 
hostname    String  FQDN of the API server
version     String  API version
==========  ======= ===========

Example request::

    curl http://localhost:42042/v1/status

Example response::

    {
      "status": "OK",
      "hostname": "int2.cendari.dariah.eu",
      "version": "1.1.0-SNAPSHOT"
    }            