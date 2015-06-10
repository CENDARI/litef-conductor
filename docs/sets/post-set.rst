POST /sets
==========

Creating sets in a dataspace is allowed only for editors and admins of a dataspace, and sysadmins.
A user with a ``member`` role on a dataspace has read-only access and therefore cannot add sets.

Request JSON object:

======================= ======= ===========
Attribute               Type    Description
======================= ======= ===========
name                    String  Unique name of the set 
                                (a string between 2 and 100 characters long, 
                                containing only lowercase alphanumeric characters, ``-`` and ``_``)
title (optional)        String  Set title
description (optional)  String  Additional information about the set
dataspaceId             String  Unique id of the dataspace that the set belongs to
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

    curl -H "Authorization: your-apikey" -H "Content-Type: application/json" \
        -d '{"name":"9200316-bibliographicresource_3000073808381", "title":"Aerial photograph, Lombartzyde, Belgium, 1917", "dataspaceId":"099c3cae-9fe2-4acf-970f-b5b149eeae24"}' \
        http://localhost:42042/v1/sets

Example response::

    {
      "id": "2bba037f-d5c3-482a-965c-89f057582b37",
      "url": "http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37",
      "name": "9200316-bibliographicresource_3000073808381",
      "title": "Aerial photograph, Lombartzyde, Belgium, 1917",
      "description": "",
      "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
      "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
      "resources": "http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37/resources",
      "state": "active"
    } 