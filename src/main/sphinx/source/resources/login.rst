/login
======


	This resource checks the credentials of a user to perform a login. No further authentication
	measures are provided at the current time - such as session creation or access granting. It's
	just a plain simple check if the credentials are fine.

POST
^^^^

	This endpoint checks the login credentials from the request and checks them with the stored credentials
	in the system.

Request
"""""""

	The request is expected to contain an entity with the fields:


	=========== ======================= =========== =================================
	Query       Format                              Usage
	=========== ======================= =========== =================================
	username    String                  required	Username part of the credentials.
	password    String                  required	Secret part of the credentials.
	=========== ======================= =========== =================================

Responses
"""""""""

	A successful response is indicated by a status code of ``200`` and has no response entity. If the
	login fails, a generic status code of ``403 - Forbidden`` is returned. One of the following endpoint 
	specific errors may occur:

	==== ====================================== ================================================================
	Code Reason                                 Description
	==== ====================================== ================================================================
	600  REQUEST_ENTITY_MISSING                 The request has not sent an entity in its body.
	601  REQUEST_ENTITY_FIELD_USERNAME_MISSING  Field "username" is missing.
	604  REQUEST_ENTITY_FIELD_PASSWORD_MISSING  Field "password" is missing.
	==== ====================================== ================================================================
