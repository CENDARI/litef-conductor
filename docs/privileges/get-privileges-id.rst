GET /privileges/$id
===================

Parameters:

==========  ======= ========================================
Parameter   Type    Description
==========  ======= ========================================
id          String  Unique identifier of the privilege object
==========  ======= ========================================

Response JSON object:

=============== ======= ===========
Attribute       Type    Description
=============== ======= ===========
id              String  Unique id of the privilege object
url             String  Privilege resource URL
userId          String  Unique id of the user with the given privilege
userUrl         String  User resource URL
dataspaceId     String  Unique id of the dataspace object
dataspaceUrl    String  Dataspace resource URL
role            String  User's role in the dataspace ("member", "editor" or "admin")
state           String  State can be "active" or "deleted"
=============== ======= ===========

Example request::

    curl -H "Authorization: your-apikey" http://localhost:42042/v1/privileges/c6a4d3f7-3ac7-457d-9254-4528861d8816

Example response::

    {
      "id": "c6a4d3f7-3ac7-457d-9254-4528861d8816",
      "url": "http://localhost:42042/v1/privileges/c6a4d3f7-3ac7-457d-9254-4528861d8816",
      "userId": "716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
      "userUrl": "http://localhost:42042/v1/users/716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
      "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
      "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
      "role": "member",
      "state": "active"
    }