/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.modify;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PostReadResponseControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadResponseControl;
import samplecode.ldap.SupportedFeature;

import java.util.ArrayList;
import java.util.List;


class IncrementModifyStrategy extends AbstractModifyStrategy {

  IncrementModifyStrategy(final LDAPConnection ldapConnection, final SearchScope scope,
                          final Filter filter) {
    super(ldapConnection);
    this.scope = scope;
    this.filter = filter;
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
    /*
    * Create the search request. The base object is the DN 'entryDn',
    * the scope and filter are taken from the command line arguments,
    * and the attribute is '1.1'. '1.1' is an OID that can never
    * match an attribute type, therefore no attributes are returned.
    */
    final String baseObject = dn.toString();
    final String[] requestedAttributes = new String[]{SearchRequest.NO_ATTRIBUTES};
    final SearchRequest searchRequest =
      new SearchRequest(baseObject,scope,filter,requestedAttributes);

    // Search for the entry specified by the entryDn.
    LDAPConnection ldapConnection = getLDAPConnection();
    try {
      final SearchResult searchResult = ldapConnection.search(searchRequest);
      if(searchResult.getEntryCount() == 0) {
        /*
        * NB: This will not be reached if the base object did not
        * exist.
        */
        return;
      }
    } catch(final LDAPException ldapException) {
      throw new ModifyException(ldapException);
    }

    /*
    * Create the modify request with the modify-increment extension.
    * This requires using the INCREMENT modification type and the
    * incrementValue specified on the command line.
    */
    final List<Modification> modifications = new ArrayList<Modification>();
    final Modification modification =
      new Modification(ModificationType.INCREMENT,attributeName,
        String.valueOf(incrementValue));
    modifications.add(modification);
    final ModifyRequest modifyRequest = new ModifyRequest(dn,modifications);

    /*
    * If the pre-read request control is supported by the server, add
    * the control to the modify request.
    */
    String controlOID = PreReadRequestControl.PRE_READ_REQUEST_OID;
    if(SupportedFeature.isControlSupported(ldapConnection,controlOID)) {

      /*
      * Create a pre-read request control to get the value of the
      * attribute before the modification; then add the control to
      * the modify request.
      */
      final boolean isCritical = true;
      final PreReadRequestControl control =
        new PreReadRequestControl(isCritical,attributeName);
      modifyRequest.addControl(control);
    }

    /*
    * If the post-read request control is supported by the server,
    * add the control to the modify request.
    */
    controlOID = PostReadRequestControl.POST_READ_REQUEST_OID;
    if(SupportedFeature.isControlSupported(ldapConnection,controlOID)) {

      /*
      * Create a post-read request control to get the value of the
      * attribute after the modification; then add the control to the
      * modify request.
      */
      final boolean isCritical = true;
      final PostReadRequestControl control =
        new PostReadRequestControl(isCritical,attributeName);
      modifyRequest.addControl(control);
    }

    /*
    * Transmit the modify request.
    */
    LDAPResult ldapResult;
    try {
      ldapResult = ldapConnection.modify(modifyRequest);
    } catch(LDAPException ldapException) {
      throw new ModifyException(ldapException);
    }

    /*
    * Check for the pre-read response control and display the value
    * of the attribute before the modification occurred.
    */
    PreReadResponseControl preReadResponseControl;
    try {
      preReadResponseControl = PreReadResponseControl.get(ldapResult);
    } catch(final LDAPException e) {
      throw new ModifyException(e);
    }

    if((preReadResponseControl != null) && preReadResponseControl.hasValue()) {
      final Entry entry = preReadResponseControl.getEntry();
      if(entry != null) {
        final Attribute attr = entry.getAttribute(attributeName);
        final StringBuilder builder = new StringBuilder();
        builder.append("Before modification the value of ");
        builder.append(attr.getBaseName());
        builder.append(" was ");
        builder.append(attr.getValue());
        builder.append(". The value of modify-increment is ");
        builder.append(incrementValue);
        builder.append(".");
        final String msg = builder.toString();
        System.out.println(msg);
      }
    }

    /*
    * Check for the post-read response control and display the value
    * of the attribute before the modification occurred.
    */
    PostReadResponseControl postReadResponseControl;
    try {
      postReadResponseControl = PostReadResponseControl.get(ldapResult);
    } catch(final LDAPException e) {
      throw new ModifyException(e);
    }
    if((postReadResponseControl != null) && postReadResponseControl.hasValue()) {
      final Entry entry = postReadResponseControl.getEntry();
      if(entry != null) {
        final Attribute attr = entry.getAttribute(attributeName);
        final StringBuilder builder = new StringBuilder();
        builder.append("After modification the value of ");
        builder.append(attr.getBaseName());
        builder.append(" is ");
        builder.append(attr.getValue());
        builder.append(". The value of modify-increment is ");
        builder.append(incrementValue);
        builder.append(".");
        final String msg = builder.toString();
        System.out.println(msg);
      }
    }
  }



  private final Filter filter;


  private final SearchScope scope;

}
