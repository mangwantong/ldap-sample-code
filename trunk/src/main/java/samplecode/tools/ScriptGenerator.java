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

package samplecode.tools;

import java.io.File;
import java.io.IOException;


/**
 * root of the script generator hierarchy
 */
public interface ScriptGenerator {

  /**
   * Generates a script which is used to execute one of the demo tools
   * and returns a {@code File} which refers to the generated script.
   */
  File generateScript() throws IOException;

  /**
   * Retrieves the name of the file produced.
   */
  String getFilename();
}
