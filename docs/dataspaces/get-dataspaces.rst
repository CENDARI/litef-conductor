GET /dataspaces
===============

Request filter parameters:

==========================  =================== ======================================================================
Parameter                   Type                Description
==========================  =================== ======================================================================
visibility (optional)       String              Valid values are: ``public``, ``private``, and ``all``. Default is ``all``.
state (optional)            String              Valid values are: "active", "deleted", and "all". Default is "active".
since (optional)            DateTime (ISO 8601) If specified, only the resources created or modified after the specified date/time will be included
until (optional)            DateTime (ISO 8601) If specified, only the resources created or modified before the specified date/time will be includeds
==========================  =================== ======================================================================

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
sets            String  URL for the dataspace's sets
name            String  Dataspace unique name
title           String  Dataspace title
description     String  Additional information about the dataspace
visibility      String  Visibility can be ``private`` or ``public``
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
        "sets": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/sets",
        "name": "cendari-dataspace",
        "title": "CENDARI dataspace",
        "description": "Content of this dataspaces is created within the CENDARI project",
        "visibility": "private",
        "state": "active"
      }, ...]
    } 

Example request (with filter parameter ``state``)::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces?state=all

Example request (with filter parameters ``since`` and ``until``)::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces?since=2015-05-20T13:00:00\&until=2015-06-01T13:00:00

Example response when there are no valid results::

    {
      "end": true
    }