POST /session
=============
    
Request JSON object:

==========  ======= ===========
Attribute   Type    Description
==========  ======= ===========
eppn        String  User's eduPersonPrincipalName
mail        String  User's e-mail address
cn          String  User's full name
==========  ======= ===========

Response JSON object:

==========  ======= ===========
Attribute   Type    Description
==========  ======= ===========
username    String  User's unique username
sessionKey  String  Authentication key
==========  ======= ===========

Example request::

    curl -H "Content-Type: application/json" -d '{"eppn":"cendari_notes@dariah.eu", "mail":"cendari-admins@gwdg.de", "cn":"CENDARI Notes Admin"}' http://localhost:42042/v1/session

Example response::

    {
      "username": "cendari_notes",
      "sessionKey": "******"
    }