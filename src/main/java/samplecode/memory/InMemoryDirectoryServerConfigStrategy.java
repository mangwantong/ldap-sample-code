/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.memory;

import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import samplecode.config.AbstractConfigStrategy;


public class InMemoryDirectoryServerConfigStrategy
  extends AbstractConfigStrategy<InMemoryDirectoryServerConfig> {

  public static InMemoryDirectoryServerConfigStrategy
  newInstance(final DN[] dnArray,
              final int port) {
    return new InMemoryDirectoryServerConfigStrategy(dnArray,port);
  }



  private InMemoryDirectoryServerConfigStrategy(final DN[] dnArray, final int port) {
    if(dnArray == null) {
      throw new IllegalArgumentException("dns must not be null.");
    }
    this.dnArray = dnArray;
    this.port = port;
  }



  /**
   * creates a configuration
   */
  @Override
  public InMemoryDirectoryServerConfig createConfiguration() {
    InMemoryDirectoryServerConfig cfg;
    try {
      cfg = new InMemoryDirectoryServerConfig(dnArray);
      cfg.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("default",port));
    } catch(LDAPException e) {
      getLogger().fatal(e);
      return null;
    }
    return cfg;
  }



  private final DN[] dnArray;


  private final int port;
}
