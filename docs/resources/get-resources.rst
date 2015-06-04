GET /resources
==============================

Request filter parameters:

==========================  =================== ======================================================================
Parameter                   Type                Description
==========================  =================== ======================================================================
dataspaceId (optional)      String              Unique identifier of the dataspace object
state (optional)            String              Valid values are: "active", "deleted", and "all". Default is "active".
since (optional)            DateTime (ISO 8601) If specified, only the resources created or modified after the specified date/time will be included
until (optional)            DateTime (ISO 8601) If specified, only the resources created or modified before the specified date/time will be included
==========================  =================== ======================================================================

Response JSON object:

======================  ======= ==========================
Attribute               Type    Description
======================  ======= ==========================
nextPage (optional)     String  URL of the next page of results
currentPage (optional)  String  URL of the current page of results
data (optional)         Array   Array of resource objects
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
size            Integer Resource file size (in KB)
created         String  Timestamp created in ISO 8601 format
created_epoch   Integer Timestamp created
modified        String  Timestamp last modified in ISO 8601 format
modified_epoch  Integer Timestamp last modified
setId           String  Unique identifier of the set that the resource belongs to
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request (with the next page, resources that belong to a dataspace)::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/resources

Example response::

    {
      "nextPage": "http://localhost:42042/v1/resources/query/results/AAAAAAAAAAAAAAFNuaKzXwAAAAAAAAAKAAAACg==",
      "currentPage": "http://localhost:42042/v1/resources/query/results/AAAAAAAAAAAAAAFNuaKzXwAAAAAAAAAAAAAACg==",
      "data": [{
        "id": "fee6284a-154d-4a33-832c-1836c5561658",
        "url": "http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658",
        "dataUrl": "http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658/data",
        "name": "CENDARI-logo.jpg",
        "description": "Updated resource: CENDARI logo 150x150 without text",
        "format": "JPG",
        "mimetype": "application/octet-stream",
        "size": 1,
        "created_epoch": 1427724584090,
        "modified_epoch": 1427724584090,
        "setId": "5038ef1a-475b-4312-b395-b63a2e2252fc",
        "state": "active",
        "created": "2015-03-30T16:09:44Z",
        "modified": "2015-03-30T16:09:44Z"
      }, ...],
      "end": false
    }

Example response (when there are no more resources in the dataspace to return)::
    
    {
      "end": true
    }

Example request (with filter parameters ``dataspaceId`` and ``state``)::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/resources?dataspaceId=c5633d7f-8bb8-4b77-be22-6ee722ff4705\&state=all

Example requests (with filter parameters ``since`` and ``until``)::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/resources?since=2015-05-20T13:00:00\&until=2015-06-01T13:00:00
