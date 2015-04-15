GET /privileges
===============

Returns an array of user privileges for dataspaces. The authenticated user 
can only list privileges for the dataspaces whose member he is.

The request may include filtering parameters dataspaceId and/or userId (see example_)

Response JSON object:

==========  ======= ==================================================
Attribute   Type    Description
==========  ======= ==================================================
data        Array   Array of user privileges for dataspaces.
==========  ======= ==================================================

Privilege object:

=============== ======= ============================================================
Attribute       Type    Description
=============== ======= ============================================================
id              String  Unique id of the privilege object
url             String  Privilege resource URL
userId          String  Unique id of the user with the given privilege
userUrl         String  User resource URL
dataspaceId     String  Unique id of the dataspace object
dataspaceUrl    String  Dataspace resource URL
role            String  User's role in the dataspace ("member", "editor" or "admin")
state           String  State can be "active" or "deleted"
=============== ======= ============================================================

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/privileges

Example response::

    {
      "data": [{
        "id": "c6a4d3f7-3ac7-457d-9254-4528861d8816",
        "url": "http://localhost:42042/v1/privileges/c6a4d3f7-3ac7-457d-9254-4528861d8816",
        "userId": "716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
        "userUrl": "http://localhost:42042/v1/users/716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
        "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "role": "member",
        "state": "active"
      }, ...]
    }

.. _example: 

Example request::

    curl -H "Authorization: your-apikey"  http://localhost:42042/v1/privileges?dataspaceId=099c3cae-9fe2-4acf-970f-b5b149eeae24

Example response::

    {
      "data": [{
        "id": "f44aa8de-e735-4ee3-af2d-d076e1162715",
        "url": "http://localhost:42042/v1/privileges/f44aa8de-e735-4ee3-af2d-d076e1162715",
        "userId": "494dc503-1766-42f1-83da-8ff173b11af5",
        "userUrl": "http://localhost:42042/v1/users/494dc503-1766-42f1-83da-8ff173b11af5",
        "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "role": "member",
        "state": "active"
      }, {
        "id": "b1807ea8-da5f-49c7-a455-c28613bd74fe",
        "url": "http://localhost:42042/v1/privileges/b1807ea8-da5f-49c7-a455-c28613bd74fe",
        "userId": "eb92e2f7-e7a8-48f6-9e8d-d03b4b8ad8df",
        "userUrl": "http://localhost:42042/v1/users/eb92e2f7-e7a8-48f6-9e8d-d03b4b8ad8df",
        "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "role": "admin",
        "state": "active"
      }, {
        "id": "5bb7878e-1c19-42bf-a963-b00c7e889e1f",
        "url": "http://localhost:42042/v1/privileges/5bb7878e-1c19-42bf-a963-b00c7e889e1f",
        "userId": "741219ae-6e2b-4e72-85b5-934b18b120df",
        "userUrl": "http://localhost:42042/v1/users/741219ae-6e2b-4e72-85b5-934b18b120df",
        "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "role": "editor",
        "state": "active"
      }, {
        "id": "c6a4d3f7-3ac7-457d-9254-4528861d8816",
        "url": "http://localhost:42042/v1/privileges/c6a4d3f7-3ac7-457d-9254-4528861d8816",
        "userId": "716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
        "userUrl": "http://localhost:42042/v1/users/716eeb7e-1fec-4eab-9e99-c35cbe7ae96b",
        "dataspaceId": "099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "dataspaceUrl": "http://localhost:42042/v1/dataspaces/099c3cae-9fe2-4acf-970f-b5b149eeae24",
        "role": "member",
        "state": "active"
      }]
    }