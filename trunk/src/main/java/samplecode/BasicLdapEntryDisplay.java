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


import com.unboundid.ldap.sdk.Entry;


/**
 * Minimal implementation of {@code LdapEntryDisplay}. All output is
 * sent to the {@code stdout}
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 29, 2011")
@CodeVersion("1.2")
class BasicLdapEntryDisplay
  implements LdapEntryDisplay {


  private final Entry entry;


  @Override
  public void display() {
    System.out.println(toString());
  }


  @Override
  public String toString() {
    return "BasicLdapEntryDisplay [" + (entry != null ? "entry=" + entry : "") +
        "]";
  }


  BasicLdapEntryDisplay(
      final Entry entry) {
    if(entry == null) {
      throw new NullPointerException("Entry cannot be null.");
    }
    this.entry = entry;
  }
}
