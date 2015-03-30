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
size            Integer Resource file size
created         Integer Timestamp created
modified        Integer Timestamp last modified
setId           String  Unique identifier of the set that the resource belongs to
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
      "mimetype": "",
      "size": 0,
      "created": 1427724584090,
      "modified": 1427724584090,
      "setId": "5038ef1a-475b-4312-b395-b63a2e2252fc"
    }