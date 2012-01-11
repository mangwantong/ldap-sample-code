/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 */
package samplecode.test;



import java.util.logging.Formatter;



import com.unboundid.util.MinimalLogFormatter;



import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;



/**
 * Only static methods in this class. *
 * <p>
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
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.0")
public enum DataUsedForTestingPurposesOnly {
        ;

        /**
         * LDAP server connection parameter properties file
         */
        public static final String LDAP_SERVER_CONNECTION_DATA_PROPS_FILE
          = "commandLineOptions.properties";



        private static final Formatter formatter = new MinimalLogFormatter();



        // Supports LDAP server connection parameters
        private static LdapServerConnectionData ldapServerConnectionData;



        // A sample LDIF entry
        private static final String[] ldif = new String[] {
                        "dn: " + DataUsedForTestingPurposesOnly.TEST_DN, "objectClass: top",
                        "objectClass: inetOrgPerson", "cn: deleteme", "sn: deleteme-sn",
                        "description: a test entry." };



        private static final String TEST_DN = "cn=deleteme,dc=example,dc=com";



        /**
         * @return the formatter
         */
        public static Formatter getFormatter() {
                return DataUsedForTestingPurposesOnly.formatter;
        }



        /**
         * @return the ldapServerConnectionData
         */
        public static LdapServerConnectionData getLdapServerConnectionData() {
                return DataUsedForTestingPurposesOnly.ldapServerConnectionData;
        }



        /**
         * @return the ldapServerConnectionDataPropsFile
         */
        public static String getLdapServerConnectionDataPropsFile() {
                return DataUsedForTestingPurposesOnly.LDAP_SERVER_CONNECTION_DATA_PROPS_FILE;
        }



        /**
         * @return the ldif
         */
        public static String[] getLdif() {
                return DataUsedForTestingPurposesOnly.ldif;
        }



        /**
         * @return the testDn
         */
        public static String getTestDn() {
                return DataUsedForTestingPurposesOnly.TEST_DN;
        }



        /**
         * @param ldapServerConnectionData
         *          the ldapServerConnectionData to set
         */
        public static void setLdapServerConnectionData(
                        final LdapServerConnectionData ldapServerConnectionData) {
                DataUsedForTestingPurposesOnly.ldapServerConnectionData = ldapServerConnectionData;
        }



        /*
         * Load the LDAP server connection parameter from the properties file into a
         * field for access by other classes. When an exception is thrown, the program
         * terminates.
         */
        static {
                try {
                        final String filename = DataUsedForTestingPurposesOnly
                                        .getLdapServerConnectionDataPropsFile();
                        DataUsedForTestingPurposesOnly.setLdapServerConnectionData(LdapServerConnectionData
                                        .getConnectionDataFromPropertiesFile(filename));
                } catch (final Exception exception) {
                        exception.printStackTrace();
                        System.exit(1);
                }
        }
}
