/**
 * The tests use a properties file for information concerning how to connect to
 * directory server, and for other purposes. The name of the properties file is
 * {@code commandLineOptions.properties}.
 * <p>
 * <b>example contents of the {@code commandLineOptions.properties}
 * file:</b><blockquote>
 *
 * <pre>
 * #
 * # Properties used by the testing harness to connect to
 * # directory server.
 * #
 * # Supported keywords:
 * # hostname      - the hostname where the directory server runs.
 * # port          - the port upon which the directory server listens for connections.
 * # bindDn        - the distinguished name used to authenticate a connection.
 * # bindPassword  - the credentials of the bind DN.
 * #
 * samplecode.test.LdapServerConnectionData.hostname = ldap.example.com
 * samplecode.test.LdapServerConnectionData.port = 1389
 * samplecode.test.LdapServerConnectionData.bindDn = CN=RootDN
 * samplecode.test.LdapServerConnectionData.bindPassword = password
 * samplecode.test.LdapServerConnectionData.sizeLimit = 32
 * </pre>
 *
 * </blockquote>
 */

package samplecode.test;



