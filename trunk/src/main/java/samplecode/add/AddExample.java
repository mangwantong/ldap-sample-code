package samplecode.add;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.migrate.ldapjdk.LDAPSearchResults;
import com.unboundid.ldif.LDIFException;

/**
 * demonstrate the {@link AddRequest}; this example uses a hard coded hostname of {@code
 * ldap-server.ldap.com} and port {@code 389} and attempts to add {@code uid=user,
 * dc=example,dc=com}.
 */
public final class AddExample {

    /**
     * The host where the LDAP Directory Server listens for client connections
     */
    private static final String HOSTNAME = "ldap-server.ldap.com";

    /**
     * The port on which the server at {@code HOSTNAME} listens for client connections
     */
    private static final int PORT = 389;

    /** The maximum number of milliseconds to wait before abandoning the AddRequest */
    private static final int OPERATION_TIMEOUT_MILLIS = 1000;

    /** The LDIF that comprises the entry to add to the server. */
    private static final String[] ldifLines =
            {"dn: uid=user,dc=example,dc=com",
                    "objectClass: top",
                    "objectClass: person",
                    "changetype: add",
                    "cn: Joe User",
                    "sn: User",
                    "uid: user",
                    "userPassword: password"};

    /**
     * demonstrate the {@link AddRequest}
     */
    public static void main(final String... args) {

        // Use connection options to specify that the connection attempt
        // should be 1 second and if the ADD request times out, the request
        // should be abandoned.
        final LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
        connectionOptions.setAbandonOnTimeout(true);
        connectionOptions.setConnectTimeoutMillis(AddExample.OPERATION_TIMEOUT_MILLIS);

        final String host = AddExample.HOSTNAME;
        final int port = AddExample.PORT;
        int result;
        LDAPResult ldapResult = null;
        try {

            // Connect to the server.
            final LDAPConnection ldapConnection =
                    new LDAPConnection(connectionOptions,host,port);
            try {

                // Create the AddRequest object using the LDIF lines.
                final AddRequest addRequest = new AddRequest(AddExample.ldifLines);

                // Transmit the AddRequest to the server.
                ldapResult = ldapConnection.add(addRequest);

                System.out.println(ldapResult);
            } catch(final LDIFException e) {
                System.err.println(e);
            } finally {
                ldapConnection.close();

                // Convert the result code to an integer for use in the exit method.
                result = ldapResult == null ? 1 : ldapResult.getResultCode().intValue();
            }
        } catch(final LDAPException e) {
            System.err.println(e);
            result = 1;
        }

        System.exit(result);

    }

}
