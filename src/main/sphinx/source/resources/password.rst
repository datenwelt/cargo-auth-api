/password
=========

	This resource deals with the password resetting for registered users. If a user needs to recover
	his or her password, a recovery mail is sent to the user with a reset token. The reset token
	can be used with the ``POST`` endpoint to set a new password.

	Password reset tokens have a validity of 24 hours. Use the GET endpoint to create a new token
	in cases where the user lost his token or did not manage to reset his password in time.

GET
^^^
	
	This endpoint sends a new password recovery e-mail to the user. 

Request
"""""""

	The request must contain a query parameter ``username`` with the e-mail address of the
	user to send the recocery link to.

	Additionally, the request must contain a referrer header with an URI path ending in ``password.html``.

	The activation link in the e-mail is created by stripping everything after "password.html" from the referrer. 
	Finally the recovery token is appended as the fragment part.

	Example:
		
		"http://xyz.de/somewhere/password.html?some_other_stuff" => "http://xyz.de/somewhere/password.html#TOKEN"


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

	This endpoint checks the password recovery token and sets a new password for the user.

Request
"""""""

	The request entity has the following fields:

	=========== ======================= =========== ==================================
	Query       Format                              Usage
	=========== ======================= =========== ==================================
	token       String                  required	The activation token to check.
	password    String ``> 6 chars``    required	The new password for the user.
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
	602  REQUEST_ENTITY_FIELD_PASSWORD_MISSING  Field "token" is missing.
	701  TOKEN_UNKNOWN                          The token is unknown.
	702  TOKEN_EXPIRED                          The token has exceeded its validity.
	==== ====================================== ================================================================

