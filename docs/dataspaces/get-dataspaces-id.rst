GET /dataspaces/$id
===================

Parameters:

==========  ======= =========================================
Parameter   Type    Description
==========  ======= =========================================
id          String  Unique identifier of the dataspace object
==========  ======= =========================================

Response JSON object:

==============  ======= ==========================================
Attribute       Type    Description
==============  ======= ==========================================
id              String  Unique identifier of the dataspace object
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

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces/33175d43-6f5c-4099-abbf-8197f9c2df4b

Example response::

    {
      "id": "c5633d7f-8bb8-4b77-be22-6ee722ff4705",
      "url": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705",
      "resources": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources",
      "sets": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/sets",
      "name": "cendari-dataspace",
      "title": "CENDARI dataspace",
      "description": "Content of this dataspaces is created within the CENDARI project",
      "visibility": "private",
      "origin": "",
      "state": "active"
    } 