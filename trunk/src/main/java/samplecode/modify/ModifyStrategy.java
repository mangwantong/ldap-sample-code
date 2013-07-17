/*
 * Copyright 2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.modify;

import com.unboundid.ldap.sdk.DN;


/**
 * provides definitions of methods for clients
 * which modify LDAP entries.
 */
public interface ModifyStrategy {

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
  void modify(DN dn, String attributeName, String newAttributeValue) throws ModifyException;

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
  void modify(DN dn, String attributeName, long incrementValue) throws ModifyException;
}
