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
package samplecode.modifydn;


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;


import java.util.EnumMap;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


@Author("terry.gardner@unboundid.com")
@Since("01-Nov-2011")
@CodeVersion("1.1")
final class ModDnExample
{

  private class Data
  {

    Data(final EnumMap<Key,Object> parameters)
    {

      deleteOldRdn = get(parameters,Key.DELETE_OLD_RDN,Boolean.class);

      newDn = get(parameters,Key.NEW_DN,String.class);

      if((newDn == null) || newDn.isEmpty())
      {
        throw new IllegalArgumentException();
      }

      newSuperiorDn = get(parameters,Key.NEW_SUPERIOR_DN,String.class);

      existingDn = get(parameters,Key.EXISTING_DN,String.class);

      if((existingDn == null) || existingDn.isEmpty())
      {
        throw new IllegalArgumentException();
      }

      hostname = get(parameters,Key.HOSTNAME,String.class);

      if((hostname == null) || hostname.isEmpty())
      {
        throw new IllegalArgumentException();
      }

      port = get(parameters,Key.PORT,Integer.class);

      if(port <= 0)
      {
        throw new IllegalArgumentException();
      }

      controls = get(parameters,Key.CONTROLS,Control[].class);
    }



    final Control[] controls;



    final boolean deleteOldRdn;



    final String existingDn;



    final String hostname;



    final String newDn;



    final String newSuperiorDn;



    final int port;
  }




  private enum Key
  {

    /**
     * The value associated with CONTROLS is an array of {@link Control}
     * that are included with the modify DN request.
     */
    CONTROLS,

    /**
     * The value associated with DELETE_OLD_RDN is Boolean, it's value
     * indicates whether the old RDN values should be deleted.
     */
    DELETE_OLD_RDN,EXISTING_DN,HOSTNAME,NEW_DN,NEW_SUPERIOR_DN,PORT;
  }



  private static ModDnExample instance = null;



  public static void main(final String... args)
  {

    final EnumMap<Key,Object> parameters = new EnumMap<Key,Object>(Key.class);

    parameters.put(Key.PORT,Integer.valueOf(1389));
    parameters.put(Key.DELETE_OLD_RDN,Boolean.TRUE);
    parameters.put(Key.EXISTING_DN,"uid=user.0,ou=people,dc=example,dc=com");
    parameters.put(Key.HOSTNAME,"slamd");
    parameters.put(Key.NEW_DN,"uid=user.0");
    parameters.put(Key.CONTROLS,new Control[]
    {
            new PreReadRequestControl("uid"), new PostReadRequestControl("uid")
    });

    final ModDnExample example = ModDnExample.getInstance();
    final LDAPResult result = example.modifyDn(parameters);
    if(result != null)
    {
      System.out.println("result: " + result);
    }
  }



  private static ModDnExample getInstance()
  {
    if(ModDnExample.instance == null)
    {
      ModDnExample.instance = new ModDnExample();
    }
    return ModDnExample.instance;
  }



  private <T> T get(final EnumMap<Key,Object> parameters,final Key key,final Class<T> cl)
  {
    T result = null;
    if((parameters != null) && (cl != null))
    {
      final Object o = parameters.get(key);
      if(o != null)
      {
        result = cl.cast(o);
      }
    }
    return result;
  }



  private LDAPResult modifyDn(final EnumMap<Key,Object> parameters)
  {
    if(parameters == null)
    {
      throw new NullPointerException();
    }

    final Data d = new Data(parameters);

    LDAPResult result;
    try
    {
      final LDAPConnection ldapConnection = new LDAPConnection(d.hostname,d.port);
      final ModifyDNRequest r =
              new ModifyDNRequest(d.existingDn,d.newDn,d.deleteOldRdn,d.newSuperiorDn,
                      d.controls);
      result = ldapConnection.modifyDN(r);
      ldapConnection.close();
    }
    catch(final LDAPException lex)
    {
      System.err.println(lex.getResultCode() + ": " + lex.getLocalizedMessage());
      result = null;
    }
    return result;
  }
}
