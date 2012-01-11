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


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.PropertiesFile;
import samplecode.Since;


/**
 * Gets properties used in the testing harnesses.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 29, 2011")
@CodeVersion("1.0")
class TestProperties
{


  private String backendId;


  private String baseDn;


  private String databaseName;


  private final String dbtestPath;


  private int numThreads;


  TestProperties()
  {
    final PropertiesFile indexPackagePropertiesFile =
        PropertiesFile.of(TestUtils.getIndexPackagePropertiesResourceName());
    baseDn =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getBaseDnKeyName());
    backendId =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getBackendIdKeyName());
    databaseName =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getDatabaseNameKeyName());
    dbtestPath =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getDbtestPathKeyName());
    try
    {
      numThreads =
          Integer.parseInt(indexPackagePropertiesFile.getProperties()
              .getProperty(TestUtils.getNumThreadsKeyName()));
    }
    catch (final Exception e)
    {
      numThreads = 1;
    }
    if(numThreads <= 0)
    {
      numThreads = 1;
    }
    if(baseDn == null)
    {
      baseDn = "dc=example,dc=com";
    }
    if(backendId == null)
    {
      backendId = "userRoot";
    }
    if(databaseName == null)
    {
      databaseName = "cn.equality";
    }
    if(dbtestPath == null)
    {
      throw new IllegalStateException(
          "no dbtestPath provided and no default is available.");
    }
  }


  /**
   * @return the backendId
   */
  public final String getBackendId()
  {
    return backendId;
  }


  /**
   * @return the baseDn
   */
  public final String getBaseDn()
  {
    return baseDn;
  }


  /**
   * @return the databaseName
   */
  public final String getDatabaseName()
  {
    return databaseName;
  }


  /**
   * @return the dbtestPath
   */
  public final String getDbtestPath()
  {
    return dbtestPath;
  }


  /**
   * @return the numThreads
   */
  public final int getNumThreads()
  {
    return numThreads;
  }
}
