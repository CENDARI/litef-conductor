DELETE /privileges/$id
======================

Parameters:

==========  ======= ========================================
Parameter   Type    Description
==========  ======= ========================================
id          String  Unique identifier of the privilege object
==========  ======= ========================================

HTTP Status Code:

- ``204`` - Privilege deleted
- ``403`` - User not authorized to delete the privilege
- ``404`` - Privilege with specified id not found 

Example request::

    curl -X DELETE -H "Authorization: your-apikey" http://localhost:42042/v1/privileges/c6a4d3f7-3ac7-457d-9254-4528861d8816
