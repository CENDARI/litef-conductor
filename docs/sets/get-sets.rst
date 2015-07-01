GET /sets
===============

Request filter parameters:

==========================  ======= ======================================================================
Parameter                   Type    Description
==========================  ======= ======================================================================
dataspaceId (optional)      String  Unique identifier of the dataspace object
state (optional)            String  Valid values are: ``active``, ``deleted``, and ``all``. Default is ``active``.
==========================  ======= ======================================================================

Response JSON object:

====================== =========== ==========================
Attribute              Type        Description
====================== =========== ==========================
nextPage (optional)    String      URL of the next page of results
currentPage (optional) String      URL of the current page of results
data (optional)        Array       Array of set objects
end                    Boolean     ``false`` if there are no more sets to return, otherwise ``true``
====================== =========== ==========================

Set object:

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
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/sets

Example response::
    
    {
      "nextPage": "http://localhost:42042/v1/sets/query/results/AAAAAAAAAAAAAAFN1yVT1AAAAAAAAAAKAAAACg==",
      "currentPage": "http://localhost:42042/v1/sets/query/results/AAAAAAAAAAAAAAFN1yVT1AAAAAAAAAAAAAAACg==",
      "data": [{
        "id": "2bba037f-d5c3-482a-965c-89f057582b37",
        "url": "http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37",
        "name": "9200316-bibliographicresource_3000073808381",
        "title": "Aerial photograph, Lombartzyde, Belgium, 1917",
        "description": "",
        "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "resources": "http://localhost:42042/v1/sets/2bba037f-d5c3-482a-965c-89f057582b37/resources",
        "state": "active"
      },...],
      "end": false
    }
    
Example response when there are no more results to return::

    {
      "end": true
    }

Example request with filter parameter ``dataspaceId``::
    
    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/sets?dataspaceId=099c3cae-9fe2-4acf-970f-b5b149eeae24