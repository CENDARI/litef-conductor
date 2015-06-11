PUT /sets/$id
=============

Set update is allowed only for editors and admins of a dataspace, and sysadmins.
A user with a ``member`` role on a dataspace has read-only access and therefore cannot update sets in that dataspace.
Moving a set from one dataspace to another is not allowed.

Request JSON object:

======================= ======= ===========
Attribute               Type    Description
======================= ======= ===========
title (optional)        String  Set title
description (optional)  String  Additional information about the set
======================= ======= ===========

Response JSON object:

==============  ======= ==========================================
Attribute       Type    Description
==============  ======= ==========================================
id              String  Unique id of the set object
url             String  Set resource URL
name            String  Set unique name
title           String  Set title
description     String  Additional information about the set
dataspaceId     String  Unique id of the dataspace that the set belongs to
dataspaceUrl    String  URL for the dataspace that the set belongs to
resources       String  URL for the set's resources
state           String  State can be ``active`` or ``deleted``
==============  ======= ==========================================

Example request::

    curl -X PUT -H "Authorization: your-apikey" -H "Content-Type: application/json" \
        -d '{"description":"This is a copy of an aerial photograph."}' \
        http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37

Example response::

    {
      "id": "2bba037f-d5c3-482a-965c-89f057582b37",
      "url": "http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37",
      "name": "9200316-bibliographicresource_3000073808381",
      "title": "Aerial photograph, Lombartzyde, Belgium, 1917",
      "description": "This is a copy of an aerial photograph.",
      "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
      "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
      "resources": "http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37/resources",
      "state": "active"
    } 