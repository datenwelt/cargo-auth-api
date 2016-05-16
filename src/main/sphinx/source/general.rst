General API usage
=================

Base URI
--------

Check the URI of this document. The base URI of this API can be found by replacing "v1/doc" by "v1/api". For example::

	http://api.datenwelt.io:8080/datenwelt-auth-api/v1/api

Overview
--------

This is a RESTful API providing resources over HTTP. JSON is used for data representations. 

Resources are clearly identified by their URI. All API calls are stateless and not related to each other by sessions or any other persistence mechanism. PUT and DELETE calls are idempotent each by definition - i.e. if called repeatedly with the same arguments they produce the same result.  GET and OPTION calls are safe - i.e. they do not cause side-effects.

Whenever possible standard HTTP status codes are generated in response to API requests such as
``404 - Not Found`` when a resource does not exist or ``400 - Bad Request`` when the request
could not be processed for formal reasons.

Terminology
-----------

==========	=========================================================================
Method		HTTP method such as GET, POST, PUT, DELETE	
Resource 	a relative URI reference that can be accessed by at least one HTTP method.
Entity		a representation of data in JSON
Endpoint 	a resource accessible via one HTTP method e.g. "GET /xyz"
==========	=========================================================================

Example:

	GET http://xyz.de/user/1

This represents a request to the endpoint ``GET http://xyz.de/user/1`` using the method ``GET``
to access the resource ``/user/1`` on the host ``xyz.de``. It may or may not return a JSON entity
which represents a ``user`` data object - depending on the API having such an endpoint and depending
on the existence and current availability of a respective data object.

Authentication
--------------

This API is used for authentication against the *datenwelt.io* system. Therefore, the API itself
is public and does not need any authentication.

The authentication workflow provided by this API starts with the user registering with this API. During
registration the user provides an e-mail address which is used as username in further API calls. 
Because the user is identified by his username, only one registration per e-mail address is supported.

After successfully registering with this API, the user receives an email containing an activation link.
This link points to an HTML page and contains a unique activation token in the fragment of the URI::
	
	http://xyz.de/activate.html#ACTIVATIONTOKEN

The activation token can be used to activate the user by sending it to the activation endpoint of this API. After the activation succeeded, the user is able to login with the credentials he provided upon registration.

	*!!! NOT IMPLEMENTED YET !!!*

	If the user logs in to the ``login`` resource, he receives an
	**authorization grant** which is a unique token representing the fact that the user successfully presented
	his credentials at a specific moment in time. The authorization grant can later be used to request
	an **access token** to access another protected API resource.

If the user loses his password, the ``password`` resource provides endpoints to recover the credentials
by sending the user a "password reset" token via e-mail. This token can be used to set a new password.

API Requests
------------

Request bodies
^^^^^^^^^^^^^^

Requests to this API should send a content type of ``application/json`` when they contain an entity in their
request body. No other content types are supported. If the request does not have an entity body, no content  header (Content-Type, Content-Length etc.) should be send at all.

CORS support
^^^^^^^^^^^^

Since this API is public, CORS support (including preflight requests) is available and activated whenever the request carries an ``Origin`` header. If no such header is sent, CORS authentication headers are not 
send with the response.

Referer requirement
^^^^^^^^^^^^^^^^^^^

Some API calls require a ``Referer`` header to be present to generate links in e-mail responses. If such a 
header is required, the corresponding endpoint documentation will mention it.


API Reponses
------------

Response bodies
^^^^^^^^^^^^^^^

If an API call requires an entity to be send with the response, a content type of ``application/json`` 
is used. However it is possible that a response has another content type than ``application/json`` for 
instance if the underlying server system generates an error message preventing or intercepting the 
actual API response. If the response carries an entity with a content type other than ``application/json``,
the API call should be considered as failed irrespective of any presumably positive HTTP status codes. 

Thus, applications calling this API should always check the content type of the response in addition to its status code.

Successful API calls
^^^^^^^^^^^^^^^^^^^^

API requests with a successful outcome are responded with status calls of the form ``2xx``.

Intermediary response stages
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Status codes of ``3xx`` represent an intermediary response stage which requires the client to 
take additional actions to complete the request. The most common case is a redirection from 
the currently accessed resource to another.

Server errors
^^^^^^^^^^^^^

Server errors are indicated by status codes of ``5xx``. Such errors can be considered on the server side. The same API request may be successful at a later time.

Client errors
^^^^^^^^^^^^^

Client errors are indicated by status codes of ``4xx``. The same API request cannot succeed at the current
time and most likely will not succeed at a later time. The client needs to change the request to have
a chance of successful completion.


Error responses
^^^^^^^^^^^^^^^

All error responses carry an error entity which has the following structure:

.. code-block:: js

	{	code: ...,
		reason: ...
	}

This is a JSON object with the fields:

=========== =========== =============================================
Fieldname	Format 		Usage
=========== =========== =============================================
code 		Number		Numerical error code representing the error.
reason 		String		A generic string representation of the error.
=========== =========== =============================================

The code field is a 3-digit number representing the error condition. In some cases
this number is from the range of HTTP status codes ``4xx`` and ``5xx``. When such a number
is found in the code field of the response, it is guaranteed to be identical with the 
http status code. The reason field contains the generic HTTP status string. For example, 
if the request entity could not be parsed as valid JSON, an HTTP status code of 
``422 - Unprocessable Entity`` is responded. In this case the response entity
would be:

.. code-block:: js

	{	code: 422,
		reason: "Unprocessable Entity"
	}	
..

In other cases the status code represents additional information to the HTTP status code to give the 
user a hint what went wrong. For instance if a field in the request entity is missing, the HTTP status code
would be ``400 - Bad Request``, but the response entity gives a further clue:

.. code-block:: js

	{	code: 601,
		reason: "BODY_FIELD_USERNAME_MISSING"
	}
..

Note that the reason field is no human readable form of the error message. It is a textual representation
of the error message which can be used by internationalization systems to provide a proper
translation for the actual error condition.






