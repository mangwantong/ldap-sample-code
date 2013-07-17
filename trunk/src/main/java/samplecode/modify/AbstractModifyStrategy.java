/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.modify;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractModifyStrategy implements ModifyStrategy {

  private final LDAPConnection ldapConnection;



  protected LDAPConnection getLDAPConnection() {
    return this.ldapConnection;
  }



  public AbstractModifyStrategy(final LDAPConnection ldapConnection) {
    if(ldapConnection == null) {
      throw new IllegalArgumentException("ldapConnection must not be null.");
    }
    this.ldapConnection = ldapConnection;
    logger = LogFactory.getLog(getClass());
  }



  /**
   * updates the specified {@code attributeName} in the {@code dn} with the
   * specified {@code
   * attributeValue} specified {@code dn}
   *
   * @param dn
   *   the distinguished name of the entry to modify
   * @param attributeName
   *   the name of the attribute to modify
   * @param newAttributeValue
   *   the new attribute value
   */
  @Override
  public void modify(final DN dn, final String attributeName, final String newAttributeValue)
    throws ModifyException {
    throw new UnsupportedOperationException("not yet implemented");
  }



  /**
   * increments the value of the attribute in {@code dn} specified by {@code
   * attributeName}
   * by the specified {@code incrementValue}.
   *
   * @param dn
   *   the distinguished name of the entry to modify
   * @param attributeName
   *   the name of the attribute to modify; the attribute must be
   *   incrementable
   * @param incrementValue
   *   the increment
   */
  @Override
  public void modify(final DN dn, final String attributeName, final long incrementValue)
    throws ModifyException {
    throw new UnsupportedOperationException("not yet implemented");
  }



  private final Log logger;



  public Log getLogger() {
    // retrieve logging facility for AbstractModifyStrategy
    return logger;
  }

}
