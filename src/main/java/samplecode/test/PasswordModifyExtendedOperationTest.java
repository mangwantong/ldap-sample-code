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


import java.io.IOException;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.PasswordModifyExtendedOperationFailedException;
import samplecode.PropertiesFile;
import samplecode.PropertiesFileNotFoundException;
import samplecode.Since;
import samplecode.SupportedFeatureException;
import samplecode.password.ChangePassword;


/**
 * test the password modify extended operation support class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.0")
public final class PasswordModifyExtendedOperationTest
{


  // the entry whose password will be changed.
  private String entryDn;


  // existing (current) password of the entry DN
  private String existingPassword;


  // connection to directory server.
  private LDAPConnection ldapConnection;


  // new password of the entry DN
  private String newPassword;


  /**
   * Close the connection to the server.
   */
  @After
  public void closeConnection()
  {
    LdapConnectionUtils.closeConnection(this.ldapConnection);
  }


  /**
   * Get connection to the server.
   * 
   * @throws LDAPException
   */
  @Before
  public void getConnection() throws LDAPException
  {
    this.ldapConnection = LdapConnectionUtils.getConnection();
  }


  /**
   * load data from properties file.
   * 
   * @throws IOException
   * @throws PropertiesFileNotFoundException
   */
  @Before
  public void getProperties() throws IOException,
      PropertiesFileNotFoundException
  {
    final PropertiesFile indexPackagePropertiesFile =
        PropertiesFile.of(TestUtils
            .getIndexPackagePropertiesResourceName());
    this.entryDn =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getEntryDnKey());
    this.existingPassword =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getExistingPasswordKey());
    this.newPassword =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getNewPasswordKey());
    Validator
        .ensureNotNull(this.entryDn,this.existingPassword,this.newPassword);
  }


  /**
   * test changing the password.
   * 
   * @throws LDAPException
   * @throws SupportedFeatureException
   * @throws PasswordModifyExtendedOperationFailedException
   */
  @Test
  public void testPasswordModifyExtendedOperation() throws LDAPException,
      SupportedFeatureException,PasswordModifyExtendedOperationFailedException
  {
    final DN distinguishedName = new DN(this.entryDn);
    final ChangePassword passwordChanger =
        ChangePassword.newChangePassword(this.ldapConnection);
    final int responseTimeout = 10000;


    /*
     * Attempt to change the password of the entry DN named in the
     * properties file from the existing password to the new password.
     */
    passwordChanger.changePassword(distinguishedName,this.existingPassword,
        this.newPassword,responseTimeout);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return "PasswordModifyExtendedOperationTest [" +
        (this.entryDn != null ? "entryDn=" + this.entryDn + ", " : "") +
        (this.existingPassword != null ? "existingPassword=" +
            this.existingPassword + ", " : "") +
        (this.ldapConnection != null ? "ldapConnection=" + this.ldapConnection +
            ", " : "") +
        (this.newPassword != null ? "newPassword=" + this.newPassword : "") +
        "]";
  }
}
