GET /dataspaces/$did/resources
==============================

Parameters:

==========  ======= ========================================
Parameter   Type    Description
==========  ======= ========================================
did         String  Unique identifier of the dataspace object
==========  ======= ========================================

Response JSON object:

======================  ======= ==========================
Attribute               Type    Description
======================  ======= ==========================
nextPage (optional)     String  URL of the next page of results
currentPage (optional)  String  URL of the current page of results
data (optional)         Array   Array of resource objects that belong to the dataspace
end                     Boolean ``false`` if there are no more resources to return, otherwise ``true``
======================  ======= ==========================

Resource object:

==============  ======= ==========================================
Attribute       Type    Description
==============  ======= ==========================================
id              String  Unique id of the resource object
url             String  Resource object URL
dataUrl         String  Resource file URL
name            String  Resource title
description     String  Additional information about the resource
format          String  Resource file format
mimetype        String  Resource file MIME Type
size            Integer Resource file size
created         Integer Timestamp created
modified        Integer Timestamp last modified
setId           String  Unique identifier of the set that the resource belongs to
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request (with the next page, resources that belong to a dataspace)::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources

Example response::

    {
      "nextPage": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources/query/results/AAAAAAAAAAAAAAFMazuo2AAAAAoAAAAK",
      "currentPage": "http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources/query/results/AAAAAAAAAAAAAAFMazuo2AAAAAAAAAAK",
      "data": [{
        "id": "fee6284a-154d-4a33-832c-1836c5561658",
        "url": "http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658",
        "dataUrl": "http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658/data",
        "name": "CENDARI-logo.jpg",
        "description": "CENDARI logo 150x150 without text",
        "format": "JPG",
        "mimetype": "",
        "size": 0,
        "created": 1427724584090,
        "modified": 1427724584090,
        "setId": "5038ef1a-475b-4312-b395-b63a2e2252fc",
        "state": "active"
      }, ...],
      "end": false
    }

Example response (when there are no more resources int the dataspace to return)::
    
    {
      "end": true
    }
