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
package samplecode;


import java.util.ArrayList;
import java.util.List;


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PostReadResponseControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadResponseControl;
import com.unboundid.util.Validator;


/**
 * An example of how to replace values of multi-valued attributes.
 */
@Author("terry.gardner@unboundid.com")
@Since("01-Nov-2011")
@CodeVersion("1.1")
final class ReplaceValue {


  /**
   * @param ldapConnectionPool
   *          Provides services applicable to a pool of connections to a
   *          directory server. This object may not be {@code null}.
   * @param entry
   *          The entry that will be the target of modify requests.
   * @return A new and distinct ReplaceValue object.
   */
  static ReplaceValue newReplaceValue(
      final LDAPConnectionPool ldapConnectionPool,final String entry) {
    Validator.ensureNotNull(ldapConnectionPool);
    return new ReplaceValue(ldapConnectionPool,entry);
  }


  private ReplaceValue(
      final LDAPConnectionPool ldapConnectionPool,final String entry) {
    this.ldapConnectionPool = ldapConnectionPool;
    this.entry = entry;
  }


  /**
   * Adds a description attribute with two values.
   */
  void addDescriptionValues() {
    final List<Modification> mods = new ArrayList<Modification>();
    mods.add(new Modification(ModificationType.ADD,"description",
        "description 1"));
    mods.add(new Modification(ModificationType.ADD,"description",
        "description 2"));
    final ModifyRequest modifyRequest = new ModifyRequest(entry,mods);
    modifyRequest.addControl(new PreReadRequestControl("description"));
    modifyRequest.addControl(new PostReadRequestControl("description"));

    try {
      final LDAPResult result = ldapConnectionPool.modify(modifyRequest);
      if(result
          .hasResponseControl(PreReadResponseControl.PRE_READ_RESPONSE_OID)) {
        final Control c =
            result
                .getResponseControl(PreReadResponseControl.PRE_READ_RESPONSE_OID);
        final ReadOnlyEntry e = ((PreReadResponseControl)c).getEntry();
        System.out.println("the entry pre-modify:" + e);
      }
      if(result
          .hasResponseControl(PostReadResponseControl.POST_READ_RESPONSE_OID)) {
        final Control c =
            result
                .getResponseControl(PostReadResponseControl.POST_READ_RESPONSE_OID);
        final ReadOnlyEntry e = ((PostReadResponseControl)c).getEntry();
        System.out.println("the entry post-modify:" + e);
      }
    } catch (final LDAPException lex) {
      lex.printStackTrace();
    }
  }


  /**
   * Replace 'description 1' with 'description 3'.
   */
  void replaceDescriptionValues() {
    final List<Modification> mods = new ArrayList<Modification>();
    mods.add(new Modification(ModificationType.REPLACE,"description",
        "description 1","description 3"));
    final ModifyRequest modifyRequest = new ModifyRequest(entry,mods);
    modifyRequest.addControl(new PreReadRequestControl("description"));
    modifyRequest.addControl(new PostReadRequestControl("description"));

    try {
      final LDAPResult result = ldapConnectionPool.modify(modifyRequest);
      if(result
          .hasResponseControl(PreReadResponseControl.PRE_READ_RESPONSE_OID)) {
        final Control c =
            result
                .getResponseControl(PreReadResponseControl.PRE_READ_RESPONSE_OID);
        final ReadOnlyEntry e = ((PreReadResponseControl)c).getEntry();
        System.out.println("the entry pre-modify:" + e);
      }
      if(result
          .hasResponseControl(PostReadResponseControl.POST_READ_RESPONSE_OID)) {
        final Control c =
            result
                .getResponseControl(PostReadResponseControl.POST_READ_RESPONSE_OID);
        final ReadOnlyEntry e = ((PostReadResponseControl)c).getEntry();
        System.out.println("the entry post-modify:" + e);
      }
    } catch (final LDAPException lex) {
      lex.printStackTrace();
    }


  }


  private final String entry;


  private final LDAPConnectionPool ldapConnectionPool;

}
