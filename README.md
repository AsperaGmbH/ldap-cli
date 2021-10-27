# ldap-cli

The application ldapcli is a command line interface (CLI) application.

###  List of commands:

        (sp)ssl-ping hostname:port_number   Do a ssl protocol ping
        (lct)ldap-connection-test           Do a LDAP connection with known parameters
        (lsc)ldap-search-command *|PATTERN  Do a LDAP search and out for all ('*') or username
        (q)uit:                             Quit the program.
        command options mandatory:          Command parameters without brackets are mandatory
        command options optional:           Command parameters inside brackets are optional
        (h)elp:                             Print this!

### Requirements:

- Java 11.x (OpenJDK)
- Apache Maven 3.0.5 or higher
- All properties files should be formatted as utf-8 without BOM (byte order mark)

###  Install and run:

- Clone the project
- Build the project with maven
- Start application with: java -jar target/ldapcli-x.x.x-XYZ-jar-with-dependencies.jar
