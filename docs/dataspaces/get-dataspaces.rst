GET /dataspaces
===============

Request filter parameters:

==========================  ======= ======================================================================
Parameter                   Type    Description
==========================  ======= ======================================================================
state (optional)            String  Valid values are: "active", "deleted", and "all". Default is "active".
==========================  ======= ======================================================================

Response JSON object:

==========  ======= ==========================
Attribute   Type    Description
==========  ======= ==========================
data        Array   Array of dataspace objects
==========  ======= ==========================

Dataspace object:

==============  ======= ==========================================
Attribute       Type    Description
==============  ======= ==========================================
id              String  Unique id of the dataspace object
url             String  Dataspace resource URL
resources       String  URL for the dataspace's resources
name            String  Dataspace unique name
title           String  Dataspace title
description     String  Additional information about the dataspace
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces

Example response::

    {
      "data": [{
        "id": "c5633d7f-8bb8-4b77-be22-6ee722ff4705",
        "url": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705",
        "resources": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources",
        "name": "cendari-dataspace",
        "title": "CENDARI dataspace",
        "description": "Content of this dataspaces is created within the CENDARI project",
        "state": "active"
      }, ...]
    } 

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces?state=all
