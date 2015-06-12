GET /resources/$id
==================

Returns resource metadata

Parameters:

==========  ======= ========================================
Parameter   Type    Description
==========  ======= ========================================
id          String  Unique identifier of the resource object
==========  ======= ========================================

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
size            Integer Resource file size (in KB)
created         String  Timestamp created in ISO 8601 format
created_epoch   Integer Timestamp created
modified        String  Timestamp last modified in ISO 8601 format
modified_epoch  Integer Timestamp last modified
setId           String  Unique identifier of the set that the resource belongs to
state           String  State can be "active" or "deleted"
==============  ======= ==========================================

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/resources/fee6284a-154d-4a33-832c-1836c5561658

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
      "state": "active",
      "created": "2015-03-30T16:09:44Z",
      "modified": "2015-03-30T16:09:44Z"
    }