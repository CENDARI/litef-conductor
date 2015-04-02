GET /users/$id
==============

Parameters:

==========  ======= ===========
Parameter   Type    Description
==========  ======= ===========
id          String  User's unique identifier
==========  ======= ===========

Response JSON object:

==========  ======= ===========
Attribute   Type    Description
==========  ======= ===========
id          String  User's unique identifier
url         String  User resource URL
username    String  User's unique username
fullname    String  User's full name
about       String  Additional information about the user
emailHash   String  MD5-hashed user's e-mail address
state       String  State can be "active" or "deleted"
==========  ======= ===========

Example request::

    curl -H "Authorization: your-apikey" http://localhost:42042/v1/users/716eeb7e-1fec-4eab-9e99-c35cbe7ae96b

Example response::

    {
      "id": "716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
      "url": "http://localhost:42042/v1/users/716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
      "username": "cendari_notes",
      "fullname": "CENDARI Notes Admin",
      "about": "",
      "emailHash": "15ec72819c9c5c09104fbb03e23c899f",
      "state": "active"
    } 