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



import org.junit.After;
import org.junit.Before;
import org.junit.Test;



import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.unboundidds.controls.AttributeRight;



import samplecode.Author;
import samplecode.CheckEffectiveRights;
import samplecode.CheckEffectiveRightsException;
import samplecode.CodeVersion;
import samplecode.Since;
import samplecode.SupportedFeatureException;



/**
 * tests for effective rights using the {@code GetEffectiveRightsRequestControl}
 * . *
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
public final class CheckEffectiveRightsTest {

	// connection to directory server.
	private LDAPConnection ldapConnection;



	/**
	 * Close the connection to the server.
	 */
	@After
	public void closeConnection() {
		LdapConnectionUtils.closeConnection(this.ldapConnection);
	}



	/**
	 * Get connection to the server.
	 * 
	 * @throws LDAPException
	 */
	@Before
	public void getConnection() throws LDAPException {
		this.ldapConnection = LdapConnectionUtils.getConnection();
	}



	/**
	 * tests all AttributeRights for an authZid for a series of attributes.
	 * 
	 * @throws LDAPSearchException
	 * @throws LDAPException
	 * @throws CheckEffectiveRightsException
	 * @throws SupportedFeatureException
	 */
	@Test
	public void testCheckEffectiveRights() throws LDAPSearchException, LDAPException,
			CheckEffectiveRightsException, SupportedFeatureException {
		for (final String attributeName : getAttributeNames()) {
			for (final AttributeRight attributeRight : getAttributeRights()) {
				new CheckEffectiveRights(this.ldapConnection).hasRight(getSearchRequest(),
						attributeName, attributeRight, getAuthZId());
			}
		}
	}



	private String[] getAttributeNames() {
		return new String[] { "cn", "uid", "userPassword", "mail" };
	}



	private AttributeRight[] getAttributeRights() {
		return AttributeRight.values();
	}



	private String getAuthZId() {
		return "dn: uid=user.0,ou=people,dc=example,dc=com";
	}



	private String getBaseObject() {
		return "uid=user.0,ou=people,dc=example,dc=com";
	}



	private Filter getFilter() throws LDAPException {
		return Filter.create("(objectClass=*)");
	}



	private SearchScope getScope() {
		return SearchScope.BASE;
	}



	private SearchRequest getSearchRequest() throws LDAPException {
		return new SearchRequest(getBaseObject(), getScope(), getFilter(), "*", "+");
	}
}
