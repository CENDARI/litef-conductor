POST /dataspaces/$did/resources
===============================

Parameters:

==========  ======= ==========================================
Parameter   Type    Description
==========  ======= ==========================================
did         String  Unique identifier of the dataspace object
==========  ======= ==========================================

Request multipart/form-data:

=============== ======= ===========
Field           Type    Description
=============== ======= ===========
file            File    File for upload
name            String  Resource title 
format          String  File format (XML, RDF, JSON, JPG, ...)
description     String  Additional information about the resource
setId           String  If setId is not specified, a new set will be created and the resource will be assigned to it
state           String  State can be "active" or "deleted"
=============== ======= ===========

Response JSON object:

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
created         String  Timestamp created in ISO 8601 format
created_epoch   Integer Timestamp created
modified        String  Timestamp last modified in ISO 8601 format
modified_epoch  Integer Timestamp last modified
setId           String  Unique identifier of the set that the resource belongs to
==============  ======= ==========================================

Example request::

    curl -H "Authorization: your-apikey"  \
        -F "file=@/path-to-file/cendari-logo.jpg" \
        -F "name=CENDARI-logo.jpg" \
        -F "format=JPG" \
        -F "description=CENDARI logo 150x150 without text" \
        http://localhost:42042/v1/dataspaces/c5633d7f-8bb8-4b77-be22-6ee722ff4705/resources

Example response::

    {
      "id": "fee6284a-154d-4a33-832c-1836c5561658",
      "url": "http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658",
      "dataUrl": "http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658/data",
      "name": "CENDARI-logo.jpg",
      "description": "CENDARI logo 150x150 without text",
      "format": "JPG",
      "mimetype": "application/octet-stream",
      "size": 1,
      "created_epoch": 1427724584090,
      "modified_epoch": 1427724584090,
      "setId": "5038ef1a-475b-4312-b395-b63a2e2252fc",
      "state": "active"
      "created": "2015-03-30T16:09:44Z",
      "modified": "2015-03-30T16:09:44Z"
    }