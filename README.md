# Authentication API

This is the Java project for a RESTful authentication API.

## Configuration

The API needs two configuration files to operate - `mail.auth.ini` and `db.auth.ini`. 
The first is to support sending mails through an external SMTP server, the second
one configures the database connection. The configuration files are expected
to be at `/etc/datenwelt`.

Reasonable defaults for both files are provided within the source folder of the 
project at `src/main/resources/defaults`. To create the configuration files,
copy the defaults files from the source folder to `/etc/datenwelt` and adjust
them to your needs.

### Mail configuration

For outgoing mail an external SMTP server is required. To configure the SMTP
parameters put the following contents in the file `/etc/datenwelt/mail.auth.ini`:

    smtp_hostname=smtp.gmail.com
    smtp_protocol=SSL
    smtp_port=465
    smtp_username=someone@gmail.com
    smtp_password=xxxx
    smtp_sender=someone@gmail.com

### Database configuration

For the database connection, create the file `/etc/datenwelt/db.auth.ini` with
the following contents and adjust them to your own settings:

    database=datenwelt_auth
    hostname=localhost
    username=datenwelt
    password=xxx

## Base URI

The path of the API servlet is defined in `web.xml` as `v1/api/*`. The base URI
is dependend from your WAR deployment option. Usually, if you deploy the
WAR in your container as "datenwelt-auth-api", the base URI is:

    http://your_server/datenwelt-auth-api/v1/api/

The base URI should return an empty document with an HTTP status code of <b>200</b>.

## API documentation

Additionally, an API documentation is served by default from the servlet from `v1/doc/*`.
In the example above this would be:

    http://your_server/datenwelt-auth-api/v1/doc/

## General project structure

This is a WAR project built with maven. It provides a Servlet which is based
on the <b>io.datenwelt:datenwelt-api-java</b> project. The resulting WAR file can be
deployed in any Servlet container including Tomcat 7 not excluding other
containers. The Java runtime is JRE 1.8 because the project makes use of
[Optionals](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
 and [Lambdas](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html).

### Java sources

The general entry point is the Servlet behind `io.datenwelt.auth.api.AuthAPI.class` 
which initializes some static resources from `io.datenwelt.auth.api.utils.*` and
registers available RESTful endpoints from `io.datenwelt.auth.api.endpoints.*`.

### Database structure

A MySQL database is used for data persistence. The corresponding database structure
can be found in the resources directory at `src/main/resources/db`.

### Configuration

The WAR runtime depends on external configuration files provided under `/etc/datenwelt`.
<b>io.datenwelt:datenwelt-config-java</b> is used as a configuration framework.


### API documentation build process

The documentation is maintained through the Sphinx documentation tool from 
[http://www.sphinx-doc.org](http://www.sphinx-doc.org). The sources for the 
documentation are kept in  `src/main/sphinx`. Use `make html` in this directory 
to build the documentation manually.

    cd src/main/sphinx
    make clean      # Optionally clean old documentation files.
    make html

To place the documentation into the final WAR, create a JAR file at the webapp
resources directory under `src/main/webapp/WEB-INF/lib`. The JAR file must contain
the contents of the directory `src/sphinx/build/html`:

    jar -C src/sphinx/build/html -cvf src/main/webapp/WEB-INF/lib/documentation

This technique of serving static content from a servlet is widely known as
<b>"WEB-INF/lib/{\*.jar}/META-INF/resources"</b> as described 
[here](https://alexismp.wordpress.com/2010/04/28/web-inflib-jarmeta-infresources/) 
for example.

The documentation build process is part of the maven "site" lifecycle and
fully automated. Note that the project won't build in the maven "site" lifecycle
without a Sphinx installation. The command `sphinx-build` is needed within one 
of the directories `/bin`, `/usr/bin` and `/usr/local/bin`.



