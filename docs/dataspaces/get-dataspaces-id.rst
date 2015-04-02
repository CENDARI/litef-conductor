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
name            String  Dataspace unique name
title           String  Dataspace title
description     String  Additional information about the dataspace
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces/33175d43-6f5c-4099-abbf-8197f9c2df4b

Example response::

    {
      "id": "c5633d7f-8bb8-4b77-be22-6ee722ff4705",
      "url": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705",
      "resources": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources",
      "name": "cendari-dataspace",
      "title": "CENDARI dataspace",
      "description": "Content of this dataspaces is created within the CENDARI project",
      "state": "active"
    } 