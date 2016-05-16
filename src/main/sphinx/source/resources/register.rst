/register
=========

	This resource registers new users with the authentication system and starts the registration process.
	When a new user is registered, a data record is created but the user is not able to login yet. To
	activate the user account for logins, the user receives an activation token by e-mail which can be used
	with the ``/activate`` resource. 

POST
^^^^

	This endpoint registers a new user with the system. On successful completion the user receives an
	e-mail containing an activation link. This link contains a unique token which can be used with the
	``/activate`` resource.

Request
"""""""

	The request must contain a referrer header with an URI path ending in ``register.html``. Most
	browsers send the URI of the current page in the ``Referer`` header. Make sure the registration
	page is accessed under ``/somewhere/register.html`` and provide a corresponding ``activate.html``.

	The activation link in the e-mail is created by stripping "register.html" from the referrer and
	replacing it by "activate.html". Finally the activation token is appended as the fragment part.

	Example:
		
		"http://xyz.de/somewhere/register.html" => "http://xyz.de/somewhere/activate.html#TOKEN"

	The request entity has the following fields:

	=========== ======================= =========== ==============================
	Query       Format                              Usage
	=========== ======================= =========== ==============================
	firstname   String                  required	First name of the user.
	lastname    String                  required	Last name of the user.
	email       String ``___@___.___``  required	E-mail address of the user.
	password    String ``> 6 chars``    required	Password for the user.
	=========== ======================= =========== ==============================

Responses
"""""""""

	A successful response is indicated by a status code of ``200`` and has no response entity. 
	One of the following endpoint specific errors may occur:

	==== ====================================== ================================================================
	Code Reason                                 Description
	==== ====================================== ================================================================
	600  REQUEST_ENTITY_MISSING                 The request has not sent an entity in its body.
	601  REQUEST_ENTITY_FIELD_FIRSTNAME_MISSING Field "firstname" is missing.
	602  REQUEST_ENTITY_FIELD_LASTNAME_MISSING  Field "lastname" is missing.
	603  REQUEST_ENTITY_FIELD_EMAIL_MISSING     Field "email" is missing.
	604  REQUEST_ENTITY_FIELD_PASSWORD_MISSING  Field "password" is missing.
	610  HEADER_REFERER_IS_MISSING	            "Referer" header is missing.
	611  HEADER_REFERER_WRONG_FORMAT            URI path of "Referer" header does not end in "register.html"
	701  USER_ALREADY_REGISTERED                The user with the provided e-mail address is already registered.
	==== ====================================== ================================================================
