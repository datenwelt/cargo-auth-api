/activate
=========

	This resource deals with the activation of a registered user. Upon successful registration, the
	user data object is created in the system but the user is not able to login yet. He must
	activate his account by confirming his e-mail address. An e-mail is sent to his e-mail address
	with an activation link. That link contains an activation token which can be used with this
	resource to activate his account.

	Activation tokens have a validity of 24 hours. Use the GET endpoint to create a new activation token
	in cases where the user lost his token or did not manage to activate his account in time.

GET
^^^
	
	This endpoint sends a new activation e-mail to the user. 

Request
"""""""

	The request must contain a query parameter ``username`` with the e-mail address of the
	user to send the activation link to.

	Additionally, the request must contain a referrer header with an URI path ending in ``register.html``. Most
	browsers send the URI of the current page in the ``Referer`` header. Make sure the registration
	page is accessed under ``/somewhere/register.html`` and provide a corresponding ``activate.html``.

	The activation link in the e-mail is created by stripping "register.html" from the referrer and
	replacing it by "activate.html". Finally the activation token is appended as the fragment part.

	Example:
		
		"http://xyz.de/somewhere/register.html" => "http://xyz.de/somewhere/activate.html#TOKEN"


	=========== ======================= =========== ==================================
	Query       Format                              Usage
	=========== ======================= =========== ==================================
	username    String                  required	The username to send the link for.
	=========== ======================= =========== ==================================

Responses
"""""""""

	If the request is valid, a generic HTTP status code of ``200`` is responded. This means
	that a successful completion is reported even if the user does not exist.

	One of the following endpoint  specific errors may occur:

	==== ====================================== ================================================================
	Code Reason                                 Description
	==== ====================================== ================================================================
	601  QUERY_USERNAME_MISSING                 Query parameter "username" is missing.
	610  HEADER_REFERER_IS_MISSING	            "Referer" header is missing.
	611  HEADER_REFERER_WRONG_FORMAT            URI path of "Referer" header does not end in "register.html"
	==== ====================================== ================================================================


POST
^^^^

	This endpoint checks the activation token and activates the corresponding user.

Request
"""""""

	The request entity has the following fields:

	=========== ======================= =========== ==================================
	Query       Format                              Usage
	=========== ======================= =========== ==================================
	token       String                  required	The activation token to check.
	=========== ======================= =========== ==================================

Responses
"""""""""

	If the request is successful, a generic HTTP status code of ``200`` is responded. 

	One of the following endpoint  specific errors may occur:

	==== ====================================== ================================================================
	Code Reason                                 Description
	==== ====================================== ================================================================
	600  REQUEST_ENTITY_MISSING                 The request has not sent an entity in its body.
	601  REQUEST_ENTITY_FIELD_TOKEN_MISSING     Field "token" is missing.
	701  TOKEN_UNKNOWN                          The token is unknown.
	702  TOKEN_EXPIRED                          The token has exceeded its validity.
	==== ====================================== ================================================================
