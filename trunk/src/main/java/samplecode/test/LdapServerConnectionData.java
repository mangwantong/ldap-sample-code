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
import java.util.Properties;


import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.CommandLineOptions;
import samplecode.PropertiesFile;
import samplecode.PropertiesFileNotFoundException;
import samplecode.PropertyNotFoundException;
import samplecode.Since;


/**
 * Storage for data needed to establish a connection to a directory
 * server.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 24, 2011")
@CodeVersion("1.1")
@NotMutable
class LdapServerConnectionData
{


  /**
   * The key to the bind DN property.
   */
  public static final String BIND_DN = CommandLineOptions.ARG_NAME_BIND_DN;


  /**
   * The key to the bind password property.
   */
  public static final String BIND_PASSWORD =
      CommandLineOptions.ARG_NAME_BIND_PASSWORD;


  /**
   * The key to the hostname property.
   */
  public static final String HOSTNAME = CommandLineOptions.ARG_NAME_HOSTNAME;


  /**
   * The key to the port property.
   */
  public static final String PORT = CommandLineOptions.ARG_NAME_PORT;


  /**
   * Delivers an {@code LdapServerConnectionData} object in which the
   * LDAp server connection data has been initialized with the contents
   * of a Java properties file.
   * <p>
   * <b>precondition</b>
   * <p>
   * propertiesFileName cannot be {@code null}, must be on the
   * CLASSSPATH, and must contain the following properties:
   * <ul>
   * <li>hostname</li>
   * <li>port</li>
   * <li>bindDn</li>
   * <li>bindPassword</li>
   * </ul>
   * <p>
   * <b>postcondition</b>
   * <p>
   * {@code LdapServerConnectionData} object initialized from the
   * contents of the specified {@code propertiesFileName} file.
   * <p>
   * <b>example properties file</b> <blockquote>
   * 
   * <pre>
   * $ cat commandLineOptions.properties
   * hostname = localhost
   * port = 1389
   * bindDn = cn=directory manager
   * bindPassword = 9sincje&*0
   * </pre>
   * 
   * </blockquote>
   * 
   * @param propertiesFileName
   * @return
   * @throws IOException
   * @throws PropertiesFileNotFoundException
   * @throws PropertyNotFoundException
   */
  static LdapServerConnectionData getConnectionDataFromPropertiesFile(
      final String propertiesFileName) throws IOException,
      PropertiesFileNotFoundException,PropertyNotFoundException
  {
    Validator.ensureNotNull(propertiesFileName);
    final PropertiesFile propertiesFile = PropertiesFile.of(propertiesFileName);
    final Properties properties = propertiesFile.getProperties();
    for(final String property : new String[]
    {
        LdapServerConnectionData.BIND_DN,
        LdapServerConnectionData.BIND_PASSWORD,
        LdapServerConnectionData.HOSTNAME,
        LdapServerConnectionData.PORT,
    })
    {
      if(properties.getProperty(property) == null)
      {
        final String exceptionMsg =
            String.format("property %s was not found in %s",property,
                propertiesFileName);
        throw new PropertyNotFoundException(exceptionMsg);
      }
    }
    final String bindDn =
        properties.getProperty(LdapServerConnectionData.BIND_DN);
    final String bindPassword =
        properties.getProperty(LdapServerConnectionData.BIND_PASSWORD);
    final String hostname =
        properties.getProperty(LdapServerConnectionData.HOSTNAME);
    final int port =
        Integer.parseInt(properties.getProperty(LdapServerConnectionData.PORT));
    return new LdapServerConnectionData(bindDn,bindPassword,hostname,port);
  }


  // The bind DN in the form of a string
  private final String bindDn;


  // The bind password
  private final String bindPassword;


  // the hostname
  private final String hostname;


  // the port
  private final int port;


  /**
   * Creates an immutable {@code LdapServerConnectionData} that will
   * house the data needed for connection to a directory server.
   * 
   * @param bindDn
   * @param bindPassword
   * @param hostname
   * @param port
   */
  LdapServerConnectionData(
      final String bindDn,final String bindPassword,final String hostname,
      final int port)
  {
    Validator.ensureNotNull(bindDn,bindPassword,hostname);
    this.bindDn = bindDn;
    this.bindPassword = bindPassword;
    this.hostname = hostname;
    this.port = port;
  }


  /**
   * @return the bindDn
   */
  public String getBindDn()
  {
    return bindDn;
  }


  /**
   * @return the bindPassword
   */
  public String getBindPassword()
  {
    return bindPassword;
  }


  /**
   * @return the hostname
   */
  public String getHostname()
  {
    return hostname;
  }


  /**
   * @return the port
   */
  public int getPort()
  {
    return port;
  }
}
