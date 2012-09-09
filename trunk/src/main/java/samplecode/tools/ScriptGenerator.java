/*
 * Copyright 2012 UnboundID Corp. All Rights Reserved.
 */
package samplecode.tools;

import java.io.File;
import java.io.IOException;

/**
 * root of the script generator hierarchy
 */
public interface ScriptGenerator
{
  File generateScript() throws IOException;
}
