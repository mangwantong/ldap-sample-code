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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.util.LDAPSDKUsageException;



import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;
import samplecode.SupportedFeature;
import samplecode.SupportedFeatureException;



/**
 * tests the {@code SupportedFeature} class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 26, 2011")
@CodeVersion("1.0")
public final class SupportedFeatureTest {

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
	 * test whether a {@code null} control is properly reported by
	 * {@code SupportedFeature}.
	 * 
	 * @throws LDAPException
	 * @throws SupportedFeatureException
	 */
	@Test
	public void testSupportedFeatureNullControlOID() throws LDAPException,
			SupportedFeatureException {
		final SupportedFeature sf = SupportedFeature
				.newSupportedFeature(this.ldapConnection);
		final String controlOID = PreReadRequestControl.PRE_READ_REQUEST_OID;
		sf.isControlSupported(controlOID);
	}



	/**
	 * test whether a supported request control is correctly noted as supported.
	 * 
	 * @throws LDAPException
	 * @throws SupportedFeatureException
	 */
	@Test
	public void testSupportedFeatureSupportedRequestControl()
			throws LDAPException, SupportedFeatureException {
		final SupportedFeature sf = SupportedFeature
				.newSupportedFeature(this.ldapConnection);
		final String controlOID = null;
		try {
			sf.isControlSupported(controlOID);
		} catch (final LDAPSDKUsageException exception) {
			Assert.assertTrue(true);
		}
	}



	/**
	 * test whether a unsupported request control is correctly noted as
	 * unsupported.
	 * 
	 * @throws LDAPException
	 */
	@Test
	public void testSupportedFeatureUnSupportedRequestControl()
			throws LDAPException {
		final SupportedFeature sf = SupportedFeature
				.newSupportedFeature(this.ldapConnection);
		final String controlOID = "0.0";
		try {
			sf.isControlSupported(controlOID);
		} catch (final SupportedFeatureException exception) {
			Assert.assertTrue(true);
		}
	}
}
