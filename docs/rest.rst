CENDARI REST API
================

Server location
---------------

The API is available only internally on CENDARI servers and does not accept requests from outside (local connections and ssh tunnels only).

If you want to connect to the API from outside, you can use the server connect.cendari.dariah.eu. This server can be accessed using ssh on the standard port 22 from worldwide using SSH keys, no passwords.
Please note that you can only access the API using the account, you do not get real shell access. See the server's page for details on how to get an Account.

Authorization
-------------

In order to get any result, you need to provide your CKAN API key as the 'Authorization' HTTP request header.

Your CKAN API Key: log in to CKAN, go to your Profile and copy the value for the private API Key in the lower left corner of the page.

In order for an application (e.g. VRE) to get this key for a Shibboleth-authenticated user, it should post Shibboleth attributes to the API. The key will be returned in response, and it should be used for future API requests.

API Requests
------------

.. toctree::
    :maxdepth: 2

    status/get-status
    session/post-session
    users/get-users
    users/get-users-id
    privileges/get-privileges
    privileges/get-privileges-id
    privileges/post-privileges
    privileges/delete-privileges-id
