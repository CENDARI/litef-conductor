PUT /dataspaces/$id
===================

Request JSON object:

=============== ======= ===========
Attribute       Type    Description
=============== ======= ===========
title           String  Dataspace title
description     String  Additional information about the dataspace
visibility      String  Visibility can be ``private`` or ``public``.
                        If a dataspace is ``public``, all registered users have
                        read-only access to it. If a dataspace is ``private``, only 
                        users with privileges can access it.
origin          String  Information about origin of the dataspace (NTE, AtoM, ...)
=============== ======= ===========

Response JSON object:

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
origin          String  Information about origin of the dataspace (NTE, AtoM, ...)
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request::

    curl -X PUT -H "Authorization: your-apikey" -H "Content-Type: application/json" \
        -d '{"title":"CENDARI dataspace", "description":"Content of this dataspaces is created within the CENDARI project", "visibility":"public", "origin": ""}' \
        http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705

Example response::

    {
      "id": "c5633d7f-8bb8-4b77-be22-6ee722ff4705",
      "url": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705",
      "resources": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources",
      "sets": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/sets",
      "name": "cendari-dataspace",
      "title": "CENDARI dataspace",
      "description": "Content of this dataspaces is created within the CENDARI project",
      "visibility": "public",
      "origin": "",
      "state": "active"
    }